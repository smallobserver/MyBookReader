package com.tinyblack.book.crashdeal;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.tinyblack.book.crashdeal.action.BaseSDKAction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by lijie on 17-7-3.
 */
public class SDKCrashHandler implements NeverCrash.CrashHandler {
    private static final String TAG = "CrashHandler";
    private String KEY = "SDKCrashHandler_";
    private static SDKCrashHandler sdkCrashHandler;
    private static final String OPEN = "1";
    private static AtomicBoolean isInit = new AtomicBoolean();

    private Map<String, BaseSDKAction> actions;

    public SDKCrashHandler() {
        actions = new HashMap<>();
    }

    public static void init(SDKCrashHandler sdkCrashHandler) {
        SDKCrashHandler.sdkCrashHandler = sdkCrashHandler;
        SDKCrashHandler.sdkCrashHandler.init();
        isInit.set(true);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            Log.e("error", "never---crash----");
            e.printStackTrace();
            //处理异常
            boolean kill = locationSdkError(e);
            //
            if (kill) {
                ActivityMgr.finishAllActivity(false);
                Process.killProcess(Process.myPid());
                System.exit(0);
                Log.e("error", "kill the app");
            } else {
                Log.e("error", "do nothing");
            }
        } catch (Exception e2222) {
            ActivityMgr.finishAllActivity(false);
            Process.killProcess(Process.myPid());
            System.exit(0);
        }

    }

    public String error(Throwable e) {
        StringBuffer err = new StringBuffer();
        try {
            err.append(e.toString());
            err.append("\n");
            StackTraceElement[] stack = e.getStackTrace();
            if (stack != null) {
                int index = stack.length;
                int more = 0;
                if (index > 10) {
                    index = 10;
                    more = stack.length - index;
                }

                for (int i = 0; i < index; i++) {
                    err.append("\tat ");
                    err.append(stack[i].toString());
                    err.append("\n");
                }
                if (more > 0) {
                    err.append("more lines : " + more);
                }

            }
            Throwable cause = e.getCause();
            if (cause != null) {
                StackTraceElement[] causeBy = cause.getStackTrace();
                err.append("Caused by: ");
                if (causeBy != null) {
                    for (int i = 0; i < causeBy.length; i++) {
                        err.append("\tat ");
                        err.append(causeBy[i].toString());
                        err.append("\n");
                    }
                }
            }
        } catch (Exception e1) {

        }

        Log.i(TAG, err.toString());
        return err.toString();
    }

    /**
     * 处理app异常信息
     *
     * @param e
     * @return 是否杀掉进程, 默认杀掉
     */
    private boolean locationSdkError(Throwable e) {
        try {
            if (!TextUtils.isEmpty(e.getMessage())) {//首先处理原因,
                for (String key : actions.keySet()) {
                    BaseSDKAction action = actions.get(key);
                    boolean result = action.dealError(e.getMessage());
                    if (result) {
                        return action.killApp();
                    }
                }

            }
            //其次处理
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                String clazzName = stackTraceElement.toString();
                if (TextUtils.isEmpty(clazzName)) {
                    continue;
                }
                for (String key : actions.keySet()) {
                    BaseSDKAction action = actions.get(key);
                    boolean result = action.dealError(clazzName);
                    if (result) {
                        return action.killApp();
                    }
                }
            }
            //最后处理
            for (StackTraceElement stackTraceElement : e.getCause().getStackTrace()) {
                String clazzName = stackTraceElement.getClassName();
                if (TextUtils.isEmpty(clazzName)) {
                    continue;
                }
                for (String key : actions.keySet()) {
                    BaseSDKAction action = actions.get(key);
                    boolean result = action.dealError(clazzName);
                    if (result) {
                        return action.killApp();
                    }
                }
            }
        } catch (Exception ec) {
            return true;
        }
        return true;
    }

    public void init() {
        boolean shouldClear = shouldClear();
        for (String key : actions.keySet()) {
            if (shouldClear) {
                actions.clear();
            } else {
                actions.get(key).initStatus();
            }
        }

    }

    private boolean shouldClear() {
        return true;
    }

}
