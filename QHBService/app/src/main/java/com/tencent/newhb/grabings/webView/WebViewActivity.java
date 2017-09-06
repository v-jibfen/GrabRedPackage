package com.tencent.newhb.grabings.webView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.newhb.grabings.BaseActivity;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.widget.NavigationTopBar;

public class WebViewActivity extends BaseActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    private NavigationTopBar _navigationTopBar;
    private WebView mWebView;

    private String _webUrl = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jswebview);

        _navigationTopBar = (NavigationTopBar) getSupportFragmentManager().findFragmentByTag(NavigationTopBar.class.getSimpleName());
        _navigationTopBar.setText(NavigationTopBar.NAVIGATION_VIEWS.TITLE, R.string.app_name);
        _navigationTopBar.setViewVisibility(NavigationTopBar.NAVIGATION_VIEWS.RIGHT, View.GONE);

        _navigationTopBar.setClickListener(NavigationTopBar.NAVIGATION_VIEWS.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebView = (WebView) findViewById(R.id.activity_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                _navigationTopBar.setTitle(mWebView.getTitle());
            }
        });


        mWebView.addJavascriptInterface(new JSBridge(this, mWebView), "JSBridgePlugin");

        _webUrl = getIntent().getStringExtra("web_url");
        refreshWebView();

    }

    private void refreshWebView() {
        Log.d(TAG, "WEB_URL --->" + _webUrl);
        mWebView.loadUrl(_webUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

