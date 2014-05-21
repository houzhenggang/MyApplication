
package com.innova.onekeyinstall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.innova.onekeyinstall.FileAdapter.EmptyViewListener;

import java.io.File;

public class MainActivity extends Activity implements OnItemClickListener {

    private ListView mListView;
    private View mEmptyView;
    private QuickScanCursorAdapter mAdapter;
    private Context mContext;
    private int mSortBy;
    private boolean mIsFirstRun = false;

    public static final int MENU_SORT = 1;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mIsFirstRun = true;
        mListView = (ListView) findViewById(R.id.overview_file_list);
        mEmptyView = findViewById(R.id.overview_empty_view);
        mAdapter = new QuickScanCursorAdapter(this,
                QuickScanCursorAdapter.RESOURCE_APK, mEmptyView, mListView, false);

        mAdapter.setEmptyViewListener(new EmptyViewListener() {
            @Override
            public void onEmptyStateChanged(boolean isEmpty) {
                if (mAdapter != null && !mAdapter.mIsLoading) {
                    if (isEmpty) {
                        mListView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mListView.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                    }
                }
            }
        });
        mListView.setAdapter(mAdapter);
        //mListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter == null || mAdapter.mIsLoading) {
            return;
        }
        File file = mAdapter.getFileList().get(position);
        Intent intent = MyUtil.getIntentByFileType(this, file);
        if (intent != null) {
            this.startActivity(intent);
        } else {
            Toast.makeText(view.getContext(), R.string.msg_invalid_intent, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdapter != null) {
            mAdapter.destroyThread();
            mAdapter = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (mAdapter != null && !mAdapter.mIsLoading && !mIsFirstRun) {
            mAdapter.updateAllApp();
            mAdapter.notifyDataSetChanged();
        }
        mIsFirstRun = false;
        super.onResume();
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, MENU_SORT, 0, R.string.menu_sort_type);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SORT:
                AlertDialog.Builder sortTypeDialog = new AlertDialog.Builder(mContext);
                sortTypeDialog.setTitle(R.string.menu_sort_type);
                final int sortType;
                final SharedPreferences settings = mContext.getSharedPreferences(
                        FileSort.FILE_SORT_KEY, 0);
                sortType = settings.getInt(FileSort.FILE_SORT_KEY, FileSort.SORT_BY_NAME);
                int selectItem = FileSort.getSelectItemByType(sortType);
                mSortBy = selectItem;
                sortTypeDialog.setSingleChoiceItems(R.array.sort_type, selectItem,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mSortBy = whichButton;
                            }
                        });
                sortTypeDialog.setNegativeButton(R.string.sort_by_asc,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                switch (mSortBy) {
                                    case 0:
                                        mSortBy = FileSort.SORT_BY_NAME;
                                        break;
                                    case 1:
                                        mSortBy = FileSort.SORT_BY_SIZE_ASC;
                                        break;
                                }
                                settings.edit().putInt(FileSort.FILE_SORT_KEY, mSortBy).commit();
                                FileSort.getFileListSort().setSortType(mSortBy);
                                if (mSortBy == FileSort.SORT_BY_TYPE) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            FileSort.getFileListSort().sort(mAdapter.getFileList());
                                            handler.post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    mAdapter.notifyDataSetChanged();
                                                }

                                            });
                                        }
                                    }).start();

                                } else {
                                    mAdapter.refresh();
                                }
                            }
                        });
                sortTypeDialog.setPositiveButton(R.string.sort_by_desc,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                switch (mSortBy) {
                                    case 0:
                                        mSortBy = FileSort.SORT_BY_NAME_DESC;
                                        break;
                                    case 1:
                                        mSortBy = FileSort.SORT_BY_SIZE_DESC;
                                        break;
                                }
                                settings.edit().putInt(FileSort.FILE_SORT_KEY, mSortBy).commit();
                                FileSort.getFileListSort().setSortType(mSortBy);
                                if (mSortBy == FileSort.SORT_BY_TYPE_DESC) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            FileSort.getFileListSort().sort(mAdapter.getFileList());
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    mAdapter.refresh();
                                }
                            }
                        });
                sortTypeDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
