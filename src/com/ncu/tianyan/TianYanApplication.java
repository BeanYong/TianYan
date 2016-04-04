package com.ncu.tianyan;

import android.app.Application;
import android.content.Context;

/**
 * Created by BeanYong on 2015/8/15.
 */
public class TianYanApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getmContext() {
        return mContext;
    }
}
