package com.tencent.newhb.grabings.ui;

import android.app.Fragment;
import android.os.Bundle;

import com.tencent.newhb.grabings.BaseSettingsActivity;
import com.tencent.newhb.grabings.BaseSettingsFragment;

public class WechatSettingsActivity extends BaseSettingsActivity {

    @Override
    public Fragment getSettingsFragment() {
        return new WechatSettingsFragment();
    }

    public static class WechatSettingsFragment extends BaseSettingsFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }
    }
}
