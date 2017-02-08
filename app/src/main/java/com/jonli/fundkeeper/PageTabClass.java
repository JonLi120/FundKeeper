package com.jonli.fundkeeper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class PageTabClass extends Fragment {

    private View v;
    private Button btn;
    private TextView text;
    private Spinner class_sp1,class_sp2;
    private String[] sp1_str={"國內基金","境外基金"};
    private String[] sp2_in_code={"ET000001","ET001001","ET001004","ET001005","ET001006","ET001007","ET001008","ET003001","ET003002","ET003003","ET003004",
            "ET004001","ET004002","ET004003","ET004004","ET005003","ET005005","ET005006","ET006002","ET006003","ET006004","ET006005","ET006006","ET007001",
            "ET008001","ET008002"};
    private String[] sp2_out_code={"28","1","55","33","35","42","32","38","2","21","50","54","57","52","15","22","29","25","16","44","8","30","17","23","31",
            "11","24","45","7","47"};
    private ArrayList<String> tag_arr,url_arr;
    private ArrayList<Integer> com_arr;
    private String Code = "",Url = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fg_page_class, container, false);
        initView();

        return v;
    }

    private void initView(){
        btn = (Button) v.findViewById(R.id.class_btn);
        text = (TextView) v.findViewById(R.id.class_text);
        class_sp1 = (Spinner) v.findViewById(R.id.class_sp1);
        class_sp2 = (Spinner) v.findViewById(R.id.class_sp2);
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sp1_str);
        class_sp1.setAdapter(adapter1);
        class_sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    setSp2(R.array.sp_internal_class);
                }else{
                    setSp2(R.array.sp_overseas_class);
                    text.setText("投資標的");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeMainActivity)getActivity()).addCount();
                tag_arr = new ArrayList<>(); url_arr = new ArrayList<>(); com_arr = new ArrayList<>();
                tag_arr.add(class_sp1.getSelectedItem().toString());
                tag_arr.add(class_sp2.getSelectedItem().toString());
                url_arr.add(Url);
                com_arr.add(class_sp1.getSelectedItemPosition());
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("tag arr",tag_arr);
                bundle.putStringArrayList("url",url_arr);
                bundle.putInt("tab pos", 2);
                bundle.putIntegerArrayList("company",com_arr);
                SearchResult f = new SearchResult();
                f.setArguments(bundle);
                FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = mfragmentmanager.beginTransaction();
                ft.replace(R.id.fl_content,f).addToBackStack(null).commit();
            }
        });
    }

    private void setSp2(int r){
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),r,android.R.layout.simple_spinner_dropdown_item);
        class_sp2.setAdapter(adapter2);
        class_sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (class_sp1.getSelectedItemPosition() == 0){
                    Code = sp2_in_code[i];
                    Url = "http://finet.landbank.com.tw/w/wt/wt01List.djhtm?A="+Code+"&B=0&C=0~0&D=0~0&E=0~0&F=0~0&G=0~0&H=0~0&I=&J=0~0&K=0&FX=0&AREA=0&L=D&M=1";
                    Log.e("Code",Code);
                }else if (class_sp1.getSelectedItemPosition() == 1){
                    Code = sp2_out_code[i];
                    Url = "http://finet.landbank.com.tw/w/wd/wd02List.djhtm?A="+Code+"&B=0&C=0&D=0&E=0&F=0~0&G=0~0&H=0~0&I=0~0&J=0~0&K=0~0&L=0~0&M=0&N=&O=0~0&P=0&Q=D&R=0&S=1";
                    Log.e("Code",Code);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("PageTabClass","onDestroy---------------");
        Code = ""; Url = "";
    }
}