package com.tencent.newhb.grabings.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tencent.newhb.grabings.R;

public class SettingHolder extends RecyclerView.ViewHolder{

    public View _itemView;
    public RelativeLayout _rootView;
    public TextView _typeTextView;
    public SwitchCompat _typeSwitchView;
    public LinearLayout _actionView;
    public Spinner _spinnerView;

    public SettingHolder(View itemView) {
        super(itemView);

        _itemView = itemView;
        _rootView = (RelativeLayout) itemView.findViewById(R.id.item_setting_root);
        _typeTextView = (TextView) itemView.findViewById(R.id.item_setting_type);
        _typeSwitchView = (SwitchCompat) itemView.findViewById(R.id.item_setting_switch);
        _actionView = (LinearLayout) itemView.findViewById(R.id.action_view);
        _spinnerView = (Spinner) itemView.findViewById(R.id.item_setting_spinner);

    }
}
