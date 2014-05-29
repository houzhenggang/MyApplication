
package com.huanghua.testdrawerlayouy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewPager mPager;

    private RadioGroup mTabGroup;
    private View mTabDividerLeft;
    private View mTabDividerRight;

    public static final int TAB_COUNT = 3;
    public static final int TAB_ONE = 0;
    public static final int TAB_TWO = 1;
    public static final int TAB_THREE = 2;

    private FragementOne mFragementOne;
    private FragementTwo mFragementTwo;
    private FragementThree mFragementThree;

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            switch (arg0) {
                case TAB_ONE:
                    return new FragementOne();
                case TAB_TWO:
                    return new FragementTwo();
                case TAB_THREE:
                    return new FragementThree();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.ic_drawer_shadow_tablet_am,
                GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer_shadow_tablet_am,
                R.string.app_name,
                R.string.app_name
                ) {
                    public void onDrawerClosed(View view) {
                        getActionBar().setTitle("onDrawerClosed");
                        invalidateOptionsMenu();
                    }

                    public void onDrawerOpened(View drawerView) {
                        getActionBar().setTitle("onDrawerOpened");
                        invalidateOptionsMenu();
                    }
                };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ViewPagerAdapter(this.getFragmentManager()));
        mPager.setOnPageChangeListener(new PageChangeListener());
        mTabGroup = (RadioGroup) findViewById(R.id.tabselect);
        mTabGroup.setOnCheckedChangeListener(new DialtactsRadioGroupChangeListener());
        mTabDividerLeft = findViewById(R.id.tab_divider_left);
        mTabDividerRight = findViewById(R.id.tab_divider_right);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof FragementOne) {
            mFragementOne = (FragementOne) fragment;
        } else if (fragment instanceof FragementTwo) {
            mFragementTwo = (FragementTwo) fragment;
        } else if (fragment instanceof FragementThree) {
            mFragementThree = (FragementThree) fragment;
        }
        super.onAttachFragment(fragment);
    }

    private class PageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            checkRadio(arg0);
        }

    }

    private class DialtactsRadioGroupChangeListener implements OnCheckedChangeListener {

        private int toPosition = -1;

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.tab_one:
                    mPager.setCurrentItem(TAB_ONE, true);
                    toPosition = TAB_ONE;
                    break;
                case R.id.tab_two:
                    mPager.setCurrentItem(TAB_TWO, true);
                    toPosition = TAB_TWO;
                    break;
                case R.id.tab_three:
                    mPager.setCurrentItem(TAB_THREE, true);
                    toPosition = TAB_THREE;
                default:
                    break;
            }

            setTabDivider(toPosition);
        }
    };

    private void checkRadio(int itemId) {
        switch (itemId) {
            case TAB_ONE:
                mTabGroup.check(R.id.tab_one);
                break;
            case TAB_TWO:
                mTabGroup.check(R.id.tab_two);
                break;
            case TAB_THREE:
                mTabGroup.check(R.id.tab_three);
            default:
                break;
        }
        setTabDivider(itemId);
    }

    private void setTabDivider(int currentItem) {
        if (currentItem == TAB_ONE) {
            mTabDividerLeft.setBackgroundResource(R.drawable.tab_normal);
            mTabDividerRight.setBackgroundResource(R.drawable.tab_divider_vertical_gray_emui);
        } else if (currentItem == TAB_TWO) {
            mTabDividerLeft.setBackgroundResource(R.drawable.tab_normal);
            mTabDividerRight.setBackgroundResource(R.drawable.tab_normal);
        } else {
            mTabDividerLeft.setBackgroundResource(R.drawable.tab_divider_vertical_gray_emui);
            mTabDividerRight.setBackgroundResource(R.drawable.tab_normal);
        }
    }

    @Override
    protected void onResume() {
        setCurrentTab(TAB_ONE);
        super.onResume();
    }

    public void setCurrentTab(int tabIndex) {
        mPager.setCurrentItem(tabIndex, false);
        checkRadio(tabIndex);
    }

}
