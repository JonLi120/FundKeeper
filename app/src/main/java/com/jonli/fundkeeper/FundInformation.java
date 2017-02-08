package com.jonli.fundkeeper;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Li on 2016/12/4.
 **/

public class FundInformation extends Fragment {
    private View v;
    private TextView t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13;
    private TableLayout tablell;
    private String url01,url02,url03,fundname,dataurl,s21,s22,s23,s24,s31,s32,s33,s34,s35;
    private ArrayList<ArrayList<String>> list;
    private ArrayList<String> x;
    private ArrayList<Entry> y;
    private String[] date;
    private int company;
    private LineChart chart_line;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fund_information,container,false);
        initView();
        setHasOptionsMenu(true);
        return v;
    }
    private void initView(){
        t1 = (TextView) v.findViewById(R.id.infor_text1);
        t2 = (TextView) v.findViewById(R.id.infor_text2);
        t3 = (TextView) v.findViewById(R.id.infor_text3);
        t4 = (TextView) v.findViewById(R.id.infor_text4);
        t5 = (TextView) v.findViewById(R.id.infor_text5);
        t6 = (TextView) v.findViewById(R.id.infor_text6);
        t7 = (TextView) v.findViewById(R.id.infor_text7);
        t8 = (TextView) v.findViewById(R.id.infor_text8);
        t9 = (TextView) v.findViewById(R.id.infor_text9);
        t10 = (TextView) v.findViewById(R.id.infor_text10);
        t11= (TextView) v.findViewById(R.id.infor_text11);
        t12 = (TextView) v.findViewById(R.id.infor_text12);
        t13 = (TextView) v.findViewById(R.id.infor_text13);
        tablell = (TableLayout) v.findViewById(R.id.infor_table);
        chart_line = (LineChart) v.findViewById(R.id.chart_line);

        Bundle bundle = getArguments();
        url01 = bundle.getString("url");
        company = bundle.getInt("company");
        fundname = bundle.getString("fundname");
        int i = url01.indexOf("_")+1; int j = url01.indexOf("-");
        if (company == 0){
            url02 = url01.replace("wr01","wr02"); url03 = url01.replace("wr01","wr03");
            if (j != -1){
                dataurl = "http://finet.landbank.com.tw/w/bcd/tBCDNavList.djbcd?a="+url01.substring(i,j)+"&b=1";
                Log.e("URL",dataurl);
            }else{
                dataurl = "http://finet.landbank.com.tw/w/bcd/tBCDNavList.djbcd?a="+url01.substring(i,url01.length()-6)+"&b=1";
                Log.e("URL",dataurl);
            }
        }else{
            url02 = url01.replace("wb01","wb02"); url03 = url01.replace("wb01","wb03");
            if (j != -1){
                dataurl = "http://finet.landbank.com.tw/w/bcd/BCDNavList.djbcd?a="+url01.substring(i,j)+"&b=1";
                Log.e("URL",dataurl);
            }else{
                dataurl = "http://finet.landbank.com.tw/w/bcd/BCDNavList.djbcd?a="+url01.substring(i,url01.length()-6)+"&b=1";
                Log.e("URL",dataurl);
            }
        }
        list = new ArrayList<ArrayList<String>>();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_update).setVisible(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((HomeMainActivity)getActivity()).setHomeBack();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(fundname);
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Document doc = Jsoup.connect(dataurl).timeout(0).get();
                Elements t = doc.select("body");
                int index = t.indexOf(" ");
                date = t.text().split(" |,");
                x = new ArrayList<>(); y = new ArrayList<>();
                for (int i = 0 ;i<date.length/2;i++){
                    x.add(date[i]);
                    y.add(new Entry(Float.parseFloat(date[i+date.length/2]),i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Document doc = Jsoup.connect(url01).timeout(0).get();
                Document doc2 = Jsoup.connect(url02).timeout(0).get();
                Document doc3 = Jsoup.connect(url03).timeout(0).get();
                Elements tr1 = doc.select("tr");
                Elements tr2 = doc2.select("tr");
                Elements tr3 = doc3.select("tr");
                if (company == 0){
                    s22 = tr1.get(6).select("td").get(1).text();
                    s23 = tr1.get(7).select("td").get(1).text();
                    s21 = tr1.get(9).select("td").get(1).text();
                    s24 = tr1.get(9).select("td").get(3).text();
                    for (int i = 9;i<40;i++){
                        if (i == 24) continue;
                        ArrayList<String> item = new ArrayList<>();
                        item.add(tr2.get(i).select("td").get(0).text());
                        item.add(tr2.get(i).select("td").get(1).text());
                        item.add(tr2.get(i).select("td").get(3).text());
                        if (i == 9){
                            item.add(tr2.get(i).select("td").get(2).text());
                        }
                        list.add(item);
                    }
                    s31 = tr3.get(18).select("td").get(1).text();
                    s32 = tr3.get(18).select("td").get(2).text();
                    s33 = tr3.get(18).select("td").get(3).text();
                    s34 = tr3.get(18).select("td").get(4).text();
                    s35 = tr3.get(18).select("td").get(6).text();
                }else{
                    s21 = tr1.get(6).select("td").get(3).text();
                    s22 = tr1.get(6).select("td").get(1).text();
                    s23 = tr1.get(5).select("td").get(3).text();
                    s24 = tr1.get(7).select("td").get(3).text();
                    for (int i = 9;i<40;i++){
                        if (i == 24) continue;
                        ArrayList<String> item = new ArrayList<>();
                        item.add(tr2.get(i).select("td").get(0).text());
                        item.add(tr2.get(i).select("td").get(1).text());
                        item.add(tr2.get(i).select("td").get(2).text());
                        if (i == 9){
                            float f1 = Float.parseFloat(tr2.get(i).select("td").get(1).text().replace(",",""));
                            float f2 = Float.parseFloat(tr2.get(i+1).select("td").get(1).text().replace(",",""));
                            float f = f1-f2; String s = f+"";
                            if (f >= 0){
                                s = s.substring(0,tr2.get(i).select("td").get(1).text().length());
                            }else{
                                s = s.substring(0,tr2.get(i).select("td").get(1).text().length());
                            }
                            item.add(s);
                        }
                        list.add(item);
                    }
                    s31 = tr3.get(16).select("td").get(2).text();
                    s32 = tr3.get(16).select("td").get(5).text();
                    s33 = tr3.get(16).select("td").get(6).text();
                    s34 = tr3.get(16).select("td").get(8).text();
                    s35 = tr3.get(16).select("td").get(10).text();
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
            initLine();

            t1.setText(list.get(0).get(1));
            t2.setText(list.get(0).get(3)+" ("+list.get(0).get(2)+")");
            t3.setText(list.get(0).get(0));
            t4.setText(s21);
            t5.setText(s22);
            t6.setText(s23);
            t7.setText(s24);
            t8.setText(s31);
            t9.setText(s32);
            t10.setText(s33);
            t11.setText(s34);
            t12.setText(s35);
            t13.setText(fundname+"-近30日淨值");
            if (list.get(0).get(2).indexOf("-") != -1){
                t1.setTextColor(Color.parseColor("#ff0000"));
                t2.setTextColor(Color.parseColor("#ff0000"));
            }else{
                t1.setTextColor(Color.parseColor("#27ff00"));
                t2.setTextColor(Color.parseColor("#27ff00"));
            }
            for (int i = 0;i<list.size();i++){
                //Log.e("table",(list.get(i).get(0)+" "+list.get(i).get(1)+list.get(i).get(2)));
                TableRow row = new TableRow(getActivity());
                LinearLayout ll1 = new LinearLayout(getActivity());
                LinearLayout ll2 = new LinearLayout(getActivity());
                LinearLayout ll3 = new LinearLayout(getActivity());
                TextView txt1 = new TextView(getActivity());
                TextView txt2 = new TextView(getActivity());
                TextView txt3 = new TextView(getActivity());
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params1.setMargins(2,0,0,2); params2.setMargins(2,0,2,2); params3.setMargins(0,0,2,2);
                txt1.setText(list.get(i).get(0)); txt1.setTextColor(Color.BLACK); txt1.setLayoutParams(params1); txt1.setBackgroundColor(Color.parseColor("#E3FDFD"));
                txt1.setTextSize(14); txt1.setGravity(Gravity.CENTER);
                ll1.addView(txt1);
                row.addView(ll1,new TableRow.LayoutParams());
                txt2.setText(list.get(i).get(1)); txt2.setTextColor(Color.BLACK); txt2.setLayoutParams(params2); txt2.setBackgroundColor(Color.parseColor("#E3FDFD"));
                txt3.setTextSize(14); txt2.setGravity(Gravity.CENTER);
                ll2.addView(txt2);
                row.addView(ll2,new TableRow.LayoutParams());
                txt3.setText(list.get(i).get(2)); txt3.setTextColor(Color.BLACK); txt3.setLayoutParams(params3); txt3.setBackgroundColor(Color.parseColor("#E3FDFD"));
                txt3.setTextSize(14); txt3.setGravity(Gravity.CENTER);
                ll3.addView(txt3);
                row.addView(ll3,new TableRow.LayoutParams());
                tablell.addView(row,new TableLayout.LayoutParams());
            }
        }
    };

    private void initLine(){
        chart_line.setDrawBorders(true);
        chart_line.setDrawGridBackground(false);
        chart_line.setDescription("");
        chart_line.setNoDataTextDescription("You need to provide data for the chart.");
        chart_line.setTouchEnabled(true);
        chart_line.setDragEnabled(true);
        chart_line.setScaleEnabled(true);
        chart_line.getAxisRight().setEnabled(false);
        chart_line.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        chart_line.getXAxis().setGridColor(Color.TRANSPARENT);
        chart_line.setPinchZoom(false);
        //设置透明度
        chart_line.setAlpha(0.8f);
        LineData data = getLineData();
        chart_line.setData(data);
        Legend mLegend = chart_line.getLegend();
        mLegend.setForm(Legend.LegendForm.SQUARE);
        CustomMarkerView mv = new CustomMarkerView(getActivity(),R.layout.markview);
        chart_line.setMarkerView(mv);

        chart_line.animateX(1000); // 立即执行的动画,x轴

    }

    private LineData getLineData(){
        LineDataSet dataset = new LineDataSet(y,"");
        dataset.setDrawCircles(false);
        dataset.setDrawFilled(true);
        dataset.setFillColor(Color.parseColor("#FF8DC1F9"));
        dataset.setLineWidth(1f);
        dataset.setColor(Color.parseColor("#FF8DC1F9"));
        dataset.setHighlightLineWidth(1.5f);
        dataset.setDrawHighlightIndicators(true);
        dataset.setValueTextColor(Color.TRANSPARENT);


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
        lineDataSets.add(dataset);

        LineData lineData = new LineData(x, lineDataSets);
        return lineData;
    }

    public class CustomMarkerView extends MarkerView {

        private TextView tvContent;

        public CustomMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            // this markerview only displays a textview
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            tvContent.setText(x.get(e.getXIndex())+" , "+e.getVal()); // set the entry-value as the display text
        }

        @Override
        public int getXOffset(float xpos) {
            // this will center the marker-view horizontally
            return -(getWidth() / 2);
        }

        @Override
        public int getYOffset(float ypos) {
            // this will cause the marker-view to be above the selected value
            return -getHeight();
        }
    }
}
