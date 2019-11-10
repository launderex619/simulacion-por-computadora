package com.example.simulaciones.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.simulaciones.Fragment.CircleBressenham;
import com.example.simulaciones.Fragment.CircleDDA;
import com.example.simulaciones.Fragment.Circle_DDA_Bressenham;
import com.example.simulaciones.Fragment.ElipseComparators;
import com.example.simulaciones.Fragment.ElipseDDAall;
import com.example.simulaciones.Fragment.ElipseDDAquadrant;
import com.example.simulaciones.Fragment.ElipseMidPoint;
import com.example.simulaciones.Helper.Constants;

import java.util.ArrayList;

public class ElipseAdapter extends FragmentPagerAdapter {


    private Context myContext;
    FragmentManager fm;
    int totalTabs;
    ArrayList<Fragment> fragments;

    public ElipseAdapter(Context context, FragmentManager fm, int tabCount) {
        super(fm);
        this.fm = fm;
        myContext = context;
        totalTabs = tabCount;
        fragments = new ArrayList<>();
        fragments.add(new ElipseDDAall());
        fragments.add(new ElipseDDAquadrant());
        fragments.add(new ElipseMidPoint());
        fragments.add(new ElipseComparators());

    }

    public void clearAll() //Clear all page
    {
        for(int i=0; i<fragments.size(); i++)
            fm.beginTransaction().remove(fragments.get(i)).commit();
        fragments.clear();
    }

    @Override
    public Fragment getItem(int pos) {
        return fragments.get(pos);
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
