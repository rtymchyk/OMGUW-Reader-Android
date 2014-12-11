package com.romantymchyk.omguwreader.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.romantymchyk.omguwreader.R;
import com.romantymchyk.omguwreader.fragments.PostsFragment;
import com.romantymchyk.omguwreader.utilities.ChangeLog;

public class HomeActivity extends FragmentActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ActionBar actionBar = getActionBar();
        setupActionBar(actionBar);

        initializePaging(actionBar, savedInstanceState);

        ChangeLog changeLog = new ChangeLog(this);
        if (changeLog.firstRun()) changeLog.getLogDialog().show();
    }

    private void setupActionBar(ActionBar actionBar) {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setTitle("Posts");

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTextView = (TextView) findViewById(titleId);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
        actionBarTextView.setTypeface(typeFace);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mSectionsPagerAdapter != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.putFragment(outState, "OMG", mSectionsPagerAdapter.getItem(0));
            fragmentManager.putFragment(outState, "MC", mSectionsPagerAdapter.getItem(1));
            fragmentManager.putFragment(outState, "OH", mSectionsPagerAdapter.getItem(2));
            fragmentManager.putFragment(outState, "ASK", mSectionsPagerAdapter.getItem(3));
        }
    }

    private void initializePaging(final ActionBar actionBar, Bundle savedInstanceState) {
        List<Fragment> fragments = new ArrayList<Fragment>();

        Fragment omgPostsFragment = null;
        Fragment mcPostsFragment = null;
        Fragment ohPostsFragment = null;
        Fragment askPostsFragment = null;

        if (savedInstanceState != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            omgPostsFragment = fragmentManager.getFragment(savedInstanceState, "OMG");
            mcPostsFragment = fragmentManager.getFragment(savedInstanceState, "MC");
            ohPostsFragment = fragmentManager.getFragment(savedInstanceState, "OH");
            askPostsFragment = fragmentManager.getFragment(savedInstanceState, "ASK");
        } else {
            omgPostsFragment = Fragment.instantiate(this, PostsFragment.class.getName());
            Bundle omgArgs = new Bundle();
            omgArgs.putString(PostsFragment.BLOG_TYPE_KEY, getString(R.string.section_omg));
            omgPostsFragment.setArguments(omgArgs);
            omgPostsFragment.setRetainInstance(true);

            mcPostsFragment = Fragment.instantiate(this, PostsFragment.class.getName());
            Bundle mcArgs = new Bundle();
            mcArgs.putString(PostsFragment.BLOG_TYPE_KEY, getString(R.string.section_mc));
            mcPostsFragment.setArguments(mcArgs);
            mcPostsFragment.setRetainInstance(true);

            ohPostsFragment = Fragment.instantiate(this, PostsFragment.class.getName());
            Bundle ohArgs = new Bundle();
            ohArgs.putString(PostsFragment.BLOG_TYPE_KEY, getString(R.string.section_oh));
            ohPostsFragment.setArguments(ohArgs);
            ohPostsFragment.setRetainInstance(true);

            askPostsFragment = Fragment.instantiate(this, PostsFragment.class.getName());
            Bundle askArgs = new Bundle();
            askArgs.putString(PostsFragment.BLOG_TYPE_KEY, getString(R.string.section_ask));
            askPostsFragment.setArguments(askArgs);
            askPostsFragment.setRetainInstance(true);
        }

        fragments.add(omgPostsFragment);
        fragments.add(mcPostsFragment);
        fragments.add(ohPostsFragment);
        fragments.add(askPostsFragment);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);

        mViewPager = (ViewPager) super.findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PostsFragment currentPostsFragment;

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                currentPostsFragment = (PostsFragment) mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentPostsFragment.refreshPosts();
                return true;
            case R.id.action_top:
                currentPostsFragment = (PostsFragment) mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentPostsFragment.scrollToTop();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.section_omg);
                case 1:
                    return getString(R.string.section_mc);
                case 2:
                    return getString(R.string.section_oh);
                case 3:
                    return getString(R.string.section_ask);
            }

            return null;
        }
    }

}
