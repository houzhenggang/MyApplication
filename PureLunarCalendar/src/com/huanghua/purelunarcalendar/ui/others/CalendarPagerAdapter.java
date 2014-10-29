package com.huanghua.purelunarcalendar.ui.others;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.huanghua.purelunarcalendar.module.LunarCalendar;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter {

	public CalendarPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return CalendarPagerFragment.create(position);
	}

	@Override
	public int getCount() {
		int years = LunarCalendar.getMaxYear() - LunarCalendar.getMinYear();
		return years * 12;
	}

}
