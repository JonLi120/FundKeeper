package com.jonli.fundkeeper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private FragmentManager fragmentmanager;
    public PageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        fragmentmanager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("getitem",position+"");

        switch (position) {
            case 0:
               PageTabCompany tab1 = new PageTabCompany();
                return tab1;
            case 1:
                PageTabClass tab2 = new PageTabClass();
                return tab2;
            case 2:
                PageTabPerformance tab3 = new PageTabPerformance();
                return tab3;
            case 3:
                PageTabAdvanced tab4 = new PageTabAdvanced();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    /*@Override
    public int getItemPosition(Object object) {
        Log.d("getItemPosition",""+object);
        return PageAdapter.POSITION_NONE;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Log.v("instantiateItem","=================="+b+position);
        Fragment fragment = (Fragment)super.instantiateItem(container,position);
        if(b == true){
            Log.v("instantiateItem","------------------"+b+position);
            FragmentTransaction ft = fragmentmanager.beginTransaction();
            ft.remove(fragment);
            ft.add(container.getId(),fragment);
            ft.show(fragment);
            ft.commit();
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
            b = false;
        }
        return fragment;
    }*/

}