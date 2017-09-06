package com.tencent.newhb.grabings.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.ui.SettingActivity;

public class RechargeGuideDialog extends Dialog {

    public RechargeGuideDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_recharge_guide);

        setCanceledOnTouchOutside(true);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        Button actionButton = (Button) findViewById(R.id.action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(getContext(), SettingActivity.class);
                intent.putExtra(Config.KEY_RECHARGE_GUIDE, "svip");
                getContext().startActivity(intent);
            }
        });
    }
}
