package com.example.simulaciones.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.simulaciones.Adapter.BezierAdapter;
import com.example.simulaciones.Adapter.CircleAdapter;
import com.example.simulaciones.Adapter.ElipseAdapter;
import com.example.simulaciones.Adapter.LineAdapter;
import com.example.simulaciones.Adapter.PointAdapter;
import com.example.simulaciones.Helper.Constants;
import com.example.simulaciones.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    PointAdapter pointAdapter;
    LineAdapter lineAdapter;
    CircleAdapter circleAdapter;
    ElipseAdapter elipseAdapter;
    BezierAdapter bezierAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        pointAdapter = new PointAdapter(this, getSupportFragmentManager(), Constants.TABS_FOR_POINT);
        lineAdapter = new LineAdapter(this, getSupportFragmentManager(), Constants.TABS_FOR_LINE);
        circleAdapter = new CircleAdapter(this, getSupportFragmentManager(), Constants.TABS_FOR_CIRCLE);
        elipseAdapter = new ElipseAdapter(this, getSupportFragmentManager(), 4);
        bezierAdapter = new BezierAdapter(this, getSupportFragmentManager(), 3);
/////////////////////////////////////////////
        addTabsForPoints();
        viewPager.setAdapter(pointAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TablayoutListener());
/////////////////////////////////////////////


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void addTabsForLine() {
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.line_title_dda)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.line_title_bressenham)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.line_title_bressenham_dda)));
    }

    private void addTabsForPoints() {
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.point_title)));
    }

    private void addTabsForCircle() {
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.circle_title_dda)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.circle_title_bressenham)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.circle_title_bressenham_dda)));
    }

    private void addTabsForElipse() {
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.elipse_title_ddaall)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.elipse_title_ddaquadrant)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.elipse_title_midpoint)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.elipse_title_comparator)));
    }
    private void addTabsForBezier() {
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.bezier_title_independient)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.bezier_title_matrix)));
    }

    private void removeTabs() {
        if (tabLayout.getTabCount() > 0) {
            viewPager.clearFocus();
            tabLayout.removeAllTabs();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Toast.makeText(this, "" + item.toString(), Toast.LENGTH_SHORT).show();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        removeTabs();
        Toast.makeText(this, "" + id, Toast.LENGTH_SHORT).show();
        if (id == R.id.nav_point) {
            addTabsForPoints();
            pointAdapter = new PointAdapter(this, getSupportFragmentManager(), Constants.TABS_FOR_POINT);
            viewPager.setAdapter(pointAdapter);

        } else if (id == R.id.nav_linea) {
            addTabsForLine();
            circleAdapter.clearAll();
            bezierAdapter.clearAll();
            elipseAdapter.clearAll();
            lineAdapter = new LineAdapter(this, getSupportFragmentManager(), Constants.TABS_FOR_LINE);
            viewPager.setAdapter(lineAdapter);

        } else if (id == R.id.nav_circle) {
            addTabsForCircle();
            elipseAdapter.clearAll();
            lineAdapter.clearAll();
            bezierAdapter.clearAll();
            circleAdapter = new CircleAdapter(this, getSupportFragmentManager(), Constants.TABS_FOR_CIRCLE);
            viewPager.setAdapter(circleAdapter);
        } else if (id == R.id.nav_elipse) {
            addTabsForElipse();
            circleAdapter.clearAll();
            lineAdapter.clearAll();
            bezierAdapter.clearAll();
            elipseAdapter = new ElipseAdapter(this, getSupportFragmentManager(), 4);
            viewPager.setAdapter(elipseAdapter);

        } else if (id == R.id.nav_bezier) {
            addTabsForBezier();
            circleAdapter.clearAll();
            elipseAdapter.clearAll();
            lineAdapter.clearAll();
            bezierAdapter = new BezierAdapter(this, getSupportFragmentManager(), 3);
            viewPager.setAdapter(bezierAdapter);

        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class TablayoutListener implements TabLayout.BaseOnTabSelectedListener {


        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
