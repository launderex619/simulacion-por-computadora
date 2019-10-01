package com.example.simulaciones.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.simulaciones.Fragment.CircleBressenham;
import com.example.simulaciones.Fragment.CircleDDA;
import com.example.simulaciones.Fragment.Circle_DDA_Bressenham;
import com.example.simulaciones.Helper.Constants;

public class CircleAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public CircleAdapter(Context context, FragmentManager fm, int tabCount) {
        super(fm);
        myContext = context;
        totalTabs = tabCount;
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos){
            case Constants.DDA_LINE_FRAGMENT_PAGE:
                return new CircleDDA();
            case Constants.BRESSENHAM_LINE_FRAGMENT_PAGE:
                return new CircleBressenham();
            case Constants.BRESSENHAM_DDA_LINE_FRAGMENT_PAGE:
                return new Circle_DDA_Bressenham();
            default:
                return new CircleDDA();
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
