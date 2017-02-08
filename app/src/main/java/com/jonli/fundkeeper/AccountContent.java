package com.jonli.fundkeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by Li on 2016/12/22.
 **/

public class AccountContent extends Fragment{
    private View v;
    private PieChart acc_pie;
    private TabLayout acc_tab_layout;
    private ViewPager acc_page;
    private Pageadapter adapter;
    private DBHelper myDBHelper;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;

    private float[] yData = {0,0};
    private String[] xData = { "現金餘額", "基金總值" };

    private int acc_pos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.account_content,container,false);
        initView();
        return v;
    }

    private void initView(){
        acc_pie = (PieChart) v.findViewById(R.id.acc_pie);
        acc_page = (ViewPager) v.findViewById(R.id.acc_page);
        acc_tab_layout = (TabLayout) v.findViewById(R.id.acc_tab_layout);

        acc_tab_layout.addTab(acc_tab_layout.newTab().setText("資金組合"));
        acc_tab_layout.addTab(acc_tab_layout.newTab().setText("戶口交易"));
        acc_tab_layout.addTab(acc_tab_layout.newTab().setText("基金交易"));
        acc_tab_layout.addTab(acc_tab_layout.newTab().setText("基金派息"));

        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);

        Bundle bundle = getArguments();
        acc_pos = bundle.getInt("acc pos");
    }

    @Override
    public void onStart() {
        super.onStart();
        acc_page.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(acc_tab_layout));
        acc_tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                acc_page.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private class Pageadapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> fragments=new ArrayList<>();

        public Pageadapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment f)
        {
            fragments.add(f);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myDBHelper = new DBHelper(getActivity());
        db = myDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Account WHERE id = ?",new String[]{acc_pos+""});
        c.moveToFirst();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(c.getString(1));
        yData[0] = c.getFloat(3);
        c = db.rawQuery("SELECT name, SUM(unit*price) sum FROM Fund WHERE A_id = ? AND interest = 0 GROUP BY name",new String[]{acc_pos+""});
        c.moveToFirst();
        if (c.getCount() != 0){
            do{
                yData[1] = yData[1]+c.getFloat(1);
            }while(c.moveToNext());
        }
        c.close();

        initChart();

        acc_tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);

        int pos = sharedPreferences.getInt("TabItemPosition",0);
        TabLayout.Tab tab = acc_tab_layout.getTabAt(pos);
        tab.select();

        adapter = new Pageadapter (getChildFragmentManager());

        //adapter.addFragment(new PageNewInstant());
        //adapter.addFragment(new PageNewFund());
        //adapter.addFragment(new PageNewFx());
        //adapter.addFragment(new PageNewStock());

        acc_page.setAdapter(adapter);
    }

    private void initChart(){
        acc_pie.setUsePercentValues(true);
        acc_pie.setDescription("");
        acc_pie.setDrawHoleEnabled(true);
        acc_pie.setExtraOffsets(5, 10, 5, 5);
//        acc_pie.setHoleColorTransparent(true);
        acc_pie.setHoleRadius(7); //半徑
        acc_pie.setTransparentCircleRadius(10); //半透明圈
        acc_pie.setRotationAngle(0);    //初始旋轉角度
        acc_pie.setRotationEnabled(true);   //手動旋轉

        addData();

        Legend l = acc_pie.getLegend();     //設置比例圖
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);  //右邊顯示
        l.setXEntrySpace(7);    //設置餅圖距離
        l.setYEntrySpace(5);
    }

    private void addData(){
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        ArrayList<Integer> colors = new ArrayList<Integer>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
          for (int c : ColorTemplate.JOYFUL_COLORS)
              colors.add(c);
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        acc_pie.setData(data);
        acc_pie.highlightValues(null);
        acc_pie.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.edit().putInt("TabItemPosition",acc_tab_layout.getSelectedTabPosition()).commit();
    }
}
