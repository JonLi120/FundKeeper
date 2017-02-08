package com.jonli.fundkeeper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Li on 2016/11/28.
 **/

public class NewsContent extends Fragment{

    private View v;
    private LinearLayout layout;
    private DBHelper myDBHelper;
    private SQLiteDatabase db;
    private TextView newscontent_title,newscontent_datetime,newscontent_text;
    private ImageView newscontent_img;

    private String url,imgurl,txt="",title,datetime;
    private int item_pos,tab_pos;
    private String[] tab_name = new String[]{"頭條新聞","基金焦點","外匯市場","國際股市"};
    private String[] db_name = new String[]{"InstantNews","FundNews","FXNews","StockNews"};
    private Bitmap bmp;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.newscontent,container,false);
        initView();
        return v;
    }

    private void initView(){
        layout = (LinearLayout) v.findViewById(R.id.news_content_layout);
        newscontent_title = (TextView) v.findViewById(R.id.newscontent_title);
        newscontent_datetime = (TextView) v.findViewById(R.id.newscontent_datetime);
        newscontent_text = (TextView) v.findViewById(R.id.newscontent_text);
        newscontent_img = (ImageView) v.findViewById(R.id.newscontent_img);


        Bundle bundle = getArguments();
        tab_pos = bundle.getInt("tab pos");
        item_pos = bundle.getInt("item pos");
    }

    @Override
    public void onStart() {
        super.onStart();
        ((HomeMainActivity)getActivity()).setHomeBack();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(tab_name[tab_pos-1]);

        myDBHelper = new DBHelper(getActivity());
        db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+db_name[tab_pos-1]+" WHERE id = ?",new String[]{item_pos+""});
        cursor.moveToFirst();
        url = cursor.getString(3);
        new Thread(runnable).start();
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Document doc = Jsoup.connect(url).timeout(10000).get();
                Log.e("Debug",url);
                Elements div = doc.select("div");

                for (int i = 0 ;i<div.size();i++)
                    Log.e("News Debug","i = "+i+" "+div.get(i).text());

                title = div.get(76).text();
//                Elements date = div.get(76).select("span#ctl00_ctl00_MainContent_Contents_lbTitle");
//                datetime = date.get(0).text();
                Elements p = div.get(82).select("P");
                if (p.size() <= 1){
                    Elements a = div.get(82).select("article");
                    txt = a.get(0).text();
                }else{
                    for (int i = 1 ; i < p.size() ; i++){
                        txt = txt + p.get(i).text()+"\n\n";
                    }
                }
                Elements img = div.get(82).select("IMG");
                if (img.size() != 0){
                    imgurl = img.get(0).attr("src");
                    bmp = getImgUrl(imgurl);
                }else{
                    imgurl = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            newscontent_title.setText(title);
            newscontent_datetime.setText(datetime);
            newscontent_text.setText(txt);
            if (imgurl.equals("")){
                newscontent_img.setVisibility(View.GONE);
            }else{
                newscontent_img.setImageBitmap(bmp);
            }
        }
    };

    private synchronized Bitmap getImgUrl(String u){
        Bitmap webImg = null;

        try {
            URL imgUrl = new URL(u);
            HttpURLConnection httpURLConnection = (HttpURLConnection) imgUrl.openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            int length = (int) httpURLConnection.getContentLength();
            int tmpLength = 512;
            int readLen = 0,desPos = 0;
            byte[] img = new byte[length];
            byte[] tmp = new byte[tmpLength];
            if (length != -1) {
                while ((readLen = inputStream.read(tmp)) > 0) {
                    System.arraycopy(tmp, 0, img, desPos, readLen);
                    desPos += readLen;
                }
                webImg = BitmapFactory.decodeByteArray(img, 0, img.length);
                if(desPos != length){
                    throw new IOException("Only read" + desPos +"bytes");
                }
            }
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return webImg;
    }
    /*private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }*/
}
