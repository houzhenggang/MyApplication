/**
 * 
 */
package com.android.keyguard;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.widget.LockPatternUtils;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.Handler.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.provider.Contacts;
import android.provider.ContactsContract;



/******************************************************************************
 * Class name:  YunlanUnlockView.java	  Created  2014-4-4  娑撳宕�:05:42      
 * Description: TODO 
 * Company:     濞ｅ崬婀锋禍鎴ｎ瀲缁夋垶濡ч張澶愭閸忣剙寰�
 * Department:  鏉烆垯娆㈠锟藉絺娴滃绗熼柈锟� * @author      xuyt@yunlauncher.com
 * @version     1.0
 * --------------------------------------------------------------------------
 * 娣囶喗鏁奸崢鍡楀蕉                                                                                                                                                                                                         
 * 鎼村繐褰� 閺冦儲婀� 娣囶喗鏁兼禍锟� 娣囶喗鏁奸崢鐔锋礈
 * 1
 *---------------------------------------------------------------------------
 ****************************************************************************
 */


public class YunlanUnlockView extends KeyguardViewBase implements KeyguardSecurityView{

	private boolean DEBUG=true;
	private String TAG = "YunlanUnlockView";
	private KeyguardSecurityCallback mCallback = null;
	private KeyguardActivityLauncher mActivityLauncher = null;
	private boolean mIsRegisted = false;
	public boolean mBInit = false;
    
	public YunlanUnlockView(Context context){
		super(context);
	}
	
	public YunlanUnlockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public YunlanUnlockView(Context context, Configuration configuration,
			KeyguardSecurityCallback callback, BitmapDrawable bgBmpDrawable, 
			Integer iPreLoadType, KeyguardActivityLauncher activityLauncher) { 
		super(context);
		mCallback = callback;
		mActivityLauncher = activityLauncher;
//		Bitmap bitmapScreen = PhoneWindowManager.getScreenShotBmp();
//		if (DEBUG) Log.d(TAG,"-----yunlan------YunlanUnlockView bitmapScreen ====" + bitmapScreen);
		View view = getCurScreen(context, configuration, bgBmpDrawable, iPreLoadType);
		if (view != null) {
			addView(view);
		}
	}

