package com.example.simulaciones.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.simulaciones.Fragment.Line_DDA_Bressenham;
import com.example.simulaciones.Fragment.LineBressenham;
import com.example.simulaciones.Fragment.LineDDA;
import com.example.simulaciones.Helper.Constants;

import java.util.ArrayList;

public class LineAdapter extends FragmentPagerAdapter {


    private Context myContext;
    FragmentManager fm;
    int totalTabs;
    ArrayList<Fragment> fragments;

    public LineAdapter(Context context, FragmentManager fm, int tabCount) {
        super(fm);
        this.fm = fm;
        myContext = context;
        totalTabs = tabCount;
        fragments = new ArrayList<>();
        fragments.add(new LineDDA());
        fragments.add(new LineBressenham());
        fragments.add(new Line_DDA_Bressenham());

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
