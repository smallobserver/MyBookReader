package com.tinyblack.book.api.support.util;

import android.text.TextUtils;

import com.tinyblack.book.api.support.Constant;

/**
 * Created by ml166 on 2018/2/10.
 */

public class CoverUtil {
    public static String clipCover(String cover) {
        if (TextUtils.isEmpty(cover)) return cover;
        return Constant.IMG_BASE_URL + cover;
    }
}
