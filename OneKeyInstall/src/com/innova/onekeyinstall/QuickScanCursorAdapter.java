package com.innova.onekeyinstall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.innova.loadimage.ImageCache;

public class QuickScanCursorAdapter extends FileAdapter implements View.OnClickListener {
    
    public static final int RESOURCE_APK = 5;

    private Activity mContext;

    private int mResourceType;

    private int mPathColumnIndex;
    
    private ListView mListView;
    
    private ProgressDialog mWaitDialog;
    
    private View mEmptyView;
    
    private boolean mTimeup = false;

    private volatile boolean mLoadFinished = false;

    private int mPosition = 0;

    private Handler mWorkingThread;

    private SharedPreferences mSettings;

    private FileExplorerScrollListener mScrollListener;
    
    private FileExplorerImageLoadCompleteListener mLoadCompleteListener;
    
    private Drawable mDefaultApkIcon;
    private String mAllInstallApp = "";
    
    public QuickScanCursorAdapter(Activity context, int resourceType,
            String orderStr) {
        super(context);
        mWorkingThread = new Handler(mHandlerThread.getLooper());
        mResourceType = resourceType;
        mContext = context;
        mDefaultApkIcon = mContext.getResources().getDrawable(R.drawable.file_item_apk_default_ic);

        mSettings = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        mAllInstallApp = MyUtil.getAllInstallApp(context);
        refresh();
    }

    public QuickScanCursorAdapter(Activity context, int resourceType,
            View emptyView, ListView listview, boolean isSelectMode) {
        super(context);
        mWorkingThread = new Handler(mHandlerThread.getLooper());
        mResourceType = resourceType;
        mContext = context;
        mDefaultApkIcon = mContext.getResources().getDrawable(R.drawable.file_item_apk_default_ic);

        mListView = listview;
        mLoadCompleteListener = new FileExplorerImageLoadCompleteListener(mListView);
        mScrollListener = new FileExplorerScrollListener(mListView);
        mListView.setOnScrollListener(mScrollListener);
        mWaitDialog = showWaitDialog();
        WaitTimer mTimer = new WaitTimer(500,500);
        mTimer.start();
        mEmptyView = emptyView;
        mSettings = PreferenceManager.getDefaultSharedPreferences(mContext);
        mAllInstallApp = MyUtil.getAllInstallApp(context);
        refresh();
    }
    
    public QuickScanCursorAdapter(Activity context, int resourceType,
            int position, ListView listview, boolean isSelectMode) {
        super(context);
        mResourceType = resourceType;
        mContext = context;
        mDefaultApkIcon = mContext.getResources().getDrawable(R.drawable.file_item_apk_default_ic);

        mWorkingThread = new Handler(mHandlerThread.getLooper());
        mListView = listview;
        mLoadCompleteListener = new FileExplorerImageLoadCompleteListener(mListView);
        mScrollListener = new FileExplorerScrollListener(mListView);
        mListView.setOnScrollListener(mScrollListener);
        mWaitDialog = showWaitDialog();
        WaitTimer mTimer = new WaitTimer(500,500);
        mTimer.start();
        mPosition = position;
        mSettings = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        mAllInstallApp = MyUtil.getAllInstallApp(context);
        refresh();
    }

    class LoadFinishedRunnable implements Runnable {
        private ArrayList<File> mAcceptList = new ArrayList<File>();
        private ArrayList<File> mWorkingList;// = new ArrayList<File>();
        volatile boolean mIsLoading = false;
        volatile boolean mIsDirty = false;

        /*
         * boolean display = mSettings.getBoolean("display_hide_file", false);
         */

        private void reload() {
            synchronized (mAcceptList) {
                mWorkingList = (ArrayList<File>) mAcceptList.clone();
                mIsLoading = true;
                mIsDirty = false;
                mWorkingThread.post(this);
            }

        }

