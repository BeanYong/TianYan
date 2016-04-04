package com.ncu.tianyan.util;

import android.widget.Toast;

import com.ncu.tianyan.TianYanApplication;

/**
 * Created by BeanYong on 2015/8/15.
 */
public class ToastUtil {
    public static void ToastShort(String str) {
        Toast.makeText(TianYanApplication.getmContext(), str, Toast.LENGTH_SHORT).show();
    }
}
