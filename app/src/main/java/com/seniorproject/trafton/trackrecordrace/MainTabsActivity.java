package com.seniorproject.trafton.trackrecordrace;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseUser;
import com.seniorproject.trafton.trackrecordrace.ChallengeFragment;

public class MainTabsActivity extends AppCompatActivity {

    //necessary UI objects
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    //loads all necessary UI components
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    ParseUser currentUser = ParseUser.getCurrentUser();

    //Inflate the menu for this Activity
    //includes the run button as well as the settings button
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("XXX","Menu created");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_tabs, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Handles possibilities of menu items being selected. Opens new activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_run:
                //Begin a new RunActivity
               Intent intent = new Intent(
                        MainTabsActivity.this,
                        MapsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                //begin a new ChangeSettingsActivity
                return true;
            case R.id.action_logout:
                //log the user out and close the application
                ParseUser.logOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //set up each fragment to be used with the ViewPager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment(), "Profile");
        adapter.addFragment(new StatsFragment(), "Statistics");
        adapter.addFragment(new ChallengeFragment(), "Challenges");
        viewPager.setAdapter(adapter);
    }

    //ViewPager allows for User to scroll through each tab with a simple swipe
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



}
