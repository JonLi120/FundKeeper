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
import java.util.List;

public class PageTabAdvanced extends Fragment {
    private View v;
    private Button btn;
    private Spinner adv_sp1,adv_sp2,adv_sp3,adv_sp4,adv_sp5,adv_sp6;
    private TextView adv_text;
    private String[] sp1_str = {"國內基金","境外基金"};
    private String[] adv_in_area={"0","23","15","9","3","52","12","6","55","49","27","58","44","1","50","47","18","30","41","4","13","42","5","59","45","2","48","71","17","31","40","34","11","57","37","8","51"};
    private String[] adv_out_area={"0","62","48","51","46","39","37","57","1","33","2","3","47","4","5","6","40","44","8","9","11","12","14","49","50","35","41","15","16","17","18","19","21","53","22","23","58",
            "34","24","25","26","42","45","52","27","28","29","43","30","31"};
    private String[] adv_sharpe={"0~0","-999~-0.9","-0.9~-0.7","-0.7~-0.5","-0.5~-0.3","-0.3~-0.1","0~0.1","0.1~0.3","0.3~0.5","0.5~0.7","0.7~0.9","0.9~1.1","1.1~1.3","1.3~1.5","1.5~999"};
    private String[] adv_bata={"0","20","40","60"};
    private String[] sp2_in_currency ={"0","AX000090","AX000020","AX000080","AX000010","AX000070","AX000180","AX000250"};
    private String[] sp2_out_currency={"0","AX000250","AX000180","AX000020","AX000100","AX000070","AX000080","AX000030","AX000090",
            "AX000140","AX000040","AX000110","AX000010","AX000190","AX000240"};
    private String[] sp2_in_class={"0","ET000001","ET001001","ET001004","ET001005","ET001006","ET001007","ET001008","ET003001","ET003002","ET003003","ET003004",
            "ET004001","ET004002","ET004003","ET004004","ET005003","ET005005","ET005006","ET006002","ET006003","ET006004","ET006005","ET006006","ET007001",
            "ET008001","ET008002"};
    private String[] sp2_out_class={"0","28","1","55","33","35","42","32","38","2","21","50","54","57","52","15","22","29","25","16","44","8","30","17","23","31",
            "11","24","45","7","47"};
    private String Str_class="",Str_area="",Str_currency="",Str_sharpe="",Str_bata="",Url="";
    private ArrayList<String> tag_arr,url_arr;
    private ArrayList<Integer> com_arr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Advanced","true");
        v=inflater.inflate(R.layout.fg_page_advanced, container, false);
        init();
        return v;
    }
    private void init(){
        btn = (Button) v.findViewById(R.id.btn_searchadvanced);
        adv_sp1 = (Spinner) v.findViewById(R.id.adv_sp1);
        adv_sp2 = (Spinner) v.findViewById(R.id.adv_sp2);
        adv_sp3 = (Spinner) v.findViewById(R.id.adv_sp3);
        adv_sp4 = (Spinner) v.findViewById(R.id.adv_sp4);
        adv_sp5 = (Spinner) v.findViewById(R.id.adv_sp5);
        adv_sp6 = (Spinner) v.findViewById(R.id.adv_sp6);
        adv_text = (TextView) v.findViewById(R.id.adv_text);
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sp1_str);
        adv_sp1.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_sharpe,android.R.layout.simple_spinner_dropdown_item);
        adv_sp5.setAdapter(adapter5);

        ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_bata,android.R.layout.simple_spinner_dropdown_item);
        adv_sp6.setAdapter(adapter6);

        adv_sp1.setOnItemSelectedListener(new itemSelect());
        adv_sp5.setOnItemSelectedListener(new itemSelect());
        adv_sp6.setOnItemSelectedListener(new itemSelect());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeMainActivity)getActivity()).addCount();
                setTag();
                url_arr = new ArrayList<>(); com_arr = new ArrayList<>();
                url_arr.add(Url); com_arr.add(adv_sp1.getSelectedItemPosition());
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("tag arr",tag_arr);
                bundle.putStringArrayList("url",url_arr);
                bundle.putInt("tab pos", 4);
                bundle.putIntegerArrayList("company",com_arr);
                SearchResult f = new SearchResult();
                f.setArguments(bundle);
                FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = mfragmentmanager.beginTransaction();
                ft.replace(R.id.fl_content,f).addToBackStack(null).commit();
            }
        });
    }

    private class itemSelect implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
            switch (adapterView.getId()){
                case R.id.adv_sp1:
                    if (adv_sp1.getSelectedItemPosition() == 0){
                        adv_text.setText("基金類型");
                        List<CharSequence> itemList =new ArrayList<>();
                        itemList.add("---全部---");
                        CharSequence[] itemArray = getResources().getTextArray(R.array.sp_internal_class);
                        for (int pos = 0;pos<itemArray.length;pos++){
                            itemList.add(itemArray[pos]);
                        }
                        ArrayAdapter<CharSequence> adapter2= new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,itemList);
                        adv_sp2.setAdapter(adapter2);

                        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_internal_area,android.R.layout.simple_spinner_dropdown_item);
                        adv_sp3.setAdapter(adapter3);

                        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_internal_currency,android.R.layout.simple_spinner_dropdown_item);
                        adv_sp4.setAdapter(adapter4);

                        adv_sp2.setOnItemSelectedListener(new itemSelect());
                        adv_sp3.setOnItemSelectedListener(new itemSelect());
                        adv_sp4.setOnItemSelectedListener(new itemSelect());
                    }else {
                        adv_text.setText("投資標的");
                        List<CharSequence> itemList =new ArrayList<>();
                        itemList.add("---全部---");
                        CharSequence[] itemArray = getResources().getTextArray(R.array.sp_overseas_class);
                        for (int pos = 0;pos<itemArray.length;pos++){
                            itemList.add(itemArray[pos]);
                        }
                        ArrayAdapter<CharSequence> adapter2= new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,itemList);
                        adv_sp2.setAdapter(adapter2);

                        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_overseas_area,android.R.layout.simple_spinner_dropdown_item);
                        adv_sp3.setAdapter(adapter3);

                        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getActivity(),R.array.sp_overseas_currency,android.R.layout.simple_spinner_dropdown_item);
                        adv_sp4.setAdapter(adapter4);

                        adv_sp2.setOnItemSelectedListener(new itemSelect());
                        adv_sp3.setOnItemSelectedListener(new itemSelect());
                        adv_sp4.setOnItemSelectedListener(new itemSelect());
                    }break;
                case R.id.adv_sp2:
                    if (adv_sp1.getSelectedItemPosition() == 0) {
                        Str_class = sp2_in_class[i];
                    }else{
                        Str_class = sp2_out_class[i];
                    }break;
                case R.id.adv_sp3:
                    if (adv_sp1.getSelectedItemPosition() == 0){
                        Str_area = adv_in_area[i];
                    }else{
                        Str_area = adv_out_area[i];
                    }break;
                case R.id.adv_sp4:
                    if (adv_sp1.getSelectedItemPosition() == 0){
                        Str_currency = sp2_in_currency[i];
                    }else{
                        Str_currency = sp2_out_currency[i];
                    }break;
                case R.id.adv_sp5:
                    Str_sharpe = adv_sharpe[i];
                    break;
                case R.id.adv_sp6:
                    Str_bata = adv_bata[i];
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void setTag(){
        tag_arr = new ArrayList<>();
        tag_arr.add(adv_sp1.getSelectedItem().toString());
        if (adv_sp1.getSelectedItemPosition() == 0){
            tag_arr.add("類型:"+adv_sp2.getSelectedItem().toString());
        }else{
            tag_arr.add("標的:"+adv_sp2.getSelectedItem().toString());
        }
        if (!Str_area.equals("0")) tag_arr.add(adv_sp3.getSelectedItem().toString());
        if (!Str_currency.equals("0")) tag_arr.add(adv_sp4.getSelectedItem().toString());
        if (!Str_sharpe.equals("0~0")) tag_arr.add("Sharpe值:"+adv_sp5.getSelectedItem().toString());
        if (!Str_bata.equals("0")) tag_arr.add("Bata值:"+adv_sp6.getSelectedItem().toString());

        if (adv_sp1.getSelectedItemPosition() == 0){
            Url ="http://finet.landbank.com.tw/w/wt/wt01List.djhtm?A="+Str_class+"&B=0&C=0~0&D=0~0&E=0~0&F=0~0&G=0~0&H=0~0&I=&J="+Str_sharpe+"&K="+Str_bata+"&FX="+Str_currency+"&AREA="+Str_area+"&L=D&M=1";
        }else{
            Url = "http://finet.landbank.com.tw/w/wd/wd02List.djhtm?A="+Str_class+"&B=0&C=0&D="+Str_area+"&E="+Str_currency+"&F=0~0&G=0~0&H=0~0&I=0~0&J=0~0&K=0~0&L=0~0&M=0&N=&O="+Str_sharpe+"&P="+Str_bata+"&Q=D&R=0&S=1";
        }
    }
}