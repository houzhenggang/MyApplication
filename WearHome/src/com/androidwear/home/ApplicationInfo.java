package com.androidwear.home;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.util.Log;

public class ApplicationInfo  extends ItemInfo{
	private static final String TAG = "ApplicationInfo";
	 /**
    * The intent used to start the application.
    */
   public Intent intent;

   /**
    * A bitmap version of the application icon.
    */
   public Bitmap iconBitmap;

   /**
    * The time at which the app was first installed.
    */
   public long firstInstallTime;

   public ComponentName componentName;

   static final int DOWNLOADED_FLAG = 1;
   static final int UPDATED_SYSTEM_APP_FLAG = 2;

   public int flags = 0;

   ApplicationInfo() {

   }

   /**
    * Must not hold the Context.
    */
   public ApplicationInfo(PackageManager pm, ResolveInfo info, IconCache iconCache,
           HashMap<Object, CharSequence> labelCache) {
       final String packageName = info.activityInfo.applicationInfo.packageName;

       this.componentName = new ComponentName(packageName, info.activityInfo.name);
       this.container = ItemInfo.NO_ID;
       this.setActivity(componentName,
               Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

       try {
           int appFlags = pm.getApplicationInfo(packageName, 0).flags;
           if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
               flags |= DOWNLOADED_FLAG;

               if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                   flags |= UPDATED_SYSTEM_APP_FLAG;
               }
           }
           firstInstallTime = pm.getPackageInfo(packageName, 0).firstInstallTime;
       } catch (NameNotFoundException e) {
           Log.d(TAG, "PackageManager.getApplicationInfo failed for " + packageName);
       }

       iconCache.getTitleAndIcon(this, info, labelCache);
       updateHideInMenu(info.activityInfo);
   }

   public ApplicationInfo(PackageManager pm, ResolveInfo info, IconCache iconCache,
                          HashMap<Object, CharSequence> labelCache, boolean isLauncher) {
       final String packageName = info.activityInfo.applicationInfo.packageName;

       this.componentName = new ComponentName(packageName, info.activityInfo.name);
       this.container = ItemInfo.NO_ID;
       if(isLauncher){
           this.setActivity(componentName,
                   Intent.FLAG_ACTIVITY_NEW_TASK);
       }else{
           this.setActivity(componentName,
                   Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
       }


       try {
           int appFlags = pm.getApplicationInfo(packageName, 0).flags;
           if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
               flags |= DOWNLOADED_FLAG;

               if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                   flags |= UPDATED_SYSTEM_APP_FLAG;
               }
           }
           firstInstallTime = pm.getPackageInfo(packageName, 0).firstInstallTime;
       } catch (NameNotFoundException e) {
           Log.d(TAG, "PackageManager.getApplicationInfo failed for " + packageName);
       }

       iconCache.getTitleAndIcon(this, info, labelCache);
       updateHideInMenu(info.activityInfo);
   }

   public ApplicationInfo(ApplicationInfo info) {
       super(info);
       componentName = info.componentName;
       title = info.title.toString();
       intent = new Intent(info.intent);
       flags = info.flags;
       firstInstallTime = info.firstInstallTime;
   }

   /**
    * Creates the application intent based on a component name and various launch flags.
    * Sets {@link #itemType} to {@link LauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
    *
    * @param className the class name of the component representing the intent
    * @param launchFlags the launch flags
    */
   final void setActivity(ComponentName className, int launchFlags) {
       intent = new Intent(Intent.ACTION_MAIN);
       intent.addCategory(Intent.CATEGORY_LAUNCHER);
       intent.setComponent(className);
       intent.setFlags(launchFlags);
   }

   @Override
   public String toString() {
       return "ApplicationInfo(title=" + title.toString() + ")";
   }

   public static void dumpApplicationInfoList(String tag, String label,
           ArrayList<ApplicationInfo> list) {
       Log.d(tag, label + " size=" + list.size());
       for (ApplicationInfo info: list) {
           Log.d(tag, "   title=\"" + info.title + "\" iconBitmap="
                   + info.iconBitmap + " firstInstallTime="
                   + info.firstInstallTime);
       }
   }

   public void copyFrom(ApplicationInfo info){
       iconBitmap = info.iconBitmap;
       intent = info.intent;
       componentName = info.componentName;
       itemType = info.itemType;
       title = info.title;
       flags = info.flags;
       firstInstallTime = info.firstInstallTime;
   }

   public static class DisplayNameComparator implements Comparator<ApplicationInfo> {
       public DisplayNameComparator(PackageManager pm) {
           mPM = pm;
       }

       public final int compare(ApplicationInfo a, ApplicationInfo b) {
           CharSequence sa = a.title;
           if (sa == null)
               sa = a.componentName.getClassName();
           CharSequence sb = b.title;
           if (sb == null)
               sb = b.componentName.getClassName();

           return sCollator.compare(sa.toString(), sb.toString());
       }

       private final Collator sCollator = Collator.getInstance();

       private PackageManager mPM;
   }

   private void updateHideInMenu(ActivityInfo aInfo){
   	if (aInfo != null && aInfo.metaData != null){
   		isHideInMenu = aInfo.metaData.getBoolean("com.google.android.wearable.app.HIDE_IN_MENU", false);
   	}
   }
}
