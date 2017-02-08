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

public class PageTabCompany extends Fragment {
    private View v;
    private Button btn;
    private Spinner company_sp1,company_sp2;
    private String[] sp1_str = {"國內基金公司","海外基金公司"};
    private String[] sp2_in_code = {"BFZ014","BFZDSA","BFZYTA","BFZJSA","BFZ010","BFZ001","BFZGCA","BFZIIA","BFZ012","BFZ002","BFZAPA","BFZBRA","BFZMLA","BFZ005","BFZAIA","BFZCYA","BFZ013","BFZNCA","BFZPSA","BFZICA","BFZ004","BFZFPA","BFZFDA","BFZ008","BFZFHA","BFZ006","BFZCSA","BFZYCA","BFZ007","BFZCIA","BFZTSA","BFZUNA","BFZCAA","BFZDFA","BFZ003","BFZJFE","BFZUIA","BFZTIA","BFZCPA","BFZ011"};
    private String[] sp2_out_code = {"BFC004","BFC069","BFC126","BFC154","BFC034","BFC112","BFC123","BFC051","BFC070","BFC031","BFC035","BFC039","BFC108","BFC084","BFC115","BFC040","BFC138","BFC087","BFC030","BFC139","BFC007","BFC045","BFC047","BFC130","BFC103","BFC050","BFC127","BFC046","BFC019","BFC036","BFC152","BFC023","BFC078","BFC083","BFC001","BFC057","BFC058","BFC011","BFC052","BFC080",
            "BFC079","BFC043","BFC038","BFC137","BFC012","BFC134","BFC006","BFC081","BFC104","BFC008","BFC025","BFC013","BFC120","BFC140","BFC141","BFC020","BFC021","BFC119","BFC014","BFC136","BFC042","BFC049","BFC113","BFC017","BFC121","BFC029","BFC122","BFC077","BFC114","BFC061","BFC063","BFC105","BFC056","BFC060","BFC106","BFC066","BFC059","BFC062","BFC065","BFC131","BFC124","BFC075","BFC133",
            "BFC033","BFC044","BFC107","BFC037","BFC024","BFC074","BFC085","BFC003","BFC018","BFC026","BFC142","BFC076","BFC027"};
    private ArrayList<String> tag_arr,url_arr;
    private ArrayList<Integer> com_arr;
    //static String Tag = "PageCompanyFragment";
    private String Code = "",Url = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fg_page_company, container, false);
        init();
        return v;
    }
    private void init(){
        btn = (Button) v.findViewById(R.id.btn_searchcompany);
        company_sp1 = (Spinner) v.findViewById(R.id.company_sp1);
        company_sp2 = (Spinner) v.findViewById(R.id.company_sp2);
    }


    @Override
    public void onStart() {
        super.onStart();

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sp1_str);
        company_sp1.setAdapter(adapter1);
        company_sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        setSp2(R.array.sp_internal_company);
                        break;
                    case 1:
                        setSp2(R.array.sp_overseas_company);
                        break;
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
                tag_arr.add(company_sp1.getSelectedItem().toString());
                tag_arr.add(company_sp2.getSelectedItem().toString());
                url_arr.add(Url);
                com_arr.add(company_sp1.getSelectedItemPosition());
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("tag arr",tag_arr);
                bundle.putStringArrayList("url",url_arr);
                bundle.putInt("tab pos", 1);
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
        company_sp2.setAdapter(adapter2);
        company_sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (company_sp1.getSelectedItemPosition() == 0){
                    Code = sp2_in_code[i];
                    Url = "http://finet.landbank.com.tw/w/wt/wt01List.djhtm?A=0&B="+Code+"&C=0~0&D=0~0&E=0~0&F=0~0&G=0~0&H=0~0&I=&J=0~0&K=0&FX=0&AREA=0&L=D&M=1";
                    Log.e("Code",Code);
                }else if (company_sp1.getSelectedItemPosition() == 1){
                    Code = sp2_out_code[i];
                    Url = "http://finet.landbank.com.tw/w/wd/wd02List.djhtm?A=0&B=0&C="+Code+"&D=0&E=0&F=0~0&G=0~0&H=0~0&I=0~0&J=0~0&K=0~0&L=0~0&M=0&N=&O=0~0&P=0&Q=D&R=0&S=1";
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
        Log.e("PageTabCompany","onDestroy---------------");
        Code = ""; Url = "";
    }
}