package com.tinyblack.book.crashdeal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.text.TextUtils;
import android.util.Log;

import com.tinyblack.book.MApplication;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by kyle on 18-2-13.
 */

public class ActivityMgr {
    private static final String TAG = "ActivityManager";
    private static Context mContext;
    private static Context mCurActivity;
    private static LinkedHashMap<Integer, Activity> mCacheActivities;

    public ActivityMgr() {
    }

    public static Context getContext() {
        if (mContext == null) {
            if (mCurActivity != null) {
                mContext = mCurActivity.getApplicationContext();
            } else {
                mContext = MApplication.getInstance();
            }
        }

        return mContext;
    }

    public static WeakReference<Activity> getActivityContext() {
        Activity activity = null;
        if (mCurActivity != null) {
            activity = (Activity) mCurActivity;
        } else if (mCacheActivities != null && mCacheActivities.size() > 0) {
            Iterator var1 = mCacheActivities.entrySet().iterator();

            while (var1.hasNext()) {
                Entry<Integer, Activity> entry = (Entry) var1.next();
                activity = (Activity) entry.getValue();
                if (activity != null) {
                    break;
                }
            }
        }

        WeakReference<Activity> weakReference = new WeakReference(activity);
        return weakReference;
    }

    public static int getCacheSize() {
        return mCacheActivities.size();
    }

    public static Activity getCurActivity() {
        return (Activity) mCurActivity;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static void removeActivity(Class activity) {
        if (mCacheActivities != null && activity != null) {
            List<Activity> activitys = new ArrayList();
            Iterator var2 = mCacheActivities.entrySet().iterator();

            while (var2.hasNext()) {
                Entry<Integer, Activity> entry = (Entry) var2.next();
                activitys.add(entry.getValue());
            }

            var2 = activitys.iterator();

            while (var2.hasNext()) {
                Activity act = (Activity) var2.next();
                if (act.getClass().getName().equals(activity.getName()) && !act.isFinishing()) {
                    mCacheActivities.remove(Integer.valueOf(activity.hashCode()));
                    act.finish();
                }
            }

        }
    }

    private static Activity getLastActivity() {
        if (mCacheActivities != null && mCacheActivities.size() != 0) {
            Iterator iterator = mCacheActivities.keySet().iterator();

            Activity lastActivity;
            for (lastActivity = null; iterator.hasNext(); lastActivity = (Activity) mCacheActivities.get(iterator.next())) {
                ;
            }

            return lastActivity;
        } else {
            return null;
        }
    }

    public static void addActivity(Activity activity) {
        mCurActivity = activity;
        if (mCacheActivities == null) {
            mCacheActivities = new LinkedHashMap();
        }

        int hashCode = activity.hashCode();
        if (mCacheActivities.containsKey(Integer.valueOf(hashCode))) {
            mCacheActivities.remove(Integer.valueOf(hashCode));
        }

        mCacheActivities.put(Integer.valueOf(hashCode), activity);
        Log.i(TAG, "addActivity.activity = " + activity.getClass().getSimpleName() + ", mCacheActivities.size() = " + mCacheActivities.size());
    }

    public static void destroyActivity(Activity activity) {
        if (mCacheActivities != null) {
            mCacheActivities.remove(Integer.valueOf(activity.hashCode()));
            if (mCurActivity == activity) {
                mCurActivity = null;
                mCurActivity = getLastActivity();
            }

            Log.i(TAG, "destroyActivity.activity = " + activity.getClass().getSimpleName() + ", mCacheActivities.size() = " + mCacheActivities.size());
        }

    }

    public static int finishAllActivity(boolean isIgnoreCurrentActivity) {
        int finishCount = 0;
        Log.i(TAG, "finishAllActivity.mCacheActivities.size() = " + (mCacheActivities == null ? 0 : mCacheActivities.size()));
        if (mCacheActivities != null && !mCacheActivities.isEmpty()) {
            List<Activity> activitys = new ArrayList();
            Iterator var3 = mCacheActivities.entrySet().iterator();

            while (var3.hasNext()) {
                Entry<Integer, Activity> entry = (Entry) var3.next();
                activitys.add(entry.getValue());
            }

            var3 = activitys.iterator();

            label35:
            while (true) {
                while (true) {
                    if (!var3.hasNext()) {
                        break label35;
                    }

                    Activity activity = (Activity) var3.next();
                    if (isIgnoreCurrentActivity && (!isIgnoreCurrentActivity || activity == mCurActivity)) {
                        mCacheActivities.remove(Integer.valueOf(activity.hashCode()));
                    } else if (!activity.isFinishing()) {
                        activity.finish();
                        ++finishCount;
                        Log.i(TAG, "finishAllActivity.activity = " + activity.getClass().getSimpleName() + " finished");
                    }
                }
            }
        }

        mCurActivity = null;
        return finishCount;
    }

    public static boolean findActivity(String name) {
        boolean find = false;
        if (TextUtils.isEmpty(name)) {
            return find;
        } else {
            if (mCacheActivities != null && !mCacheActivities.isEmpty()) {
                List<Activity> activitys = new ArrayList();
                Iterator var3 = mCacheActivities.entrySet().iterator();

                while (var3.hasNext()) {
                    Entry<Integer, Activity> entry = (Entry) var3.next();
                    activitys.add(entry.getValue());
                }

                var3 = activitys.iterator();

                while (var3.hasNext()) {
                    Activity activity = (Activity) var3.next();
                    String activityName = activity.getClass().getSimpleName();
                    if (name.equals(activityName)) {
                        find = true;
                        break;
                    }
                }
            }

            return find;
        }
    }

    public static boolean isTopActivityByCache(Activity activity) {
        if (mCacheActivities != null && mCacheActivities.size() != 0) {
            Iterator iterator = mCacheActivities.keySet().iterator();

            Activity lastActivity;
            for (lastActivity = null; iterator.hasNext(); lastActivity = (Activity) mCacheActivities.get(iterator.next())) {
                ;
            }

            return lastActivity != null && mCacheActivities != null && mCacheActivities.size() > 0 ? mCacheActivities.get(Integer.valueOf(lastActivity.hashCode())) == activity : false;
        } else {
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    public static boolean isTopActivity(String packageName, Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService("activity");
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list.size() == 0) {
            return false;
        } else {
            Iterator var4 = list.iterator();

            RunningAppProcessInfo process;
            do {
                if (!var4.hasNext()) {
                    return false;
                }

                process = (RunningAppProcessInfo) var4.next();
            } while (process.importance != 100 || !process.processName.equals(packageName));

            return true;
        }
    }

    @SuppressLint("WrongConstant")
    public static boolean isTopActivity(Activity activity) {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) activity.getSystemService("activity");
        ComponentName cn = ((RunningTaskInfo) am.getRunningTasks(1).get(0)).topActivity;
        if (cn.getClassName().contains(activity.getClass().getName())) {
            isTop = true;
        }

        return isTop;
    }
}

