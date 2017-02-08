package com.jonli.fundkeeper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Li on 2016/11/13.
 **/

public class FXRate extends Fragment{

    private View v;
    private ImageButton btn_flag1,btn_flag2,btn_change;
    private TextView txt_flag1,txt_flag2,txt_edit_price,txt_rate;
    private EditText edit_price;
    private ListView list_rate;
    private ArrayList<Map<String, Object>> items;
    private DataAdapter adapter;

    private static final String url ="http://www.findrate.tw/converter.php#.WC11DrJ9600";

    private String[] arr1 = new String[]{"新台幣","美元","人民幣","日圓","澳幣","歐元","港幣","英鎊","加拿大幣","泰銖","韓元"};
    private String[] arr2 = new String[]{"TWD","USD","CNY","JPY","AUD","EUR","HKD","GBP","CAD","THB","KRW"};
    private int[] arr3 = new int[]{R.drawable.flag_twd,R.drawable.flag_usd,R.drawable.flag_cny,R.drawable.flag_jpy,R.drawable.flag_aud
            ,R.drawable.flag_eur,R.drawable.flag_hkd,R.drawable.flag_gbp,R.drawable.flag_cad, R.drawable.flag_thb,R.drawable.flag_krw};
    private int[] arr4 = new int[]{R.drawable.country_twd,R.drawable.country_usd,R.drawable.country_cny,R.drawable.country_jpy,R.drawable.country_aud,
            R.drawable.country_eur,R.drawable.country_hkd,R.drawable.country_gbp,R.drawable.country_cad, R.drawable.country_thb,R.drawable.country_krw};

    private ArrayList<ArrayList<String>> cross_rate;

