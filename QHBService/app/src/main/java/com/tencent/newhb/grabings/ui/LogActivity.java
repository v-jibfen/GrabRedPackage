package com.tencent.newhb.grabings.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tencent.newhb.grabings.BaseActivity;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.database.DBHelper;
import com.tencent.newhb.grabings.entity.PackageLog;
import com.tencent.newhb.grabings.ui.adapter.LogAdapter;
import com.tencent.newhb.grabings.widget.DividerItemDecoration;
import com.tencent.newhb.grabings.widget.NavigationTopBar;

import java.util.ArrayList;

public class LogActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Context _context;
    private NavigationTopBar _navigationTopBar;
    private RecyclerView _logRecyclerView;
    private LogAdapter _logAdapter;
    private DBHelper _DBHelper;
    private SwipeRefreshLayout _swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        _context = LogActivity.this;

        _navigationTopBar = (NavigationTopBar) getSupportFragmentManager().findFragmentByTag(NavigationTopBar.class.getSimpleName());
        _navigationTopBar.setText(NavigationTopBar.NAVIGATION_VIEWS.TITLE, R.string.log_text);
        _navigationTopBar.setViewVisibility(NavigationTopBar.NAVIGATION_VIEWS.RIGHT, View.GONE);

        _navigationTopBar.setClickListener(NavigationTopBar.NAVIGATION_VIEWS.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        _swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.log_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);
        _logRecyclerView = (RecyclerView) findViewById(R.id.log_recycler_view);
        _logRecyclerView.setLayoutManager(new LinearLayoutManager(_context));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(_context, LinearLayoutManager.VERTICAL, R.drawable.recycle_item_divider);
        _logRecyclerView.addItemDecoration(itemDecoration);

        _logAdapter = new LogAdapter(_context, getLogsFromDB());
        _logRecyclerView.setAdapter(_logAdapter);
    }

    private ArrayList<PackageLog> getLogsFromDB() {
        if (_DBHelper == null) {
            _DBHelper = new DBHelper(_context);
        }

        return _DBHelper.getLogList();
    }

    @Override
    public void onRefresh() {
        _logAdapter.setData(getLogsFromDB());
        _swipeRefreshLayout.setRefreshing(false);
    }
}
