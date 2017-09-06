package com.tencent.newhb.grabings.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.newhb.grabings.BaseActivity;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.widget.NavigationTopBar;

public class HelpActivity extends BaseActivity {

    private NavigationTopBar _navigationTopBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        _navigationTopBar = (NavigationTopBar) getSupportFragmentManager().findFragmentByTag(NavigationTopBar.class.getSimpleName());
        _navigationTopBar.setText(NavigationTopBar.NAVIGATION_VIEWS.TITLE, R.string.help_text);
        _navigationTopBar.setViewVisibility(NavigationTopBar.NAVIGATION_VIEWS.RIGHT, View.GONE);

        _navigationTopBar.setClickListener(NavigationTopBar.NAVIGATION_VIEWS.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button openServiceBtn = (Button) findViewById(R.id.open_service_btn);
        openServiceBtn.setText(String.format(getString(R.string.helper_1), getString(R.string.app_name), getString(R.string.app_name)));

        TextView helperOneText = (TextView) findViewById(R.id.helper_2_text);
        helperOneText.setText(String.format(getString(R.string.helper_2), getString(R.string.app_name), getString(R.string.app_name),
                getString(R.string.app_name), getString(R.string.app_name), getString(R.string.app_name)));

        TextView helperTwoText = (TextView) findViewById(R.id.helper_3_text);
        helperTwoText.setText(String.format(getString(R.string.helper_3), getString(R.string.app_name), getString(R.string.app_name)));
    }
}
