package com.ncu.tianyan;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by BeanYong on 2015/8/12.
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener {
    public final String URL_FINDDRIVER = "http://djwang.roboo.com/";
    private ImageView mBack, mRefresh;
    private TextView mTitle;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        initViews();
        initEvents();
    }

    private void initEvents() {
        mWebView.loadUrl(URL_FINDDRIVER);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTitle.setText(title);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mBack.setOnClickListener(this);
        mRefresh.setOnClickListener(this);
    }

    private void initViews() {
        mWebView = (WebView) findViewById(R.id.id_wv_findDriver);
        mBack = (ImageView) findViewById(R.id.id_iv_back);
        mRefresh = (ImageView) findViewById(R.id.id_iv_self);
        mTitle = (TextView) findViewById(R.id.id_tv_title);

        mBack.setImageResource(R.drawable.back);
        mRefresh.setImageResource(R.drawable.refresh);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back:
                finish();
                break;
            case R.id.id_iv_self:
                mWebView.reload();
                break;
        }
    }

    /**
     * *********************************************************************************
     * 选项菜单部分
     * **********************************************************************************
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exit_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_menu_exit:
                ActivityManager.finishAllActivities();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
