package com.ncu.tianyan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ncu.tianyan.util.ToastUtil;

/**
 * Created by BeanYong on 2015/8/17.
 */
public class CheckInternetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = con.getActiveNetworkInfo();
        if(info != null && info.isAvailable()){
            ToastUtil.ToastShort("网络已正常连接");
        } else{
            ToastUtil.ToastShort("当前网络不可用，请检查网络连接");
        }
    }
}
