
package com.innova.onekeyinstall;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyUtil {
    public final static SimpleDateFormat SIMPLE_DATE_FOTMAT;

    static {
        SIMPLE_DATE_FOTMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    public static Bitmap readBitMap(String filePath, Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(filePath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = filePath;
            appInfo.publicSourceDir = filePath;
            Drawable drawable = appInfo.loadIcon(pm);
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        } else {
            return null;
        }
    }

    public static Intent getIntentByFileType(Context context, File file) {
        if (file == null) {
            return null;
        }
        if (!file.exists() || !file.canRead()) {
            return null;
        }
        Intent intent = null;
        intent = getIntent(file, "application/vnd.android.package-archive");
        if (!isIntentAvailable(context, intent)) {
            intent = null;
        }
        return intent;
    }

    private static Intent getIntent(File file, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), type);
        return intent;
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }

    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return appInfo.loadIcon(pm);
        }
        return null;
    }

    public static String getApkName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return appInfo.loadLabel(pm).toString();
        }
        return "";
    }
    
    public static String getPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return appInfo.packageName;
        }
        return "";
    }

    public static String getAllInstallApp(Context context) {
        String result = "";
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo i : packages) {
            result += i.applicationInfo.packageName + ",";
            Log.i("huanghua", "result:" + result);
        }
        return result.substring(0, result.length() - 1);
    }
}
