package com.jonli.fundkeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Li on 2016/11/26.
 **/

public class News extends Fragment {

    private View view;
    private TabLayout news_tab_layout;
    private ViewPager news_page;
    private Pageadapter adapter;
    private String Tag = "News";
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news, container, false);
        initView();
        setHasOptionsMenu(true);
        return view;
    }

    private void initView(){
        news_page = (ViewPager) view.findViewById(R.id.news_page);
        news_tab_layout = (TabLayout) view.findViewById(R.id.news_tab_layout);

        news_tab_layout.addTab(news_tab_layout.newTab().setText("頭條新聞"));
        news_tab_layout.addTab(news_tab_layout.newTab().setText("基金焦點"));
        news_tab_layout.addTab(news_tab_layout.newTab().setText("外匯市場"));
        news_tab_layout.addTab(news_tab_layout.newTab().setText("國際股市"));

        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(Tag,"onStart");

        news_page.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(news_tab_layout));
        news_tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TabSelected",tab.getPosition()+"");
                news_page.setCurrentItem(tab.getPosition());
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
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Tag, "onCreate()............");
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("新聞");

        news_tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);

        int pos = sharedPreferences.getInt("TabItemPosition",0);
        TabLayout.Tab tab = news_tab_layout.getTabAt(pos);
        tab.select();

        adapter = new Pageadapter (getChildFragmentManager());

        adapter.addFragment(new PageNewInstant());
        adapter.addFragment(new PageNewFund());
        adapter.addFragment(new PageNewFx());
        adapter.addFragment(new PageNewStock());

        news_page.setAdapter(adapter);
        Log.i(Tag, "onActivityCreate()............");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.edit().putInt("TabItemPosition",news_tab_layout.getSelectedTabPosition()).commit();
        Log.i(Tag, "onDestroyView()............");
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

        Log.i(Tag, "onResume()............");
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
