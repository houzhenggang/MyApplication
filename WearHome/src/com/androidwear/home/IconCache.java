package com.androidwear.home;

import java.util.HashMap;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class IconCache {
	private static final String TAG = "Home.IconCache";

	private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

	private static class CacheEntry {
		public Bitmap icon;
		public String title;
	}

	private final Bitmap mDefaultIcon;
	private HomeApplication mContext;
	private PackageManager mPackageManager;
	private final HashMap<ComponentName, CacheEntry> mCache = new HashMap<ComponentName, CacheEntry>(
			INITIAL_ICON_CACHE_CAPACITY);
	private int mIconDpi;
	private String appBasePath = null;
    private HashMap<String, String> mIconNameMap;

	public IconCache(HomeApplication context) {
		mContext = context;
		mPackageManager = context.getPackageManager();
		mContext = context;
		mPackageManager = context.getPackageManager();
		int density = context.getResources().getDisplayMetrics().densityDpi;
		if (HomeApplication.isScreenLarge()) {
			if (density == DisplayMetrics.DENSITY_LOW) {
				mIconDpi = DisplayMetrics.DENSITY_MEDIUM;
			} else if (density == DisplayMetrics.DENSITY_MEDIUM) {
				mIconDpi = DisplayMetrics.DENSITY_HIGH;
			} else if (density == DisplayMetrics.DENSITY_HIGH) {
				mIconDpi = DisplayMetrics.DENSITY_XHIGH;
			} else if (density == DisplayMetrics.DENSITY_XHIGH) {
				// We'll need to use a denser icon, or some sort of a mipmap
				mIconDpi = DisplayMetrics.DENSITY_XHIGH;
			} else {
				mIconDpi = (int) ((density * 1.5f) + .5f);
			}
		} else {
			mIconDpi = context.getResources().getDisplayMetrics().densityDpi;
		}
		// need to set mIconDpi before getting default icon
		mDefaultIcon = makeDefaultIcon();
	}

	public Drawable getFullResIcon(Resources resources, int iconId) {
		Drawable d;
		try {
			d = resources.getDrawableForDensity(iconId, mIconDpi);
		} catch (Resources.NotFoundException e) {
			d = null;
		}

		return (d != null) ? d : getFullResDefaultActivityIcon();
	}

	public Drawable getFullResIcon(String packageName, int iconId) {
		Resources resources;
		try {
			resources = mPackageManager.getResourcesForApplication(packageName);
		} catch (PackageManager.NameNotFoundException e) {
			resources = null;
		}
		if (resources != null) {
			if (iconId != 0) {
				return getFullResIcon(resources, iconId);
			}
		}
		return getFullResDefaultActivityIcon();
	}

	public Drawable getFullResIcon(ResolveInfo info) {
		Resources resources;
		try {
			resources = mPackageManager
					.getResourcesForApplication(info.activityInfo.applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			resources = null;
		}
		if (resources != null) {
			int iconId = info.activityInfo.getIconResource();
			if (iconId != 0) {
				return getFullResIcon(resources, iconId);
			}
		}
		return getFullResDefaultActivityIcon();
	}

	private Bitmap makeDefaultIcon() {
		Drawable d = getFullResDefaultActivityIcon();
		Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
				Math.max(d.getIntrinsicHeight(), 1), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		d.setBounds(0, 0, b.getWidth(), b.getHeight());
		d.draw(c);
		c.setBitmap(null);
		return b;
	}

	public Drawable getFullResDefaultActivityIcon() {
		return mPackageManager.getDefaultActivityIcon();
	}

	/**
     * Remove any records for the supplied ComponentName.
     */
    public void remove(ComponentName componentName) {
        synchronized (mCache) {
            mCache.remove(componentName);
        }
    }

    /**
     * Empty out the cache.
     */
    public void flush() {
        synchronized (mCache) {
            mCache.clear();
        }
    }

    /**
     * Fill in "application" with the icon and label for "info."
     */
    public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            CacheEntry entry = cacheLocked(application.componentName, info, labelCache);

            application.title = entry.title;
            application.iconBitmap = entry.icon;
        }
    }

    public Bitmap getIcon(Intent intent) {
    		synchronized (mCache) {
                final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
                ComponentName component = intent.getComponent();

                if (resolveInfo == null || component == null) {
                    return mDefaultIcon;
                }

                CacheEntry entry = cacheLocked(component, resolveInfo, null);
                return entry.icon;
            }
        
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            if (resolveInfo == null || component == null) {
                return null;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
            return entry.icon;
        }
    }

    private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();

            mCache.put(componentName, entry);

            String packageName = componentName.getPackageName();
            String className = componentName.getClassName();

            ComponentName key = HomeModel.getComponentNameFromResolveInfo(info);
            if (labelCache != null && labelCache.containsKey(key)) {
                entry.title = labelCache.get(key).toString();              
            } else {
                entry.title = info.loadLabel(mPackageManager).toString();             
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            }
            //if (!Utilities.usingSystemIcons()) {
                if (mIconNameMap != null) {
                    String name = mIconNameMap.get(className);
                    if (name != null && !"".equals(name)) {
                        if(name.equals("empty")){
                            packageName = className;
                        } else {
                            packageName = name;
                        }
                    } else {
                        name = mIconNameMap.get(packageName);
                        if (name != null && !"".equals(name)) {
                            packageName = name;
                        }
                    }
                }
            //}
            if (entry.title == null) {
                entry.title = info.activityInfo.name;
            }

            entry.icon = Utilities.createIconBitmap(getFullResIcon(info), mContext);
        }
        return entry;
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return mDefaultIcon == icon;
    }
}
