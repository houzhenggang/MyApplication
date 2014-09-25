package com.androidwear.home;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

public class HomeModel extends BroadcastReceiver{

	static final String TAG = "Home.Model";

	static final boolean DEBUG_LOADERS = false;
	static final boolean DEBUG_LOADERS_REORDER = false;

	private final Object mLock = new Object();

	private HomeApplication mApp = null;
	private IconCache mIconCache = null;
	private AllAppsList mAllAppsList;
	private LoaderTask mLoaderTask;
	private Bitmap mDefaultIcon;

	protected int mPreviousConfigMcc;
	private int mBatchSize; // 0 is all apps at once
	protected float mFontscale;
	private boolean mAllAppsLoaded;
    private int mAllAppsLoadDelay; // milliseconds between batches
	private boolean mAppsCanBeOnExternalStorage;

	private DeferredHandler mHandler = new DeferredHandler();
	private static final HandlerThread sWorkerThread = new HandlerThread(
			"launcher-loader");
	static {
		sWorkerThread.start();
	}
	private static final Handler sWorker = new Handler(
			sWorkerThread.getLooper());

	
	private WeakReference<Callbacks> mCallbacks;

	public interface Callbacks {
		public boolean setLoadOnResume();
		public void startBinding();
		public void bindAllApplications(ArrayList<ApplicationInfo> apps);
		public void bindAppsAdded(ArrayList<ApplicationInfo> apps);
		public void bindAppsUpdated(ArrayList<ApplicationInfo> apps);
		public void bindAppsRemoved(ArrayList<ApplicationInfo> apps,
				boolean permanent);
		public void finishBindingItems();
	}

	public HomeModel(HomeApplication app, IconCache iconCache) {
		mAppsCanBeOnExternalStorage = !Environment.isExternalStorageEmulated();
		mApp = app;
		mIconCache = iconCache;
		mAllAppsList = new AllAppsList(iconCache, this);
        mIconCache = iconCache;

        mDefaultIcon = Utilities.createIconBitmap(
                mIconCache.getFullResDefaultActivityIcon(), app);

        final Resources res = app.getResources();
        mAllAppsLoadDelay = res.getInteger(R.integer.config_allAppsBatchLoadDelay);
        mBatchSize = res.getInteger(R.integer.config_allAppsBatchSize);
        Configuration config = res.getConfiguration();
        mPreviousConfigMcc = config.mcc;
	}

