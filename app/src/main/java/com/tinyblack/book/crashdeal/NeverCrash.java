package com.tinyblack.book.crashdeal;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by lijie on 17-7-3.
 */

public class NeverCrash {

    private CrashHandler mCrashHandler;

    private static NeverCrash mInstance;

    public static boolean NEVER = true;

    private NeverCrash() {

    }

    private static NeverCrash getInstance() {
        if (mInstance == null) {
            synchronized (NeverCrash.class) {
                if (mInstance == null) {
                    mInstance = new NeverCrash();
                }
            }
        }

        return mInstance;
    }

    public static void init(CrashHandler crashHandler) {
        getInstance().setCrashHandler(crashHandler);
    }

    private void setCrashHandler(CrashHandler crashHandler) {

        mCrashHandler = crashHandler;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (NEVER) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        if (e != null)
                            Log.e("mCrashHandler  ", e.getMessage());
                        if (mCrashHandler != null) {//捕获异常处理
                            mCrashHandler.uncaughtException(Looper.getMainLooper().getThread(), e);
                        }
                    }
                }
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (e != null)
                    Log.e("mCrashHandler  ", e.getMessage());
                if (mCrashHandler != null) {//捕获异常处理
                    mCrashHandler.uncaughtException(t, e);
                }
            }
        });

    }

    public interface CrashHandler {
        void uncaughtException(Thread t, Throwable e);
    }

}