        public void changeCursor(Cursor cursor) {
            if (cursor != null && cursor.isClosed()) {
                refresh();
                return;
            }
            synchronized (mAcceptList) {
                mAcceptList.clear();
                if (cursor != null && cursor.getCount() > 0) {
                    mPathColumnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(mPathColumnIndex);
                        File file = new File(path);
                        mAcceptList.add(file);
                    }
                }
                if (mIsLoading) {
                    mWorkingList = (ArrayList<File>) mAcceptList.clone();
                    mIsDirty = true;
                } else {
                    mWorkingList = (ArrayList<File>) mAcceptList.clone();
                    mIsLoading = true;
                    mIsDirty = false;
                    mWorkingThread.removeCallbacks(this);
                    mWorkingThread.postDelayed(this, 500);
                }
            }
        }

        /**
         * Time casting action, put it into thread.
         * 
         * @param file
         *            , the file you want to check, you can give a null file
         *            into here, but suggest not to do that
         * @param isShowHide
         *            Whether show hidden which set by {@code SharedPreferences}
         * @return a boolean value if need to put into {@code ArrayList}
         */
        private boolean isValid(File file, boolean isShowHide) {
            if (file == null) {
                return false;
            }
            return file.exists() && (isShowHide ? true : !file.isHidden());
        }

