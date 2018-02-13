//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.tinyblack.book.api.support.util.AppUtils;
import com.tinyblack.book.crashdeal.NeverCrash;
import com.tinyblack.book.crashdeal.SDKCrashHandler;
import com.tinyblack.book.service.DownloadService;

public class MApplication extends Application {

    private static MApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppUtils.init(this);
        initCrashCatch();
        startService(new Intent(this, DownloadService.class));
    }

    /**
     * 初始化异常捕获
     */
    private void initCrashCatch() {
        NeverCrash.init(new SDKCrashHandler());
    }

    public static MApplication getInstance() {
        return instance;
    }
}
