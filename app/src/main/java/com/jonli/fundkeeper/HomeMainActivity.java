package com.jonli.fundkeeper;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class HomeMainActivity extends AppCompatActivity {

    private DrawerLayout mdrawerLayout;
    private NavigationView mnavigationView;
    private Toolbar toolbar;
    private DBHelper mDBHelper ;
    private SQLiteDatabase db;
    private Boolean goback = false;
    private int Count = 0;
    private String[] in_code = {"BFZ014","BFZDSA","BFZYTA","BFZJSA","BFZ010","BFZ001","BFZGCA","BFZIIA","BFZ012","BFZ002","BFZAPA","BFZBRA","BFZMLA","BFZ005","BFZAIA","BFZCYA","BFZ013","BFZNCA","BFZPSA","BFZICA","BFZ004","BFZFPA","BFZFDA","BFZ008","BFZFHA","BFZ006","BFZCSA","BFZYCA","BFZ007","BFZCIA","BFZTSA","BFZUNA","BFZCAA","BFZDFA","BFZ003","BFZJFE","BFZUIA","BFZTIA","BFZCPA","BFZ011"};
    private String url = "http://finet.landbank.com.tw/w/wt/wt01List.djhtm?a=0&b=BFZ014&c=0~0&d=0~0&e=0~0&f=0~0&g=0~0&h=0~0&i=&j=0~0&k=0&L=Z&M=&fx=0&area=0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_main);

        mdrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mnavigationView = (NavigationView) findViewById(R.id.navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("基金帳管");


        setupDrawerContent(mnavigationView);

        //Begin new fragment content
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_content, new HomeContentFragment()).commit();
        //Creat DataBase
        mDBHelper = new DBHelper(this);

        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isFirstRun)
        {
            editor.putBoolean("isFirstRun", false);
            editor.commit();
            new Thread(runnable).start();
        }

        //mDBHelper.close();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            db = mDBHelper.getWritableDatabase();
            for (int i =0;i<in_code.length;i++){
                try {
                    Document doc = Jsoup.connect("http://finet.landbank.com.tw/w/wt/wt01List.djhtm?a=0&b="+in_code[i]+"&c=0~0&d=0~0&e=0~0&f=0~0&g=0~0&h=0~0&i=&j=0~0&k=0&L=Z&M=&fx=0&area=0").timeout(0).get();
                    Elements table = doc.select("table.wfb0c tr");
                    for (int j = 2;j<table.size();j++){
                        String s,s1;
                        s = table.get(j).select("td").get(0).text();
                        int index = s.indexOf("(本");
                        if(index != -1)s = s.substring(0,index);
                        s1 = "http://finet.landbank.com.tw"+table.get(j).select("td").get(0).select("a").attr("href");
                        if (s.equals("目前沒有符合搜尋條件的相關結果")) break;
                        db.execSQL("INSERT INTO FundData(name,url,company) VALUES (?,?,?);",new Object[]{s,s1,0});
                        Log.e("Main","I= "+i+" J= "+j+"/"+s+" /"+s1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //側屏監聽
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_share:
                        menuItem.setChecked(false);
                        break;
                    case R.id.nav_send:
                        menuItem.setChecked(false);
                        break;
                    default:
                        menuItem.setChecked(true);
                        break;
                }
                mdrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // ActionBar監聽
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                return true;
            case R.id.action_exit:
                return true;
            case R.id.action_search:
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                SearchView searchView = (SearchView) item.getActionView();

                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

                searchView.setIconifiedByDefault(true);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_update:
                return true;
            case android.R.id.home:
                if (goback){
                    getSupportFragmentManager().popBackStack();
                    Count-=1;
                }else{
                    mdrawerLayout.openDrawer(mnavigationView);  //打開側屏
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (Count!=0){
                Count-=1;
                getSupportFragmentManager().popBackStack();
            }else{
                finish();
            }
            Log.e("KeyDown",Count+"");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setHomeBack(){
        final ActionBar ab = getSupportActionBar();
        Log.e("setHomeBack",Count+"");
        if (Count<2){
            goback = false;
            ab.setHomeAsUpIndicator(R.drawable.ic_left_menu);   // 改變左上角圖標
            ab.setDisplayHomeAsUpEnabled(true);     //给左上角圖標的左邊加上一个返回的圖標
            ab.setHomeButtonEnabled(true);      //决定左上角的圖標是否可以點擊
        }else{
            goback = true;
            ab.setHomeAsUpIndicator(R.drawable.ic_left_back);   // 改變左上角圖標
            ab.setDisplayHomeAsUpEnabled(true);     //给左上角圖標的左邊加上一个返回的圖標
            ab.setHomeButtonEnabled(true);      //决定左上角的圖標是否可以點擊
        }

    }
    public void addCount(){
        Count += 1;
    }
    public void setCount(int c){
        Count = c;
    }



}