        @Override
        public void run() {
            ArrayList<File> invalidFiles = new ArrayList<File>();
            mLoadFinished = false;
            synchronized (mWorkingList) {
                boolean isShowHide = mSettings.getBoolean("display_hide_file", false);
                for (File f: mWorkingList) {
                    if (!isValid(f, isShowHide)) {
                        if (null != f) {
                            invalidFiles.add(f);
                        }
                    }
                }
                if (!invalidFiles.isEmpty()) {
                    for(File file:invalidFiles) {
                        mWorkingList.remove(file);
                    }
                }
                SharedPreferences sortPreference = mContext
                        .getSharedPreferences(FileSort.FILE_SORT_KEY, 0);
                FileSort sorter = FileSort.getFileListSort();
                sorter.setSortType(sortPreference.getInt(
                        FileSort.FILE_SORT_KEY, FileSort.SORT_BY_NAME));
                sorter.sortDefault(mWorkingList);
                mLoadFinished = true;
                mMainThreadHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (!mLoadFinished) {
                            return;
                        }
                        QuickScanCursorAdapter.this.mIsLoading = true;
                        synchronized (mWorkingList) {
                            mFileList.clear();
                            for (File f : mWorkingList) {
                                mFileList.add(f);
                            }
                        }
                        mIsLoading = false;
                        QuickScanCursorAdapter.this.mIsLoading = false;
                        notifyDataSetChanged();
                        if (mWaitDialog != null && mWaitDialog.isShowing()
                                && mTimeup) {
                            mWaitDialog.dismiss();
                            mListView.setEmptyView(mEmptyView);
                            mListView.setSelection(mPosition);
                        }

                    }
                });
                if (mIsDirty) {
                    reload();
                }
            }

        };
    }
    private LoadFinishedRunnable mFinishedRunnable = new LoadFinishedRunnable();
    
    private LoaderManager.LoaderCallbacks<Cursor> mFileLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>(){
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            mListView.setEmptyView(null);
            String[] projection = new String[] { MediaColumns._ID,
                    MediaColumns.DATA, MediaColumns.TITLE,
                    MediaColumns.DATE_MODIFIED, MediaColumns.SIZE};
            String selection = null;
            Uri uri = null;
            final SharedPreferences sortPreference =  mContext.getSharedPreferences(
                    FileSort.FILE_SORT_KEY, 0);
            int sort = sortPreference.getInt(FileSort.FILE_SORT_KEY, FileSort.SORT_BY_NAME);

            String sortBy = FileSort.getOrderStr(sort);
            switch (mResourceType) {
            case RESOURCE_APK:
                uri = MediaStore.Files.getContentUri("external");
                selection = MediaStore.Files.FileColumns.DATA + " like '%.apk'";
                break;
            }
            return new FileLoader(mContext, uri, projection, selection,sortBy);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
            if (cursor != null && !cursor.isClosed()) {
                mFinishedRunnable.changeCursor(cursor);
            }
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

    };

    public void refresh() {
        final SharedPreferences sortPreference =  mContext.getSharedPreferences(
                FileSort.FILE_SORT_KEY, 0);
        int sort = sortPreference.getInt(FileSort.FILE_SORT_KEY, FileSort.SORT_BY_NAME);
        int id = mResourceType * 10 + sort;
        mContext.getLoaderManager().restartLoader(id, null, mFileLoaderListener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ViewHolder vHolder = (ViewHolder) view.getTag(R.id.action_paste_mode_cancel);
        File file = mFileList.get(position);
        view.setTag(file.getPath());
        Bitmap bitmap = null;
        switch (mResourceType) {
        case RESOURCE_APK:
            if (mScrolling) {
                bitmap = ImageCache.get(file.getPath());
            } else {
                bitmap = ImageCache.get(file.getPath());
                if (null == bitmap) {
                        ImageCache.loadImageBitmap(mContext, file.getPath(), mLoadCompleteListener,
                                mMainThreadHandler, position);
                }
            }
            if (null == bitmap) {
                vHolder.fileIcon.setImageDrawable(mDefaultApkIcon);
            } else {
                vHolder.fileIcon.setImageBitmap(bitmap);
            }
            String packageName = MyUtil.getPackageName(mContext, file.getPath());
            if (mAllInstallApp.contains(packageName)) {
                vHolder.installButton.setEnabled(false);
                vHolder.installButton.setText(R.string.installed);
            } else {
                vHolder.installButton.setOnClickListener(this);
                vHolder.installButton.setEnabled(true);
                vHolder.installButton.setText(R.string.install);
            }
            vHolder.fileName.setText(MyUtil.getApkName(mContext, file.getPath()));
        }
/*            if(isChecked(position)){
                vHolder.installButton.setChecked(true);
            }else{
                vHolder.installButton.setChecked(false);
            }*/
        vHolder.fileIcon.setClickable(false);
        return view;
    }

    public Uri getFileUri(int position){
        Uri uri = MediaStore.Files.getContentUri("external");
        Cursor cursor = mContext.getContentResolver().query(uri, new String[]{MediaStore.Files.FileColumns._ID}, MediaStore.Files.FileColumns.DATA + "=?", new String[]{getFile(position).getPath()}, null);
        if (cursor == null || cursor.getCount() == 0) {
            if (cursor != null) {
                cursor.close();
            }
            notifyDataSetChanged();
            return null;
        }
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        cursor.close();
        return ContentUris.withAppendedId(uri, id);
    }
    
    public File getFile(int position){
        return mFileList.get(position);
    }
    

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.install_button:
            if (mIsLoading) {
                return;
            }
            File file = (File) v.getTag();
            Intent intent = MyUtil.getIntentByFileType(mContext, file);
            if (intent != null) {
                mContext.startActivity(intent);
            } else {
                Toast.makeText(mContext, R.string.msg_invalid_intent, Toast.LENGTH_SHORT)
                        .show();
            }
            break;
        }
    }

    public List<File> getFileList(){
        return mFileList;
    }
    private ProgressDialog showWaitDialog(){
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setTitle(R.string.dialog_hint_title);
        dialog.setMessage(mContext.getResources().getString(R.string.dialog_hint_msg));
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }
    static class FileLoader extends CursorLoader{
        public FileLoader(Context context, Uri uri, String[] projection, String selection, String sort) {
            super(context, uri, projection, selection, null, sort);
        }
    }
    
    @Override
    public void destroyThread() {
        if (mContext instanceof Activity) {
            if (!mContext.isFinishing() && mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
        }
        mWaitDialog = null;
        super.destroyThread();
    }

    public void updateAllApp() {
        mAllInstallApp = MyUtil.getAllInstallApp(mContext);
    }
    
    class WaitTimer extends CountDownTimer{
        public WaitTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            
        }

        @Override
        public void onFinish() {
            mTimeup = true;
            if(mWaitDialog != null && mWaitDialog.isShowing() && mLoadFinished){
                mWaitDialog.dismiss();
                mListView.setEmptyView(mEmptyView);
                mListView.setSelection(mPosition);
            }
        }
    }
}
