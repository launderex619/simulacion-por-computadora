package com.example.simulaciones.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.example.simulaciones.Fragment.Point;
import com.example.simulaciones.Helper.Constants;

public class PointAdapter extends FragmentStatePagerAdapter {

    private Context myContext;
    int totalTabs;

    public PointAdapter(Context context, FragmentManager fm, int tabCount) {
        super(fm);
        myContext = context;
        totalTabs = tabCount;
    }
    @Override
    public Fragment getItem(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case Constants.POINT_FRAGMENT_PAGE:
                fragment = new Point();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
