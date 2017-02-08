package com.jonli.fundkeeper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Li on 2016/11/26.
 **/

public class PageNewFund extends Fragment {
    private View v;
    private CustomRefreshListView listview;
    private DBHelper myDBHelper;
    private SQLiteDatabase db;
    private ListAdapter adapter;
    private String url = "http://www.moneydj.com/KMDJ/News/NewsRealList.aspx?a=MB110000";
    private int p = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.page_fundnews, container, false);
        initView();
        return v;
    }
    private void initView(){
        listview = (CustomRefreshListView)v.findViewById(R.id.fundnews_listview);
    }

    @Override
    public void onStart() {
        super.onStart();
        myDBHelper = new DBHelper(getActivity());
        db = myDBHelper.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM FundNews;",null);
        if (cursor.getCount() == 0){
            new Thread(runnable).start();
        }else{
            p = cursor.getCount()/20;
            setList();
        }
        cursor.close();
    }

    private void setList() {
        adapter = new ListAdapter(getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new ItemClick());
        listview.setOnRefreshListener(new LoadMore());
    }

    private class ItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ((HomeMainActivity)getActivity()).addCount();

            Bundle bundle = new Bundle();
            bundle.putInt("item pos", i);
            bundle.putInt("tab pos", 2);
            NewsContent f = new NewsContent();
            f.setArguments(bundle);
            FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = mfragmentmanager.beginTransaction();
            ft.replace(R.id.fl_content, f).addToBackStack(null).commit();
        }
    }

    private class LoadMore implements CustomRefreshListView.OnRefreshListener {

        @Override
        public void onDownPullRefresh() {
            refresh();
        }

        @Override
        public void onLoadingMore() {
            loadmore();
        }
    }

    private void takeWebData(int s){
        db = myDBHelper.getWritableDatabase();
        if (s == 0){            //數據為空，抓取Data
            url = "http://www.moneydj.com/KMDJ/News/NewsRealList.aspx?a=MB110000";
        }else if(s == 1){       //刷新
            p = 1;
            url = "http://www.moneydj.com/KMDJ/News/NewsRealList.aspx?a=MB110000";
            db.execSQL("DELETE FROM FundNews;");
        }else if(s == 2){       //加載
            p = p+1;
            url = "http://www.moneydj.com/KMDJ/News/NewsRealList.aspx?index1="+p+"&a=MB110000";
        }
        try {
            Document doc = Jsoup.connect(url).timeout(10000).get();
            Elements table = doc.select("tr[style*=25]");
            for (int i = 0; i < table.size(); i++) {
                Elements td = table.get(i).select("td");
                String date = td.get(0).text();
                String title = td.get(1).text();
                String href = "https://www.moneydj.com" + td.select("a").attr("href");
                db.execSQL("INSERT INTO FundNews(date,title,src) VALUES (?, ?, ?);", new String[]{date, title, href});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            takeWebData(0);
            handler.sendEmptyMessage(0);
        }
    };

    private class ListAdapter extends BaseAdapter{
        private LayoutInflater myInflater;

        private ListAdapter(Context context){
            myInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            db = myDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM FundNews;",null);
            int count = cursor.getCount();
            cursor.close();
            //Log.e("getCount",count+"");
            return count;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                view = myInflater.inflate(R.layout.news_item,null);
                holder = new ViewHolder();
                holder.newstitle = (TextView) view.findViewById(R.id.newstitle);
                holder.newsdate = (TextView) view.findViewById(R.id.newsdate);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            db = myDBHelper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM FundNews WHERE id = ?",new String[]{i+1+""});
            c.moveToFirst();
            Log.e("Cursor"," "+c.getString(0)+" "+c.getString(1)+" "+c.getString(2));
            holder.newsdate.setText(c.getString(1));
            holder.newstitle.setText(c.getString(2));
            c.close();
            return view;
        }
    }

    private class ViewHolder{
        TextView newstitle,newsdate;
    }

    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                takeWebData(1);
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void loadmore(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (p<15){
                    takeWebData(2);
                }else{
                    listview.noData(true);
                }
                handler.sendEmptyMessage(2);
            }
        }).start();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    setList();
                    break;
                case 1:
                    listview.hideHeaderView();
                    break;
                case 2:
                    listview.hideFooterView();
                    break;
            }
        }
    };
}
