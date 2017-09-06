package com.tencent.newhb.grabings.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.entity.PackageLog;
import com.tencent.newhb.grabings.ui.holder.LogHolder;

import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogHolder> {

    private ArrayList<PackageLog> _packageLogs = new ArrayList<>();
    private Context _context;

    public LogAdapter(Context context, ArrayList<PackageLog> packageLogs) {
        this._context = context;
        this._packageLogs = packageLogs;
    }

    @Override
    public LogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LogHolder(LayoutInflater.from(_context).inflate(R.layout.item_log, parent, false));
    }

    @Override
    public void onBindViewHolder(LogHolder holder, int position) {
        if (_packageLogs != null && _packageLogs.size() > 0) {
            holder._sourceTextView.setText(_packageLogs.get(position).getSource());
            holder._nicknameTextView.setText(_packageLogs.get(position).getNickname());
            holder._moneyTextView.setText(_packageLogs.get(position).getMoney());
            holder._timeTextView.setText(_packageLogs.get(position).getTime());
        }
    }

    @Override
    public int getItemCount() {
        return _packageLogs.size();
    }

    //添加数据
    public void setData(List<PackageLog> newDatas) {
        _packageLogs.clear();
        _packageLogs.addAll(newDatas);
        notifyDataSetChanged();
    }
}
