package com.ncu.tianyan;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Application中所有Activity的管理类
 * Created by BeanYong on 2015/8/10.
 */
public class ActivityManager {
    /**
     * 存放整个Application中未销毁的Activity
     */
    private static List<Activity> activities = new ArrayList<Activity>();

    /**
     * 向Activity集合中添加activity
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 从Activity集合中移除activity
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * finish当前Application的所有Activity
     */
    public static void finishAllActivities() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
