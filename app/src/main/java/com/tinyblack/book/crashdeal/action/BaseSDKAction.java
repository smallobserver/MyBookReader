package com.tinyblack.book.crashdeal.action;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;


import com.tinyblack.book.MApplication;
import com.tinyblack.book.utils.SPStorageUtil;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liutao on 14/07/2017.
 * <p>
 * TODO 替换为数据库进行数据存储
 */

public abstract class BaseSDKAction implements SDKAction {
    private static final String TAG = "SDKAction";
    public static final int MAX_COUNT = 5;//最大错误次数
    public static final int NEXT_HOUR = 6;//下次开启时间
    private AtomicBoolean OPEN = new AtomicBoolean(true);

    @Override
    public boolean dealError(String error) {
        Log.e(TAG, sdkCode() + " to deal the error " + error);
        if (TextUtils.isEmpty(error)) {
            return false;
        }
        if (error.contains(getName())) {
            Log.e(TAG, "" + sdkCode() + " can deal this error " + error);
            int crashCount = SPStorageUtil.getInt(MApplication.getInstance(), getCrashCountKey(), 0);
            crashCount++;
            SPStorageUtil.saveInt(MApplication.getInstance(), getCrashCountKey(), crashCount);
            Log.e(TAG, "当前" + getName() + "  :" + crashCount + "---keyOpenTime:" + getNextTimeKey());
            if (crashCount >= MAX_COUNT) {
                SPStorageUtil.saveLong(MApplication.getInstance(), getNextTimeKey(), getNextSixTime());
            }
            return true;
        }
        return false;
    }

    @Override
    public void closeSDK() {
        Log.e(TAG, "close the sdk " + getKey() + " --- " + getName() + "---" + sdkCode());
        OPEN.set(false);
    }

    @Override
    public boolean isOpen() {
        return OPEN.get();
    }

    /**
     * @return 保存在本地的key值
     */
    @NonNull
    public abstract String getKey();

    private String getNextTimeKey() {
        return getKey().concat("_next_time");
    }

    private String getCrashCountKey() {
        return getKey().concat("_crash_count");
    }

    /**
     * @return 获取要校验的类名
     */
    @NonNull
    public abstract String getName();


    public boolean killApp() {
        return true;
    }

    public boolean getCacheStatus() {
        long nextOpenTime = SPStorageUtil.getLong(MApplication.getInstance(), getNextTimeKey(), 0);
        if (nextOpenTime != 0) {
            long currentTime = System.currentTimeMillis();
            return nextOpenTime < currentTime;
        }
        return true;

    }

    @Override
    public boolean initStatus() {
        if (!OPEN.get()) {
            Log.e(TAG, "already close the sdk " + getName());
            return false;
        }
        boolean status = getCacheStatus();
        OPEN.set(status);
        return status;
    }


    @Override
    public void clear() {
        SPStorageUtil.saveInt(MApplication.getInstance(), getCrashCountKey(), 0);
        SPStorageUtil.saveLong(MApplication.getInstance(), getNextTimeKey(), 0);
    }


    /**
     * 获取当前时间6小时后的时间戳
     *
     * @return
     */
    private long getNextSixTime() {
        Date nowDate = new Date();
        nowDate.setHours(nowDate.getHours() + NEXT_HOUR);
        return nowDate.getTime();
    }
}