	public void initialize(Callbacks callbacks) {
        synchronized (mLock) {
            mCallbacks = new WeakReference<Callbacks>(callbacks);
        }
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final String action = intent.getAction();

		if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
				|| Intent.ACTION_PACKAGE_REMOVED.equals(action)
				|| Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			final String packageName = intent.getData().getSchemeSpecificPart();
			final boolean replacing = intent.getBooleanExtra(
					Intent.EXTRA_REPLACING, false);

			int op = PackageUpdatedTask.OP_NONE;

			if (packageName == null || packageName.length() == 0) {
				// they sent us a bad intent
				return;
			}

			if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
				op = PackageUpdatedTask.OP_UPDATE;
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				if (!replacing) {
					op = PackageUpdatedTask.OP_REMOVE;
				}
			} else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				if (!replacing) {
					op = PackageUpdatedTask.OP_ADD;
				} else {
					op = PackageUpdatedTask.OP_UPDATE;
				}
			}

			if (op != PackageUpdatedTask.OP_NONE) {
				enqueuePackageUpdated(new PackageUpdatedTask(op,
						new String[] { packageName }));
			}

		} else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
			// First, schedule to add these apps back in.
			String[] packages = intent
					.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
			enqueuePackageUpdated(new PackageUpdatedTask(
					PackageUpdatedTask.OP_ADD, packages));
			// Then, rebind everything.
			startLoaderFromBackground();
		} else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE
				.equals(action)) {
			String[] packages = intent
					.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
			enqueuePackageUpdated(new PackageUpdatedTask(
					PackageUpdatedTask.OP_UNAVAILABLE, packages));
		} else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
			// If we have changed locale we need to clear out the labels in all
			// apps/workspace.
            mIconCache.flush();
			forceReload();
        } else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(action)) {
			// Check if configuration change was an mcc/mnc change which would
			// affect app resources
			// and we would need to clear out the labels in all apps/workspace.
			// Same handling as
			// above for ACTION_LOCALE_CHANGED
			Configuration currentConfig = context.getResources()
					.getConfiguration();
			if (mPreviousConfigMcc != currentConfig.mcc
					|| mFontscale != currentConfig.fontScale) {
				Log.d(TAG, "Reload apps on config change. curr_mcc:"
						+ currentConfig.mcc + " prevmcc:" + mPreviousConfigMcc
						+ " curr_fontscale:" + currentConfig.fontScale
						+ " prevfont:" + mFontscale);
				forceReload();
			}
			// Update previousConfig
			mPreviousConfigMcc = currentConfig.mcc;
			mFontscale = currentConfig.fontScale;
		}
	}

	public void startLoaderFromBackground() {
        boolean runLoader = false;
        if (mCallbacks != null) {
            Callbacks callbacks = mCallbacks.get();
            if (callbacks != null) {
                // Only actually run the loader if they're not paused.
                if (!callbacks.setLoadOnResume()) {
                    runLoader = true;
                }
            }
        }
        if (runLoader) {
            startLoader(mApp, false);
        }
    }

	private void forceReload() {
        synchronized (mLock) {
            // Stop any existing loaders first, so they don't set mAllAppsLoaded or
            // mWorkspaceLoaded to true later
            stopLoaderLocked();
            mAllAppsLoaded = false;
        }        
        if (DEBUG_LOADERS) {
            Log.d(TAG, "forceReload mLoaderTask =" + mLoaderTask + ",mAllAppsLoaded = "
                    + mAllAppsLoaded + ",this = " + this);
        }        
        // Do this here because if the launcher activity is running it will be restarted.
        // If it's not running startLoaderFromBackground will merely tell it that it needs
        // to reload.
        startLoaderFromBackground();
    }

	public void startLoader(Context context, boolean isLaunching) {
        synchronized (mLock) {
            if (DEBUG_LOADERS) {
                Log.d(TAG, "startLoader isLaunching=" + isLaunching + ",mCallbacks = " + mCallbacks);
            }

            // Don't bother to start the thread if we know it's not going to do anything
            if (mCallbacks != null && mCallbacks.get() != null) {
                // If there is already one running, tell it to stop.
                // also, don't downgrade isLaunching if we're already running
                isLaunching = isLaunching || stopLoaderLocked();
                mLoaderTask = new LoaderTask(context, isLaunching);
                sWorkerThread.setPriority(Thread.NORM_PRIORITY);
                sWorker.post(mLoaderTask);
            }
        }
    }

	private boolean stopLoaderLocked() {
        boolean isLaunching = false;
        LoaderTask oldTask = mLoaderTask;
        if (oldTask != null) {
            if (oldTask.isLaunching()) {
                isLaunching = true;
            }
            oldTask.stopLocked();
        }
        if (DEBUG_LOADERS) {
            Log.d(TAG, "stopLoaderLocked mLoaderTask =" + mLoaderTask + ",isLaunching = "
                    + isLaunching + ",this = " + this);
        }
        return isLaunching;
    }

	void enqueuePackageUpdated(PackageUpdatedTask task) {
        sWorker.post(task);
    }

	private class PackageUpdatedTask implements Runnable {
		int mOp;
		String[] mPackages;

		public static final int OP_NONE = 0;
		public static final int OP_ADD = 1;
		public static final int OP_UPDATE = 2;
		public static final int OP_REMOVE = 3; // uninstlled
		public static final int OP_UNAVAILABLE = 4; // external media unmounted

		public PackageUpdatedTask(int op, String[] packages) {
			mOp = op;
			mPackages = packages;
		}

		public void run() {
			final Context context = mApp;

			final String[] packages = mPackages;
			final int N = packages.length;
			switch (mOp) {
			case OP_ADD:
				for (int i = 0; i < N; i++) {
					if (DEBUG_LOADERS)
						Log.d(TAG, "mAllAppsList.addPackage " + packages[i]);
					mAllAppsList.addPackage(context, packages[i]);
				}
				break;
			case OP_UPDATE:
				for (int i = 0; i < N; i++) {
					if (DEBUG_LOADERS)
						Log.d(TAG, "mAllAppsList.updatePackage " + packages[i]);
					mAllAppsList.updatePackage(context, packages[i]);
				}
				break;
			case OP_REMOVE:
			case OP_UNAVAILABLE:
				for (int i = 0; i < N; i++) {
					if (DEBUG_LOADERS)
						Log.d(TAG, "mAllAppsList.removePackage " + packages[i]);
					mAllAppsList.removePackage(packages[i]);
				}
				break;
			}

			ArrayList<ApplicationInfo> added = null;
			ArrayList<ApplicationInfo> removed = null;
			ArrayList<ApplicationInfo> modified = null;

			if (mAllAppsList.added.size() > 0) {
				added = mAllAppsList.added;
				mAllAppsList.added = new ArrayList<ApplicationInfo>();
			}
			if (mAllAppsList.removed.size() > 0) {
				removed = mAllAppsList.removed;
				mAllAppsList.removed = new ArrayList<ApplicationInfo>();
				for (ApplicationInfo info : removed) {
					mIconCache.remove(info.intent.getComponent());
				}
			}
			if (mAllAppsList.modified.size() > 0) {
				modified = mAllAppsList.modified;
				mAllAppsList.modified = new ArrayList<ApplicationInfo>();
			}

			final Callbacks callbacks = mCallbacks != null ? mCallbacks.get()
					: null;
			if (callbacks == null) {
				Log.w(TAG,
						"Nobody to tell about the new app.  Launcher is probably loading.");
				return;
			}

			if (added != null) {
				final ArrayList<ApplicationInfo> addedFinal = added;
				mHandler.post(new Runnable() {
					public void run() {
						Callbacks cb = mCallbacks != null ? mCallbacks.get()
								: null;
						if (callbacks == cb && cb != null) {
							callbacks.bindAppsAdded(addedFinal);
						}
					}
				});
			}
			if (modified != null) {
				final ArrayList<ApplicationInfo> modifiedFinal = modified;
				mHandler.post(new Runnable() {
					public void run() {
						Callbacks cb = mCallbacks != null ? mCallbacks.get()
								: null;
						if (callbacks == cb && cb != null) {
							callbacks.bindAppsUpdated(modifiedFinal);
						}
					}
				});
			}
			if (removed != null) {
				final boolean permanent = mOp != OP_UNAVAILABLE;
				final ArrayList<ApplicationInfo> removedFinal = removed;
				mHandler.post(new Runnable() {
					public void run() {
						Callbacks cb = mCallbacks != null ? mCallbacks.get()
								: null;
						if (callbacks == cb && cb != null) {
							callbacks.bindAppsRemoved(removedFinal, permanent);
						}
					}
				});
			}
		}
	}

	private class LoaderTask implements Runnable{

        private Context mContext;
        private Thread mWaitThread;
        private boolean mIsLaunching;
        private boolean mStopped;
        private boolean mLoadAndBindStepFinished;
        private HashMap<Object, CharSequence> mLabelCache;

        LoaderTask(Context context, boolean isLaunching) {
            mContext = context;
            mIsLaunching = isLaunching;
            mLabelCache = new HashMap<Object, CharSequence>();
            if (DEBUG_LOADERS) {
                Log.d(TAG, "LoaderTask construct: mLabelCache = " + mLabelCache +
                        ",mIsLaunching = " + mIsLaunching + ",this = " + this);
            }
        }

        boolean isLaunching() {
            return mIsLaunching;
        }

        private void waitForIdle() {
            // Wait until the either we're stopped or the other threads are done.
            // This way we don't start loading all apps until the workspace has settled
            // down.
            synchronized (LoaderTask.this) {
                final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				if (DEBUG_LOADERS) {
					Log.d(TAG, "waitForIdle start, workspaceWaitTime : "
							+ workspaceWaitTime + "ms, Thread priority :"
							+ Thread.currentThread().getPriority() + ",this = " + this);
				}
                mHandler.postIdle(new Runnable() {
                        public void run() {
                            synchronized (LoaderTask.this) {
                                mLoadAndBindStepFinished = true;
                                if (DEBUG_LOADERS) {
                                    Log.d(TAG, "done with previous binding step");
                                }
                                LoaderTask.this.notify();
                            }
                        }
                    });

                while (!mStopped && !mLoadAndBindStepFinished) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        // Ignore
                    }
                }
                if (DEBUG_LOADERS) {
                    Log.d(TAG, "waited "
                            + (SystemClock.uptimeMillis()-workspaceWaitTime)
                            + "ms for previous step to finish binding");
                }
            }
        }

        public void run() {
            // Optimize for end-user experience: if the Launcher is up and // running with the
            // All Apps interface in the foreground, load All Apps first. Otherwise, load the
            // workspace first (default).

            keep_running: {
                // Elevate priority when Home launches for the first time to avoid
                // starving at boot time. Staring at a blank home is not cool.
                synchronized (mLock) {
                    if (DEBUG_LOADERS) Log.d(TAG, "Setting thread priority to " +
                            (mIsLaunching ? "DEFAULT" : "BACKGROUND"));
                    android.os.Process.setThreadPriority(mIsLaunching
                            ? android.os.Process.THREAD_PRIORITY_DEFAULT : android.os.Process.THREAD_PRIORITY_BACKGROUND);
                }
                if (DEBUG_LOADERS) Log.d(TAG, "step 1: special: loading all apps, this = " + this);
                loadAndBindAllApps();
                
                //first load workspace and all apps
                if (DEBUG_LOADERS) Log.d(TAG, "First: step2: loading all apps, this = " + this);
                onlyLoadAllApps();

                if (mStopped) {
                    break keep_running;
                }

                // Whew! Hard work done.  Slow us down, and wait until the UI thread has
                // settled down.
                synchronized (mLock) {
                    if (mIsLaunching) {
                        if (DEBUG_LOADERS) Log.d(TAG, "Setting thread priority to BACKGROUND");
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    }
                }
                waitForIdle();

                if (DEBUG_LOADERS) Log.d(TAG, "Second step:2  bindAllApps, this = " + this);
                onlyBindAllApps();

                // Restore the default thread priority after we are done loading items
                synchronized (mLock) {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
                }
            }

            mContext = null;

            synchronized (mLock) {
                // If we are still the last one to be scheduled, remove ourselves.
                if (mLoaderTask == this) {
                    mLoaderTask = null;
                }
            }
        }

        public void stopLocked() {
            synchronized (LoaderTask.this) {
                mStopped = true;
                this.notify();
            }
            if (DEBUG_LOADERS) {
                Log.d(TAG, "stopLocked completed, this = " + LoaderTask.this 
                        + ",mLoaderTask = " + mLoaderTask);
            }
        }

        /**
         * Gets the callbacks object.  If we've been stopped, or if the launcher object
         * has somehow been garbage collected, return null instead.  Pass in the Callbacks
         * object that was around when the deferred message was scheduled, and if there's
         * a new Callbacks object around then also return null.  This will save us from
         * calling onto it with data that will be ignored.
         */
        Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
            synchronized (mLock) {
                if (mStopped) {
                    return null;
                }

                if (mCallbacks == null) {
                    return null;
                }

                final Callbacks callbacks = mCallbacks.get();
                if (callbacks != oldCallbacks) {
                    return null;
                }
                if (callbacks == null) {
                    Log.w(TAG, "no mCallbacks");
                    return null;
                }

                return callbacks;
            }
        }

        private void loadAndBindAllApps() {
            if (!mAllAppsLoaded) {
                loadAllAppsByBatch();
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                    mAllAppsLoaded = true;
                }
            } else {
                onlyBindAllApps();
            }
        }
        
        private void onlyLoadAllApps() {
        	if (DEBUG_LOADERS) {
                Log.d(TAG, "onlyLoadAllApps mAllAppsLoaded =" + mAllAppsLoaded + ",this = " + this);
            }
            if (!mAllAppsLoaded) {
                loadAllAppsByBatch();
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                    mAllAppsLoaded = true;
                }
            } 
        }

        private void onlyBindAllApps() {
            final Callbacks oldCallbacks = mCallbacks.get();
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher (onlyBindAllApps)");
                return;
            }
            
            if (DEBUG_LOADERS) {
                Log.d(TAG, "onlyBindAllApps oldCallbacks =" + oldCallbacks + ",this = " + this);
            }

            mHandler.post(new Runnable() {
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.startBinding();
                    }
                }
            });

            // shallow copy
            final ArrayList<ApplicationInfo> list = (ArrayList<ApplicationInfo>) mAllAppsList.data.clone();
            mHandler.post(new Runnable() {
                public void run() {
                    final long t = SystemClock.uptimeMillis();
                    final Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.bindAllApplications(list);
                    }
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "bound all " + list.size() + " apps from cache in "
                                + (SystemClock.uptimeMillis()-t) + "ms, this = " + this);
                    }
                }
            });

            // Tell the workspace that we're done.
            mHandler.post(new Runnable() {
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.finishBindingItems();
                    }
                }
            });

        }

        private void loadAllAppsByBatch() {
            final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

            // Don't use these two variables in any of the callback runnables.
            // Otherwise we hold a reference to them.
            final Callbacks oldCallbacks = mCallbacks.get();
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher (loadAllAppsByBatch)");
                return;
            }

            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            final PackageManager packageManager = mContext.getPackageManager();
            List<ResolveInfo> apps = null;

            int N = Integer.MAX_VALUE;

            int startIndex;
            int i=0;
            int batchSize = -1;
            while (i < N && !mStopped) {
                if (i == 0) {
                    mAllAppsList.clear();
                    final long qiaTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
                    apps = packageManager.queryIntentActivities(mainIntent, 0);
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "queryIntentActivities took "
                                + (SystemClock.uptimeMillis()-qiaTime) + "ms");
                    }
                    if (apps == null) {
                        return;
                    }
                    N = apps.size();
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "queryIntentActivities got " + N + " apps, mBatchSize = "
                                + mBatchSize + ",this = " + this);
                    }
                    if (N == 0) {
                        // There are no apps?!?
                        return;
                    }
                    if (mBatchSize == 0) {
                        batchSize = N;
                    } else {
                        batchSize = mBatchSize;
                    }

                    /*
                     * If locale changed, we need to clear icon cache and label
                     * cache before we get the right label cache, this can make
                     * sure the next step to add application to list will cache
                     * the right label.
                     */
                    flushCacheIfNeeded(mLabelCache);
                    final long sortTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
                    Collections.sort(apps,
                            new HomeModel.ShortcutNameComparator(packageManager, mLabelCache));
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "sort took " + (SystemClock.uptimeMillis() - sortTime) + "ms"
                                + ",this = " + this);
                    }
                }

                final long t2 = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
                startIndex = i;
                for (int j=0; i<N && j<batchSize; j++) {
                    // This builds the icon bitmaps.
                	ApplicationInfo info = new ApplicationInfo(packageManager, apps.get(i), mIconCache, mLabelCache);
                    mAllAppsList.add(info);
                    i++;
                }

                if (DEBUG_LOADERS) {
                    Log.d(TAG, "batch of " + (i-startIndex) + " icons processed in "
                            + (SystemClock.uptimeMillis()-t2) + "ms");
                }

                if (mAllAppsLoadDelay > 0 && i < N) {
                    try {
                        if (DEBUG_LOADERS) {
                            Log.d(TAG, "sleeping for " + mAllAppsLoadDelay + "ms");
                        }
                        Thread.sleep(mAllAppsLoadDelay);
                    } catch (InterruptedException exc) { }
                }
            }

            if (DEBUG_LOADERS) {
                Log.d(TAG, "cached all " + N + " apps in "
                        + (SystemClock.uptimeMillis()-t) + "ms"
                        + (mAllAppsLoadDelay > 0 ? " (including delay)" : ""));
            }
        }

        public void dumpState() {
            Log.d(TAG, "mLoaderTask.mContext=" + mContext);
            Log.d(TAG, "mLoaderTask.mWaitThread=" + mWaitThread);
            Log.d(TAG, "mLoaderTask.mIsLaunching=" + mIsLaunching);
            Log.d(TAG, "mLoaderTask.mStopped=" + mStopped);
            Log.d(TAG, "mLoaderTask.mLoadAndBindStepFinished=" + mLoadAndBindStepFinished);
            Log.d(TAG, "mItems size=" + mAllAppsList.size());
        }
	}

