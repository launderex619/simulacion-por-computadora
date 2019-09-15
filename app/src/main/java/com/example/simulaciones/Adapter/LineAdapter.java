package com.example.simulaciones.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.example.simulaciones.Fragment.DDA_Bressenham;
import com.example.simulaciones.Fragment.LineBressenham;
import com.example.simulaciones.Fragment.LineDDA;
import com.example.simulaciones.Helper.Constants;

public class LineAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public LineAdapter(Context context, FragmentManager fm, int tabCount) {
        super(fm);
        myContext = context;
        totalTabs = tabCount;
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos){
            case Constants.DDA_LINE_FRAGMENT_PAGE:
                return new LineDDA();
            case Constants.BRESSENHAM_LINE_FRAGMENT_PAGE:
                return new LineBressenham();
            case Constants.BRESSENHAM_DDA_LINE_FRAGMENT_PAGE:
                return new DDA_Bressenham();
            default:
                return new LineDDA();
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
