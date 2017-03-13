package com.sourcey.materiallogindemo.ui;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseUser;
import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.view.FragmentDrawer;
import com.sourcey.materiallogindemo.view.SlidingTabLayout;
import com.sourcey.materiallogindemo.adapters.ViewPageAdapterHome;

public class Home extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    ViewPager pager;
    CharSequence Titles[]={"PERFIL", "AMIGOS", "BUSCAR AMIGOS", "MASCOTAS"};
    int Numboftabs = 4;
    SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {

        }

        FragmentDrawer drawerFragment = (FragmentDrawer)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        ViewPageAdapterHome viewPageAdapterHome = new ViewPageAdapterHome(getSupportFragmentManager(), Titles, Numboftabs);

        pager = (ViewPager)findViewById(R.id.pager_home);
        pager.setAdapter(viewPageAdapterHome);

        tabs = (SlidingTabLayout)findViewById(R.id.tabs_home);
        tabs.setDistributeEvenly(true);
        tabs.setBackgroundResource(R.color.primary);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });

        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        switch (position){
            case 0:{
                Intent logout = new Intent(this, Home.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
                break;
            }
            case 1:{
                Intent account = new Intent(this, Account.class);
                startActivity(account);
                break;
            }
            case 2:{
                ParseUser.logOut();
                Intent logout = new Intent(this, LoginActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
                break;
            }
            default:break;
        }
    }
}
