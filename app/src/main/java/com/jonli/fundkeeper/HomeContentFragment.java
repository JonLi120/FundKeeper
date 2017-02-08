package com.jonli.fundkeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HomeContentFragment extends Fragment {

    private View v;
    private ViewFlow viewFlow;
    private CircleFlowIndicator indic;  //頁表指示器
    private ImageButton btn_fund,btn_list,btn_news,btn_exchange,btn_account,btn_plane;
    private Toolbar tb;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private ArrayList<ArrayList<String>> arr;


    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fg_home, container, false);
        init();

        return v;
    }
    private void init(){
        //ViewFlow init
        viewFlow = (ViewFlow) v.findViewById(R.id.viewflow);
        indic = (CircleFlowIndicator) v.findViewById(R.id.viewflowindicator);
        //ImageButton init
        btn_fund = (ImageButton) v.findViewById(R.id.btn_fund);
        btn_account = (ImageButton) v.findViewById(R.id.btn_account);
        btn_exchange = (ImageButton) v.findViewById(R.id.btn_fx);
        btn_list = (ImageButton) v.findViewById(R.id.btn_list);
        btn_news = (ImageButton) v.findViewById(R.id.btn_news);
        btn_plane = (ImageButton) v.findViewById(R.id.btn_dream);
        tb = (Toolbar)getActivity().findViewById(R.id.toolbar);

    }

    @Override
    public void onStart() {
        super.onStart();
        ((HomeMainActivity)getActivity()).setHomeBack();
        ((HomeMainActivity)getActivity()).setCount(0);

        clearSharedPreferences();

        mDBHelper = new DBHelper(getActivity());
        db = mDBHelper.getReadableDatabase();
        final Cursor c = db.rawQuery("SELECT * FROM List",null);
        if (c.getCount() == 0){
            viewFlow.setAdapter(new ViewAdapter(getActivity(),1,false),0); //接合器,0 is initial position
            viewFlow.setFlowIndicator(indic);
        }else{
            new Thread(runnable).start();
            arr = new ArrayList<>();
        }
        viewFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = mfragmentmanager.beginTransaction();
                ((HomeMainActivity)getActivity()).addCount();
                if (c.getCount() == 0){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("基金搜尋");
                    ft.replace(R.id.fl_content,new FundContentFragment(),"FUND").addToBackStack(null).commit();
                }else{
                    Cursor cursor = db.rawQuery("SELECT * FROM List WHERE id = ?",new String[]{(i+1)+""});
                    cursor.moveToFirst();
                    Bundle bundle = new Bundle();
                    bundle.putString("url",cursor.getString(3));
                    bundle.putInt("company",cursor.getInt(11));
                    bundle.putString("fundname",cursor.getString(1));
                    FundInformation f = new FundInformation();
                    f.setArguments(bundle);
                    cursor.close();
                    ft.replace(R.id.fl_content,f).addToBackStack(null).commit();
                }
                Log.e("onItem",i+"");
            }
        });
        c.close();
        btn_fund.setOnClickListener(new ButtonListener());
        btn_list.setOnClickListener(new ButtonListener());
        btn_exchange.setOnClickListener(new ButtonListener());
        btn_account.setOnClickListener(new ButtonListener());
        btn_news.setOnClickListener(new ButtonListener());
        btn_plane.setOnClickListener(new ButtonListener());
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            db = mDBHelper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM List",null);
            c.moveToFirst();
            do{
                ArrayList<String> item = new ArrayList<>();

                String url = c.getString(3);
                int company = c.getInt(11);
                if (company == 0){
                    url = url.replace("wr01","wr02");
                }else{
                    url = url.replace("wb01","wb02");
                }
                try {
                    Document doc = Jsoup.connect(url).timeout(0).get();
                    Elements tr = doc.select("tr");
                    if (c.getInt(11) == 0){
                        item.add(tr.get(9).select("td").get(0).text());
                        item.add(tr.get(9).select("td").get(1).text());
                        item.add(tr.get(9).select("td").get(3).text());
                    }else{
                        item.add(tr.get(9).select("td").get(0).text());
                        item.add(tr.get(9).select("td").get(1).text());
                        item.add(tr.get(9).select("td").get(2).text());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arr.add(item);
            }while(c.moveToNext());
            c.close();
            handler.sendEmptyMessage(0);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            viewFlow.setAdapter(new ViewAdapter(getActivity(),arr.size(),true,arr),0); //接合器,0 is initial position
            viewFlow.setFlowIndicator(indic);
        }
    };

    private final class ButtonListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {

            FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = mfragmentmanager.beginTransaction();

            switch (view.getId()){
                case R.id.btn_fund:
                    ((HomeMainActivity)getActivity()).addCount();
                    ((AppCompatActivity)getActivity()).setSupportActionBar(tb);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("基金搜尋");
                    ft.replace(R.id.fl_content,new FundContentFragment(),"FUND").addToBackStack(null).commit();
                    break;
                case R.id.btn_fx:
                    ((HomeMainActivity)getActivity()).addCount();
                    ((AppCompatActivity)getActivity()).setSupportActionBar(tb);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("匯率計算");
                    ft.replace(R.id.fl_content,new FXRate(),"FXRate").addToBackStack(null).commit();
                    break;
                case R.id.btn_account:
                    ((HomeMainActivity)getActivity()).addCount();
                    ((AppCompatActivity)getActivity()).setSupportActionBar(tb);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("帳戶管理");
                    ft.replace(R.id.fl_content,new Account(),"Account").addToBackStack(null).commit();
                    break;
                case R.id.btn_news:
                    ((HomeMainActivity)getActivity()).addCount();
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("新聞");
                    ft.replace(R.id.fl_content,new News(),"News").addToBackStack(null).commit();
                    break;
                case R.id.btn_list:
                    ((HomeMainActivity)getActivity()).addCount();
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("觀察清單");
                    ft.replace(R.id.fl_content,new Favorite(),"Favorite").addToBackStack(null).commit();
                    break;
            }
        }
    }

    // 處理滑動屏幕
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewFlow.onConfigurationChanged(newConfig);
    }

    private void clearSharedPreferences(){
        SharedPreferences shared = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        shared.edit().putInt("TabItemPosition",0).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("基金帳管");
    }
}
