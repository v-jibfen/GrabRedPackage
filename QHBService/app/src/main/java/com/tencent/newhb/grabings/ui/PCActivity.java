package com.tencent.newhb.grabings.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.newhb.grabings.AppHelper;
import com.tencent.newhb.grabings.BaseActivity;
import com.tencent.newhb.grabings.R;

/**
 * Created by MSI05 on 2017/5/11.
 */
public class PCActivity extends BaseActivity {

    private PCActivity mPCActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPCActivity = this;

        setContentView(R.layout.activity_scan_pc);
        Button pay = (Button) findViewById(R.id.pay_btn);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toWeChatScan();
            }
        });
        Button over = (Button) findViewById(R.id.over_btn);
        over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void toWeChatScan() {

        if (AppHelper.isWeChatAvilible(mPCActivity)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);
            intent.putExtra("nofification_type", "pushcontent_notification");
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(PCActivity.this, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_LONG).show();
        }
    }
}