    private int base = 1,relative = 0;   //基準、相對貨幣
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fx_rate, container, false);
        init();
        return v;
    }

    public void init(){
        btn_flag1 = (ImageButton) v.findViewById(R.id.ig_btn1);
        btn_flag2 = (ImageButton) v.findViewById(R.id.ig_btn2);
        btn_change = (ImageButton) v.findViewById(R.id.change_btn);
        txt_flag1 = (TextView) v.findViewById(R.id.ig_txt1);
        txt_flag2 = (TextView) v.findViewById(R.id.ig_txt2);
        txt_edit_price = (TextView) v.findViewById(R.id.edit_numb2);
        txt_rate = (TextView) v.findViewById(R.id.rate_txt);
        edit_price = (EditText) v.findViewById(R.id.edit_numb);
        list_rate = (ListView) v.findViewById(R.id.rate_list);
    }

    @Override
    public void onStart() {
        super.onStart();
        items = new ArrayList<Map<String,Object>>();
        cross_rate = new ArrayList<ArrayList<String>>();
        for(int i = 0 ; i < arr1.length ; i++){
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("currency", arr1[i]);
            item.put("currency_short", arr2[i]);
            item.put("img_flag", arr3[i]);
            item.put("img_country", arr4[i]);
            items.add(item);
        }
        new Thread(runnable).start();

        btn_flag1.setOnClickListener(new ButtonListener());
        btn_flag2.setOnClickListener(new ButtonListener());
        btn_change.setOnClickListener(new ButtonListener());
        edit_price.addTextChangedListener(new EditListener());

    }

    //EditText 監聽事件
    private class EditListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
           setPrice();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
    // Button 監聽事件
    private final class ButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.ig_btn1:
                    showDialog(1);
                    break;
                case R.id.ig_btn2:
                    showDialog(2);
                    break;
                case R.id.change_btn:
                    int i = base;
                    btn_flag1.setImageResource(arr3[relative]);
                    txt_flag1.setText(arr2[relative]);
                    btn_flag2.setImageResource(arr3[base]);
                    txt_flag2.setText(arr2[base]);
                    base = relative; relative = i;
                    new Thread(runnable).start();
                    break;
            }
        }
    }
    private void setPrice(){
        Double rate = Double.parseDouble(txt_rate.getText().toString());
        Double price;
        DecimalFormat df = new DecimalFormat("#,###,##0.000#");
        if (edit_price.getText().toString().isEmpty()){
            price = 1.0;
        }else{
            price = Double.parseDouble(edit_price.getText().toString());
        }
        txt_edit_price.setText(String.valueOf(df.format(rate*price)));
    }

    //顯示彈跳視窗
    private void showDialog(final int s){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.rate_dialog, null);

        final ListView list_dialog = (ListView) view.findViewById(R.id.dialog_list);

        DialogAdapter da = new DialogAdapter(getActivity(), arr1, arr2, arr4);
        list_dialog.setAdapter(da);

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Select Country")
                .setView(view);
        final AlertDialog dialog = ad.create();
        dialog.show();
        list_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (s){
                    case 1:
                        btn_flag1.setImageResource(arr3[i]);
                        txt_flag1.setText(arr2[i]);
                        SetSelectItem(i,s);

                        break;
                    case 2:
                        btn_flag2.setImageResource(arr3[i]);
                        txt_flag2.setText(arr2[i]);
                        SetSelectItem(i,s);
                        break;
                }
                new Thread(runnable).start();
                dialog.cancel();

            }
        });

    }

    private void SetSelectItem(int pos, int s){
        switch (s){
            case 1:
                base = pos;
                break;
            case 2:
                relative = pos;
                break;
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements table_row = doc.select("tr");
                Log.e("div.table Size=","  "+doc.select("tr").size());
                int count = 0;
                for(int i = 0; i < table_row.size()-2; i++){
                    ArrayList<String> list = new ArrayList<>();
                    list.add(doc.select("td").eq(count).text());
                    list.add(doc.select("td").eq(count+1).text());
                    list.add(doc.select("td").eq(count+2).text());
                    list.add(doc.select("td").eq(count+3).text());
                    list.add(doc.select("td").eq(count+4).text());
                    list.add(doc.select("td").eq(count+5).text());
                    list.add(doc.select("td").eq(count+6).text());
                    list.add(doc.select("td").eq(count+7).text());
                    list.add(doc.select("td").eq(count+8).text());
                    list.add(doc.select("td").eq(count+9).text());
                    list.add(doc.select("td").eq(count+10).text());
                    cross_rate.add(list);
                    count += 11;
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
            Log.e("position","base="+base+",relative="+relative);

            txt_rate.setText(cross_rate.get(base).get(relative));
            setPrice();

            adapter = new DataAdapter(getActivity());
            list_rate.setAdapter(adapter);

        }
    };

    class ViewHolder{
        public ImageView item_img;
        public TextView item_tx1,item_tx2,item_tx3,item_tx4;
    }

    private class DataAdapter extends BaseAdapter{

        private LayoutInflater myInflater;

        public DataAdapter(Context context) {
            myInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {

            ViewHolder holder;
            if( v == null){
                v = myInflater.inflate(R.layout.rate_item, null);
                holder = new ViewHolder();
                holder.item_tx1 = (TextView) v.findViewById(R.id.rate_item_txt1);
                holder.item_tx2 = (TextView) v.findViewById(R.id.rate_item_txt2);
                holder.item_tx3 = (TextView) v.findViewById(R.id.rate_item_txt3);
                holder.item_tx4 = (TextView) v.findViewById(R.id.rate_item_txt4);
                holder.item_img = (ImageView) v.findViewById(R.id.rate_item_iv);
                v.setTag(holder);
            }else {
                holder = (ViewHolder) v.getTag();
            }
            double d = 0,edit;
            double rate = Double.parseDouble(cross_rate.get(base).get(i));
            DecimalFormat df = new DecimalFormat("#,###,##0.0###");
            if (edit_price.getText().toString().isEmpty()){
                edit = 1.0;
            }else{
                edit = Double.parseDouble(edit_price.getText().toString());
            }
            holder.item_tx1.setText(arr2[i]);
            holder.item_tx2.setText(String.valueOf(df.format(edit*rate)));
            holder.item_tx3.setText(arr1[i]);
            holder.item_tx4.setText("1 "+arr2[base]+"="+cross_rate.get(base).get(i)+" "+arr2[i]);
            holder.item_img.setImageResource(arr4[i]);
            return v;
        }
    }
}
