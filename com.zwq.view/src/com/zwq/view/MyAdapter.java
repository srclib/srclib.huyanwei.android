package com.zwq.view;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyAdapter extends PagerAdapter {

	List<ImageView> mList = null;

	ViewPager vPage = null;

	MyAdapter(List<ImageView> list, ViewPager page) {
		mList = list;
		vPage = page;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = mList.get(position);
		view.setId(position);
		container.addView(view);
		return view;
	}

}
