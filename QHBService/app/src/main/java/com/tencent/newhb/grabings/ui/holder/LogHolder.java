package com.tencent.newhb.grabings.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tencent.newhb.grabings.R;

public class LogHolder extends RecyclerView.ViewHolder{

    public TextView _sourceTextView;
    public TextView _nicknameTextView;
    public TextView _moneyTextView;
    public TextView _timeTextView;

    public LogHolder(View itemView) {
        super(itemView);

        _sourceTextView = (TextView) itemView.findViewById(R.id.item_log_source);
        _nicknameTextView = (TextView) itemView.findViewById(R.id.item_log_nickname);
        _moneyTextView = (TextView) itemView.findViewById(R.id.item_log_money);
        _timeTextView = (TextView) itemView.findViewById(R.id.item_log_time);
    }
}