	@Override
	public void onScreenTurnedOff() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScreenTurnedOn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyUnlock() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		KeyguardUpdateMonitor.getInstance(getContext()).removeCallback(mYLUpdateMonitorCallback);
		mIsRegisted = false;
		if(mCleanUp != null) {
			try {
				mCleanUp.invoke(mView,  new Object[0]);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public long getUserActivityTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setKeyguardCallback(KeyguardSecurityCallback callback) {
		// TODO Auto-generated method stub
		mCallback = callback;
	}

	@Override
	public void setLockPatternUtils(LockPatternUtils utils) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		KeyguardUpdateMonitor.getInstance(getContext()).removeCallback(mYLUpdateMonitorCallback);
		mIsRegisted = false;
		if(mOnPause != null) {
			try {
				mOnPause.invoke(mView, new Object[0]);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResume(int reason) {
		// TODO Auto-generated method stub
		if (mBInit == false) {
			return;
		}
		
		if(!mIsRegisted) {
			KeyguardUpdateMonitor.getInstance(getContext()).registerCallback(mYLUpdateMonitorCallback);	
			mIsRegisted = true;
		}
	    boolean isShowing = KeyguardUpdateMonitor.getInstance(mContext).isKeyguardVisible();
	    if(mOnResume != null) {
			try {
				mOnResume.invoke(mView, new Object[]{reason, isShowing});
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean needsInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public KeyguardSecurityCallback getCallback() {
		// TODO Auto-generated method stub
		return mCallback;
	}

	@Override
	public void showUsabilityHint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showBouncer(int duration) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void hideBouncer(int duration) {
		// TODO Auto-generated method stub
		
	}
    
    private View mView = null;
    private Method mOnResume = null;
    private Method mOnPause = null;
    private Method mCleanUp = null;
    private Method mOnRefreshBatteryInfo=null;
    private Method mOnTimeChanged=null;
    private Method mOnKeyguardVisibilityChanged = null;
    
    
	public View getCurScreen(Context context, Configuration configuration,
			BitmapDrawable preloadBGBmp, int preloadType) {
		Context mmsCtx = null;
		try {
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen 1");
			mmsCtx = context.createPackageContext("com.yunlan.syslockmarket", 
			        Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen 2");
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen NameNotFoundException");
			
			return null;
		}
		Class<?> maClass = null;
		try {
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen 3");
			maClass = Class.forName("com.yunlan.syslockmarket.YLLockScreenView", true, mmsCtx.getClassLoader());
			if (DEBUG) Log.d(TAG,"wang-------------------------------getCurScreen thememarket 4");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen 4 ClassNotFoundException");
			return null;
		}
		
		Class[] params = new Class[6];
		params[0] = Context.class;
		params[1] = Context.class;
		params[2] = Configuration.class;
		params[3] = BitmapDrawable.class;
		params[4] = Integer.class;
		params[5] = Bitmap.class;
		Constructor<?> con = null;
		try {
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen 5 preloadBGBmp="+preloadBGBmp+"  preloadType="+preloadType);
				
			con = maClass.getConstructor(params);
			mView = (View)con.newInstance(mmsCtx, context, configuration, preloadBGBmp, preloadType, KeyguardService.getScreenShotBmp());
			Method f = maClass.getDeclaredMethod("getInitFinish", new Class[0]);
			mBInit = (Boolean)f.invoke(mView, new Object[0]);
			
			if(!mBInit){
				mView=null;
				return mView;
			}
			f = maClass.getDeclaredMethod("setIntentCallBack", new Class[]{Callback.class});
			f.invoke(mView, new Object[]{mYunlanCallback});
			
			
			mOnResume = maClass.getDeclaredMethod("onResume", new Class[]{int.class, boolean.class});	
			mOnPause = maClass.getDeclaredMethod("onPause", new Class[0]);
			mCleanUp = maClass.getDeclaredMethod("cleanUp", new Class[0]);
			mOnRefreshBatteryInfo = maClass.getDeclaredMethod("onRefreshBatteryInfo", 
					new Class[]{boolean.class, int.class, int.class, int.class, int.class});
			mOnTimeChanged=maClass.getDeclaredMethod("onTimeChanged", new Class[0]);
			mOnKeyguardVisibilityChanged = maClass.getDeclaredMethod("onKeyguardVisibilityChanged", new Class[]{boolean.class}); ;
			
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen 6");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen err1"+ e.getMessage());
			return null;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen err2" + e.getMessage());
			return null;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen err3"+ e.getMessage());
			return null;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen err4"+ e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen err5"+ e.getMessage());
			return null;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen err6"+ e.getMessage());
			return null;
		}
		if (DEBUG) Log.d(TAG,"-----yunlan------getCurScreen 7");
		return mView;
	}
	
    private Callback mYunlanCallback = new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Intent intent = null;
			switch (msg.what) {
			case 0: 
				break;
			case 1: 
				intent = new Intent(Intent.ACTION_DIAL);
				break;
			case 2: 
				intent = new Intent(Intent.ACTION_MAIN);
				intent.setType("vnd.android-dir/mms-sms");
				break;
			case 3: 
				Bundle bundle = msg.getData();
				String string = bundle.getString("url");
				if (string != null) {
					Uri uri = Uri.parse(string);
					intent = new Intent(Intent.ACTION_VIEW, uri);
				} else {
					intent = new Intent();
					intent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
				}
				break;
			case 4:  
				intent = new Intent();
				intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
				break;

			case 5: 
				intent = new Intent();
				intent.setAction("android.intent.action.MUSIC_PLAYER");
				break;

			case 6: 
			    intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				break;
				
			case 100:
				mCallback.userActivity(10000);
				return false;
			
			default:
				break;
			}
			if(intent != null){
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mActivityLauncher.launchActivityWithAnimation(intent, false, null, null, null);
			}
			mCallback.dismiss(false);
			return false;
		}
	};
	
	private KeyguardUpdateMonitorCallback mYLUpdateMonitorCallback = new YLKeyguardUpdateMonitorCallback();
	class YLKeyguardUpdateMonitorCallback extends KeyguardUpdateMonitorCallback {
		protected void onKeyguardVisibilityChanged(boolean showing) {
			//fixed bug for LGunlock when ringing
			if(mOnKeyguardVisibilityChanged !=null){
				try {
					mOnKeyguardVisibilityChanged.invoke(mView, new Object[]{showing});
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		protected void onRefreshBatteryInfo(KeyguardUpdateMonitor.BatteryStatus batteryStatus) {
			
			super.onRefreshBatteryInfo(batteryStatus);

			//if (batteryStatus.index < 1) {
				try {
					if(mOnRefreshBatteryInfo != null)
						mOnRefreshBatteryInfo.invoke(mView, new Object[]{batteryStatus.isPluggedIn(),
								batteryStatus.level, batteryStatus.status,  100});
					
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}
		}

		protected void onTimeChanged() {
			super.onTimeChanged();
			if(mOnTimeChanged !=null)
				try {
					mOnTimeChanged.invoke(mView, new Object[0]);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		

		
	}
	
	public boolean isInit(){
		return mBInit;
	}
	
	public boolean isAlarmUnlockScreen() {
		return false;
	}
	
	public void wakeWhenReadyTq(int arg0) {
	}
}
