package com.ncu.tianyan;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * 当前Application中所有Activity的基类
 * Created by BeanYong on 2015/8/10.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivityManager.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
