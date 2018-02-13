package com.tinyblack.book.crashdeal.action;

/**
 * Created by liutao on 14/07/2017.
 */

public interface SDKAction {


    String sdkCode();

    boolean dealError(String error);

    void closeSDK();

    boolean isOpen();

    boolean killApp();

    boolean initStatus();

    void clear();
}
