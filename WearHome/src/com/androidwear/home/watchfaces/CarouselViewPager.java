package com.androidwear.home.watchfaces;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class CarouselViewPager extends ViewPager {
	private CarouselPagerAdapter mAdapter;
	private int mInfinitePagingOffset;

	public CarouselViewPager(Context context) {
		super(context);
	}

	private int getOffsetItem(int item) {
		return (item + mInfinitePagingOffset);
	}

	private void recomputeInfinitePagingOffset() {
		if (mAdapter != null) {
			mInfinitePagingOffset = (mAdapter.getCount() / 2);
			mInfinitePagingOffset -= mInfinitePagingOffset
					% mAdapter.getAdapterCount();
		} else {
			mInfinitePagingOffset = 0;
		}
	}

	public void setAdapter(PagerAdapter adapter) {
		mAdapter = new CarouselPagerAdapter(adapter);
		super.setAdapter(mAdapter);
		recomputeInfinitePagingOffset();
		setCurrentItem(0);
	}

	public void setCurrentItem(int item) {
		super.setCurrentItem(getOffsetItem(item));
	}

	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(getOffsetItem(item), smoothScroll);
	}

	private static class CarouselPagerAdapter extends PagerAdapter {
		private final PagerAdapter mAdapter;

		public CarouselPagerAdapter(PagerAdapter pagerAdapter) {
			mAdapter = pagerAdapter;
		}

		private int getAdapterCount() {
			return mAdapter != null ? mAdapter.getCount() : 0;
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			if (mAdapter == null) {
				return;
			}
			int i = position % mAdapter.getCount();
			mAdapter.destroyItem(container, i, object);
		}

		public void finishUpdate(ViewGroup container) {
			if (mAdapter == null) {
				return;
			}
			mAdapter.finishUpdate(container);
		}

		public int getCount() {
			return mAdapter != null ? 100 * this.mAdapter.getCount() : 0;
		}

		public Object instantiateItem(ViewGroup container, int position) {
			if (mAdapter != null) {
				position = position % mAdapter.getCount();
			}
			return mAdapter.instantiateItem(container, position);
		}

		public boolean isViewFromObject(View view, Object object) {
			if (mAdapter != null) {
				return mAdapter.isViewFromObject(view, object);
			} else {
				return false;
			}
		}

		public void restoreState(Parcelable state, ClassLoader loader) {
			if (mAdapter == null) {
				return;
			}
			mAdapter.restoreState(state, loader);
		}

		public Parcelable saveState() {
			if (mAdapter != null) {
				return mAdapter.saveState();
			}
			return null;
		}

		public void startUpdate(ViewGroup container) {
			if (mAdapter == null) {
				return;
			}
			mAdapter.startUpdate(container);
		}
	}
}
