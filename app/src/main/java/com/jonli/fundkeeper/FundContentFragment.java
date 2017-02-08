package com.jonli.fundkeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

public class FundContentFragment extends Fragment{

    private View v;
    private EditText edt_searchbar;
    private ImageButton btn_go;
    private TabLayout tab_layout;
    private ViewPager page_fund;
    static String Tag = "ContentFragment";
    private PageAdapter adapter;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private Toolbar tb;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> tag_arr,url_arr;
    private ArrayList<Integer> com_arr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fg_searchfund, container, false);
        initView();
        return v;
    }
    private void initView(){
        edt_searchbar = (EditText) v.findViewById(R.id.edt_searchbar);
        btn_go = (ImageButton) v.findViewById(R.id.btn_go);
        tab_layout = (TabLayout) v.findViewById(R.id.tab_layout);
        page_fund = (ViewPager) v.findViewById(R.id.page_fund);
        tb = (Toolbar)getActivity().findViewById(R.id.toolbar);

        tab_layout.addTab(tab_layout.newTab().setText("公司搜尋"));
        tab_layout.addTab(tab_layout.newTab().setText("類別搜尋"));
        tab_layout.addTab(tab_layout.newTab().setText("績效搜尋"));
        tab_layout.addTab(tab_layout.newTab().setText("進階搜尋"));

        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeMainActivity)getActivity()).addCount();

                int tab_pos = 0;

                tag_arr = new ArrayList<>(); url_arr = new ArrayList<>(); com_arr = new ArrayList<>();
                tag_arr.add("關鍵字搜尋"); tag_arr.add(edt_searchbar.getText().toString());
                mDBHelper = new DBHelper(getActivity());
                db = mDBHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM FundData WHERE name LIKE ?",new String[]{"%"+edt_searchbar.getText()+"%"});
                cursor.moveToFirst();
                if (cursor.getCount() == 0){
                    url_arr.add("");
                    com_arr.add(-1);
                    tab_pos = -1;
                    cursor.close();
                }else{
                    int index =0;
                    do{
                        url_arr.add(cursor.getString(2));
                        com_arr.add(cursor.getInt(3));
                        index +=1;
                    }while (cursor.moveToNext());
                    tab_pos = 0;
                    cursor.close();
                }

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("tag arr",tag_arr);
                bundle.putStringArrayList("url",url_arr);
                bundle.putInt("tab pos", tab_pos);
                bundle.putIntegerArrayList("company",com_arr);
                SearchResult f = new SearchResult();
                f.setArguments(bundle);
                FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = mfragmentmanager.beginTransaction();
                ft.replace(R.id.fl_content,f).addToBackStack(null).commit();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });


        page_fund.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));
        tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                page_fund.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Tag, "onCreate()............");
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeMainActivity)getActivity()).setHomeBack();

        ((AppCompatActivity)getActivity()).setSupportActionBar(tb);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("基金搜尋");

        int pos = sharedPreferences.getInt("TabItemPosition",0);
        TabLayout.Tab tab = tab_layout.getTabAt(pos);
        tab.select();

        tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new PageAdapter(getChildFragmentManager(), tab_layout.getTabCount());
        page_fund.setAdapter(adapter);
        //tab_layout.setupWithViewPager(page_fund);
        Log.i(Tag, "onActivityCreate()............");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.edit().putInt("TabItemPosition",tab_layout.getSelectedTabPosition()).commit();
        Log.i(Tag, "onDestroyView()............"+tab_layout.getSelectedTabPosition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Tag, "onDestroy()............");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(Tag, "onDetach()............");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(Tag, "onPause()............");
    }

    @Override
    public void onResume() {
        super.onResume();
        //getChildFragmentManager().popBackStack();
        Log.i(Tag, "onResume()............"+tab_layout.getSelectedTabPosition());
        Log.e("FundContentFragment",getFragmentManager().getClass().getName()+"");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(Tag, "onStop()............");
    }

    @Override
    public void onAttach(Context context) {
        Log.i(Tag, "onAttach()............");
        super.onAttach(context);}

}

