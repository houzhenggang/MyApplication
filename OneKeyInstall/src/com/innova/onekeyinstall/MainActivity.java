
package com.innova.onekeyinstall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

}
