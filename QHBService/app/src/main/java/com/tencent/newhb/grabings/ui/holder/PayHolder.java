package com.tencent.newhb.grabings.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.widget.CustomCheckBox;

/**
 * Created by MSI05 on 2017/5/12.
 */
public class PayHolder extends RecyclerView.ViewHolder{

    public RelativeLayout mContentView;
    public ImageView mLogoView;
    public TextView mTextView;
    public CustomCheckBox mCheckBox;

    public PayHolder(View itemView) {
        super(itemView);
        mContentView = (RelativeLayout) itemView.findViewById(R.id.item_pay_content);
        mLogoView = (ImageView) itemView.findViewById(R.id.item_pay_logo);
        mTextView = (TextView) itemView.findViewById(R.id.item_pay_text);
        mCheckBox = (CustomCheckBox) itemView.findViewById(R.id.item_pay_checkbox);
    }
}
