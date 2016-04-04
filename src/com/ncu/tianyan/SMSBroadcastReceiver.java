package com.ncu.tianyan;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.ncu.tianyan.util.FileUtils;
import com.ncu.tianyan.util.ToastUtil;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    public static String wdString, jdString;
    public String info = null;
    // public static String saveInfo = null;
    public static boolean jiujia = false;// 是否为酒驾短信
    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final int NOTIFICATION_ID = 100;// notification的id
    public static NotificationManager notiManager;

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdusObjects = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdusObjects.length];
                for (int i = 0; i < pdusObjects.length; i++) {
                    if (MainActivity.mTts != null) {
                        // 执行朗读
                        MainActivity.mTts.speak("尊敬的用户，您的亲人正在酒驾",
                                TextToSpeech.QUEUE_ADD, null);
                    }
                    messages[i] = SmsMessage
                            .createFromPdu((byte[]) pdusObjects[i]);
                }
                StringBuilder SMSContent = new StringBuilder();
                for (SmsMessage message : messages) {
                    SMSContent.append(message.getDisplayMessageBody());
                    if (SMSContent.charAt(0) == 'N' && SMSContent.length() == 22) {
                        String locationStr = SMSContent.toString();
                        String[] locationStrArr = locationStr.split(",");
                        wdString = locationStrArr[0].substring(1);
                        jdString = locationStrArr[1].substring(1);

                        info = "您的亲人正在酒驾他（她）的位置是" + '\n' + "北纬："
                                + wdString + "度"
                                + '\n'
                                + "东经："
                                + jdString + "度";

                        // 作为酒驾进行提示
                        if (!MainActivity.isGetOpposite) {
                            ToastUtil.ToastShort(info);

                            notiManager = (NotificationManager) context
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            Intent notiIntent = new Intent(context,
                                    MainActivity.class);
                            notiIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pintent = PendingIntent.getActivity(
                                    context, 0, notiIntent, 0);
                            Builder builder = new Notification.Builder(context);
                            builder.setSmallIcon(R.drawable.logo3);// 设置图标
                            builder.setTicker("天眼提示：您的亲人正在酒驾！");// 手机状态栏的提示；
                            builder.setWhen(System.currentTimeMillis());// 设置时间
                            builder.setContentTitle("天眼");// 设置标题
                            builder.setContentText("您的亲人正在酒驾，点击查看酒驾司机位置");// 设置通知内容
                            builder.setContentIntent(pintent);// 点击后的意图
                            builder.setDefaults(Notification.DEFAULT_ALL);// 设置震动
                            Notification notification =
                                    builder.build();//4.1以上
//                            Notification notification = builder
//                                    .getNotification();
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notiManager.notify(NOTIFICATION_ID, notification);
                        } else {
                            // 作为定位对方进行提示
                            ToastUtil.ToastShort(
                                    "你的TA在" + '\n' + "北纬："
                                            + wdString + "度"
                                            + '\n'
                                            + "东经："
                                            + jdString + "度" + '\n'
                                            + "正在为您定位！");
                            // 当接收到对方信息时直接进行定位
                            locateOpposite();
                            MainActivity.isGetOpposite = false;
                        }
                        FileUtils.writeInfo(context);
                        abortBroadcast();//拦截短信
                    }
                }
            }
        }
    }

    /**
     * 当接收到对方的经纬度信息后，定位对方
     */
    public void locateOpposite() {
        LatLng ptCenter = new LatLng(Float.valueOf(wdString),
                Float.valueOf(jdString));
        // 反Geo搜索
        MainActivity.mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));
    }
}