private boolean sForceFlushCache = false;    
    
    public AllAppsList getAllAppsList() {
    	return mAllAppsList;
    }

    /**
     * Set flush cache.
     */
    synchronized void setFlushCache() {
        sForceFlushCache = true;
    }
    
    /**
     * Flush icon cache and label cache if locale has been changed.
     * 
     * @param labelCache label cache.
     */
    synchronized void flushCacheIfNeeded(HashMap<Object, CharSequence> labelCache) {
        if (sForceFlushCache) {
            labelCache.clear();
            mIconCache.flush();
            sForceFlushCache = false;
        }   
    }

	private static Collator sCollator = Collator.getInstance();
    public static void resetCollator() {
    	sCollator = Collator.getInstance();
    }
 
	public static class ShortcutNameComparator implements Comparator<ResolveInfo> {
        private PackageManager mPackageManager;
        private HashMap<Object, CharSequence> mLabelCache;
        ShortcutNameComparator(PackageManager pm) {
            mPackageManager = pm;
            mLabelCache = new HashMap<Object, CharSequence>();
        }
        ShortcutNameComparator(PackageManager pm, HashMap<Object, CharSequence> labelCache) {
            mPackageManager = pm;
            mLabelCache = labelCache;
        }
        public final int compare(ResolveInfo a, ResolveInfo b) {
            CharSequence labelA, labelB;
            ComponentName keyA = HomeModel.getComponentNameFromResolveInfo(a);
            ComponentName keyB = HomeModel.getComponentNameFromResolveInfo(b);
            if (mLabelCache.containsKey(keyA)) {
                labelA = mLabelCache.get(keyA);
            } else {
                labelA = a.loadLabel(mPackageManager).toString();

                mLabelCache.put(keyA, labelA);
            }
            if (mLabelCache.containsKey(keyB)) {
                labelB = mLabelCache.get(keyB);
            } else {
                labelB = b.loadLabel(mPackageManager).toString();

                mLabelCache.put(keyB, labelB);
            }
            return sCollator.compare(labelA, labelB);
        }
    };

    public static class NameComparator implements Comparator<ApplicationInfo> {
        NameComparator() {

        }
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
            return sCollator.compare(a.title, b.title);
        }
    };

	public static ComponentName getComponentNameFromResolveInfo(ResolveInfo info) {
		if (info.activityInfo != null) {
			return new ComponentName(info.activityInfo.packageName,
					info.activityInfo.name);
		} else {
			return new ComponentName(info.serviceInfo.packageName,
					info.serviceInfo.name);
		}
	}
}
