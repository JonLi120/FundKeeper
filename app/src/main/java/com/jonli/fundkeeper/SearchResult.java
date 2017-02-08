package com.jonli.fundkeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jonli.fundkeeper.MyHScrollView.OnScrollChangedListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Li on 2016/12/1.
 **/

public class SearchResult extends Fragment {

    private View v;
    private LinearLayout mHead,tag;
    private ListView mListView1;
    private MyAdapter myAdapter;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private Document doc;
    private ArrayList<String> tag_arr,url_arr;
    private ArrayList<Integer> com_arr;
    private ArrayList<Map<String,Object>> list;
    private String url;
    private int tab_pos,company;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.searchresult, container, false);
        initView();
        setHasOptionsMenu(true);
        return v;
    }

    private void initView(){
        mHead = (LinearLayout) v.findViewById(R.id.head);
        tag = (LinearLayout) v.findViewById(R.id.tag_ll);
        mListView1 = (ListView) v.findViewById(R.id.listView1);

        Bundle bundle = getArguments();
        tag_arr = bundle.getStringArrayList("tag arr");
        url_arr = bundle.getStringArrayList("url");
        tab_pos = bundle.getInt("tab pos");
        com_arr = bundle.getIntegerArrayList("company");
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



    }
    private void setList(){
        mHead.setFocusable(true);
        mHead.setClickable(true);
        mHead.setBackgroundColor(Color.parseColor("#b2d235"));
        mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

        mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

        myAdapter = new MyAdapter(getActivity(), R.layout.hvl_item);
        mListView1.setAdapter(myAdapter);
        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((HomeMainActivity)getActivity()).addCount();
                if (tab_pos == 0)company = com_arr.get(i); else company = com_arr.get(0);
                Bundle bundle = new Bundle();
                bundle.putString("url",list.get(i).get("url").toString());
                bundle.putInt("company",company);
                bundle.putString("fundname",list.get(i).get("name").toString());
                FundInformation f = new FundInformation();
                f.setArguments(bundle);
                FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = mfragmentmanager.beginTransaction();
                ft.replace(R.id.fl_content,f).addToBackStack(null).commit();
            }
        });
        mListView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopupWindow(view,i);
                return true;
            }
        });
    }

    private void setTagView(){
        int count =1;

        for(int i = 0 ;i<tag_arr.size();i++){
            LinearLayout ll = new LinearLayout(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,4,4);
            if ((tab_pos == 3 || tab_pos == 4)&& (i == 0 || i == tag_arr.size()-1)){
                TextView t = new TextView(getActivity());
                t.setText(tag_arr.get(count-1).toString()); t.setBackgroundResource(R.drawable.result_tab); t.setLayoutParams(params);
                ll.addView(t);tag.addView(ll);count+=1;continue;
            }else {
                TextView t = new TextView(getActivity());
                t.setText(tag_arr.get(count-1).toString()); t.setBackgroundResource(R.drawable.result_tab); t.setLayoutParams(params);
                TextView t2 = new TextView(getActivity());
                t2.setText(tag_arr.get(count).toString()); t2.setBackgroundResource(R.drawable.result_tab); t2.setLayoutParams(params);
                ll.addView(t); ll.addView(t2);
                tag.addView(ll); i+=1;count+=2;
            }
        }
    }

    private void initData(){
        list = new ArrayList<>();
        for (int pos = 0;pos<url_arr.size();pos++){
            try {
                doc = Jsoup.connect(url_arr.get(pos)).timeout(0).get();
                Elements table = doc.select("table.wfb0c tr");
                for (int i = 2; i<table.size(); i++){
                    Map<String, Object> item = new HashMap<String, Object>();
                    Elements td = table.get(i).select("td");
                    if (td.get(0).text().equals("目前沒有符合搜尋條件的相關結果")){
                        handler.sendEmptyMessage(1);
                        break;
                    }
                    if (i > 1000 || td.get(5).text().equals("N/A")) break;
                    item.put("url","http://finet.landbank.com.tw"+td.get(0).select("a").attr("href"));
                    String t = td.get(0).text();
                    int index = t.indexOf("(本");
                    if(index != -1)t = t.substring(0,index);
                    item.put("name",t);
                    item.put("1m",td.get(5).text());
                    item.put("3m",td.get(6).text());
                    item.put("6m",td.get(7).text());
                    item.put("y",td.get(4).text());
                    item.put("1y",td.get(8).text());
                    if (com_arr.get(pos) == 0){
                        item.put("date",td.get(3).text());
                        item.put("3y",td.get(10).text());
                        item.put("5y",td.get(11).text());
                    }else{
                        item.put("date",td.get(1).text());
                        item.put("3y",td.get(9).text());
                        item.put("5y",td.get(10).text());
                    }
                    list.add(item);
                }
                handler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initKeyWordData(){
        list = new ArrayList<>();
        for (int pos = 0;pos<url_arr.size();pos++){
            String url = url_arr.get(pos).replace("wr01","wr03");
            try {
                doc = Jsoup.connect(url).timeout(0).get();
                Elements table = doc.select("table.wfb0c tr");
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("1m",table.get(17).select("td").get(1).text());
                item.put("3m",table.get(17).select("td").get(2).text());
                item.put("6m",table.get(17).select("td").get(3).text());
                item.put("y",table.get(13).select("td").get(3).text());
                item.put("1y",table.get(17).select("td").get(4).text());
                item.put("3y",table.get(17).select("td").get(6).text());
                item.put("5y",table.get(17).select("td").get(7).text());
                String t = table.get(13).select("td").get(0).text();
                int index = t.indexOf("(本");
                if(index != -1)t = t.substring(0,index);
                item.put("name",t);
                item.put("date",table.get(13).select("td").get(2).text());
                item.put("url",url_arr.get(pos));
                list.add(item);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                Toast.makeText(getActivity(),"目前沒有符合搜尋條件的相關結果",Toast.LENGTH_LONG).show();
            }
            setList();
        }
    };

    private void showPopupWindow(View view,int pos){
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_window, null);
        PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        Button pop_btn1 = (Button) contentView.findViewById(R.id.pop_btn1);
        Button pop_btn2 = (Button) contentView.findViewById(R.id.pop_btn2);
        Button pop_btn3 = (Button) contentView.findViewById(R.id.pop_btn3);
        pop_btn1.setOnClickListener(new ButtonClick(pos,popupWindow));
        pop_btn1.setText("加入清單");
        pop_btn2.setVisibility(View.GONE);
        pop_btn3.setVisibility(View.GONE);
        popupWindow.showAsDropDown(view, 550, 0);
    }
    private class ButtonClick implements View.OnClickListener{
        int pos;
        PopupWindow pw;
        ButtonClick(int pos,PopupWindow pw){
            this.pos = pos; this.pw = pw;
        }
        @Override
        public void onClick(View view) {
            if (tab_pos == 0) company = com_arr.get(pos); else company = com_arr.get(0);
            if (view.getId() == R.id.pop_btn1){
                Toast.makeText(getActivity(),"已加入觀察清單",Toast.LENGTH_SHORT).show();
                mDBHelper = new DBHelper(getActivity());
                db = mDBHelper.getWritableDatabase();
                db.execSQL("INSERT INTO List(name,date,url,month1,month3,month6,year,year1,year3,year5,company) VALUES (?,?,?,?,?,?,?,?,?,?,?)",new Object[]{
                        list.get(pos).get("name").toString(),
                        list.get(pos).get("date").toString(),
                        list.get(pos).get("url").toString(),
                        list.get(pos).get("1m").toString(),
                        list.get(pos).get("3m").toString(),
                        list.get(pos).get("6m").toString(),
                        list.get(pos).get("y").toString(),
                        list.get(pos).get("1y").toString(),
                        list.get(pos).get("3y").toString(),
                        list.get(pos).get("5y").toString(),
                        company});
                pw.dismiss();
            }
        }
    }

    private class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent arg1) {
            HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
                    .findViewById(R.id.horizontalScrollView1);
            headSrcrollView.onTouchEvent(arg1);
            return false;
        }
    }

    private class MyAdapter extends BaseAdapter{

        private int id_row_layout;
        private LayoutInflater mInflater;

        public MyAdapter(Context context, int id_row_layout) {
            super();
            this.id_row_layout = id_row_layout;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
             return list.size();
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
            final ViewHolder holder;
            if (view == null){
                view = mInflater.inflate(id_row_layout, null);
                holder = new ViewHolder();
                MyHScrollView scrollView1 = (MyHScrollView) view.findViewById(R.id.horizontalScrollView1);
                holder.scrollView = scrollView1;
                holder.txt1 = (TextView) view.findViewById(R.id.textView1);
                holder.txt2 = (TextView) view.findViewById(R.id.textView2);
                holder.txt3 = (TextView) view.findViewById(R.id.textView3);
                holder.txt4 = (TextView) view.findViewById(R.id.textView4);
                holder.txt5 = (TextView) view.findViewById(R.id.textView5);
                holder.txt6 = (TextView) view.findViewById(R.id.textView6);
                holder.txt8 = (TextView) view.findViewById(R.id.textView8);
                holder.txt9 = (TextView) view.findViewById(R.id.textView9);
                holder.t1 = (TextView) view.findViewById(R.id.tx1);
                holder.t2 = (TextView) view.findViewById(R.id.tx2);
                MyHScrollView headSrcrollView = (MyHScrollView) mHead.findViewById(R.id.horizontalScrollView1);
                headSrcrollView.AddOnScrollChangedListener(new OnScrollChangedListenerImp(scrollView1));
                view.setTag(holder);
            }else{
                holder = (ViewHolder)view.getTag();
            }
            holder.txt1.setVisibility(View.GONE);
            holder.t1.setVisibility(View.VISIBLE); holder.t2.setVisibility(View.VISIBLE);
            holder.txt2.setText(list.get(i).get("1m").toString());
            holder.txt3.setText(list.get(i).get("3m").toString());
            holder.txt4.setText(list.get(i).get("6m").toString());
            holder.txt5.setText(list.get(i).get("y").toString());
            holder.txt6.setText(list.get(i).get("1y").toString());
            holder.txt8.setText(list.get(i).get("3y").toString());
            holder.txt9.setText(list.get(i).get("5y").toString());
            holder.t1.setText(list.get(i).get("name").toString());
            holder.t2.setText(list.get(i).get("date").toString());

            return view;
        }
    }
    private class OnScrollChangedListenerImp implements OnScrollChangedListener{
        MyHScrollView mScrollViewArg;

        public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
            mScrollViewArg = scrollViewar;
        }
        @Override
        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            mScrollViewArg.smoothScrollTo(l, t);
        }
    }
    private class ViewHolder {
        TextView txt1,txt2,txt3,txt4,txt5,txt6,txt8,txt9,t1,t2;
        HorizontalScrollView scrollView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeMainActivity)getActivity()).setHomeBack();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("搜尋結果");

        setTagView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (tab_pos == 0){
                    initKeyWordData();
                }else if (tab_pos == -1){
                    list = new ArrayList<>();
                    handler.sendEmptyMessage(1);
                }else{
                    initData();
                }
            }
        }).start();
    }
}
