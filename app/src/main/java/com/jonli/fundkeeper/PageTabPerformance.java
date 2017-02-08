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

import java.util.ArrayList;

public class PageTabPerformance extends Fragment {

    private View v;
    private Spinner per_sp1,per_sp2,per_sp3,per_sp4,per_sp5,per_sp6,per_sp7;
    private Button btn;
    private String[] sp1_str = {"國內基金公司","境外基金"};
    private String[] sp_code ={"0~0","-999~-30","-15~-30","-15~0","0~5","5~10","10~15","15~20","20~25","25~30","30~999"};
    private String[] sp2_in_code ={"0","AX000090","AX000020","AX000080","AX000010","AX000070","AX000180","AX000250"};
    private String[] sp2_out_code={"0","AX000250","AX000180","AX000020","AX000100","AX000070","AX000080","AX000030","AX000090",
            "AX000140","AX000040","AX000110","AX000010","AX000190","AX000240"};
    private ArrayList<String> tag_arr,url_arr;
    private ArrayList<Integer> com_arr;
    private String Code = "",Url = "",DL = "0~0", EK = "0~0", FJ = "0~0", GI = "0~0", H = "0~0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fg_page_performance, container, false);
        initView();
        return v;
    }

    private void initView(){
        per_sp1 = (Spinner) v.findViewById(R.id.per_sp1);
        per_sp2 = (Spinner) v.findViewById(R.id.per_sp2);
        per_sp3 = (Spinner) v.findViewById(R.id.per_sp3);
        per_sp4 = (Spinner) v.findViewById(R.id.per_sp4);
        per_sp5 = (Spinner) v.findViewById(R.id.per_sp5);
        per_sp6 = (Spinner) v.findViewById(R.id.per_sp6);
        per_sp7 = (Spinner) v.findViewById(R.id.per_sp7);
        btn = (Button) v.findViewById(R.id.per_btn);
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sp1_str);
        per_sp1.setAdapter(adapter1);
        per_sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        setSp2(R.array.sp_internal_currency);
                        break;
                    case 1:
                        setSp2(R.array.sp_overseas_currency);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_performance,android.R.layout.simple_spinner_dropdown_item);
        per_sp3.setAdapter(adapter3);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_performance,android.R.layout.simple_spinner_dropdown_item);
        per_sp4.setAdapter(adapter4);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_performance,android.R.layout.simple_spinner_dropdown_item);
        per_sp5.setAdapter(adapter5);
        ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_performance,android.R.layout.simple_spinner_dropdown_item);
        per_sp6.setAdapter(adapter6);
        ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_performance,android.R.layout.simple_spinner_dropdown_item);
        per_sp7.setAdapter(adapter7);

        per_sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (per_sp1.getSelectedItemPosition() == 0){
                    DL = sp_code[i];
                }else{
                    H = sp_code[i];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        per_sp4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (per_sp1.getSelectedItemPosition() == 0){
                    EK = sp_code[i];
                }else{
                    GI = sp_code[i];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        per_sp5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (per_sp1.getSelectedItemPosition() == 0){
                    FJ = sp_code[i];
                }else{
                    FJ = sp_code[i];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        per_sp6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (per_sp1.getSelectedItemPosition() == 0){
                    GI = sp_code[i];
                }else{
                    EK = sp_code[i];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        per_sp7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (per_sp1.getSelectedItemPosition() == 0){
                    H = sp_code[i];
                }else{
                    DL = sp_code[i];
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
                tag_arr.add(per_sp1.getSelectedItem().toString());
                if (per_sp2.getSelectedItemPosition() != 0)
                    tag_arr.add(per_sp2.getSelectedItem().toString());
                if (per_sp1.getSelectedItemPosition() == 0){
                    if (!DL.equals("0~0")) tag_arr.add("3月"+per_sp3.getSelectedItem().toString());
                    if (!EK.equals("0~0")) tag_arr.add("6月"+per_sp4.getSelectedItem().toString());
                    if (!FJ.equals("0~0")) tag_arr.add("1年"+per_sp5.getSelectedItem().toString());
                    if (!GI.equals("0~0")) tag_arr.add("3年"+per_sp6.getSelectedItem().toString());
                    if (!H.equals("0~0")) tag_arr.add("5年"+per_sp7.getSelectedItem().toString());
                    Url = "http://finet.landbank.com.tw/w/wt/wt01List.djhtm?A=0&B=0&C=0~0&D="+DL+"&E="+EK+"&F="+FJ+"&G="+GI+"&H="+H+"&I=&J=0~0&K=0&FX="+Code+"&AREA=0&L=D&M=1";
                    Log.e("Per","Url ="+Url);
                }else{
                    if (!H.equals("0~0")) tag_arr.add("3月"+per_sp3.getSelectedItem().toString());
                    if (!GI.equals("0~0")) tag_arr.add("6月"+per_sp4.getSelectedItem().toString());
                    if (!FJ.equals("0~0")) tag_arr.add("1年"+per_sp5.getSelectedItem().toString());
                    if (!EK.equals("0~0")) tag_arr.add("3年"+per_sp6.getSelectedItem().toString());
                    if (!DL.equals("0~0")) tag_arr.add("5年"+per_sp7.getSelectedItem().toString());
                    Url = "http://finet.landbank.com.tw/w/wd/wd02List.djhtm?A=0&B=0&C=0&D=0&E="+Code+"&F=0~0&G=0~0&H="+H+"&I="+GI+"&J="+FJ+"&K="+EK+"&L="+DL+"&M=0&N=&O=0~0&P=0&Q=D&R=0&S=1";
                    Log.e("Per","Url ="+Url);
                }
                url_arr.add(Url); com_arr.add(per_sp1.getSelectedItemPosition());

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("tag arr",tag_arr);
                bundle.putStringArrayList("url",url_arr);
                bundle.putInt("tab pos", 3);
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
        per_sp2.setAdapter(adapter2);
        per_sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (per_sp1.getSelectedItemPosition() == 0){
                    Code = sp2_in_code[i];
                    Log.e("Code",Code);
                }else if (per_sp1.getSelectedItemPosition() == 1){
                    Code = sp2_out_code[i];
                    Log.e("Code",Code);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}