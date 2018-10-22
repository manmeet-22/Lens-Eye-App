package com.manmeet.lenseye;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.manmeet.lenseye.adapters.HistoryAdapter;
import com.manmeet.lenseye.database.HistoryContract;
import com.manmeet.lenseye.database.HistoryProvider;
import com.manmeet.lenseye.model.History;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID_HISTORY = 1;
    @BindView(R.id.history_recycler_view)
    RecyclerView mHistoryRecyclerView;
    HistoryAdapter mHistoryAdapter;
    ArrayList<History> mHistoryList;
    GridLayoutManager mGridLayoutManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");
        ButterKnife.bind(this);
        mHistoryList = new ArrayList<History>();
        mGridLayoutManager = new GridLayoutManager(this,2);
        mHistoryRecyclerView.setLayoutManager(mGridLayoutManager);
        getSupportLoaderManager().initLoader(LOADER_ID_HISTORY, null, this);
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, HistoryContract.HistoryEntry.CONTENT_URI,
                null, null, null, null);

    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        mHistoryAdapter = new HistoryAdapter(this, data);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {

    }
}
