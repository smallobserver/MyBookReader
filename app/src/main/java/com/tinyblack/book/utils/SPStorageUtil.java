package com.tinyblack.book.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * 存储性能较低，建议采用Acache
 * 大数据建议在异步线程中读取
 */
public class SPStorageUtil {
    private static SharedPreferences sp;
    public static final String SP_NAME = "plu_config";

    public static final int TIME_HOUR = 60 * 60;
    public static final int TIME_DAY = TIME_HOUR * 24;
    private static SharedPreferences.Editor mEditor;
    private static SPStorageUtil cache = null;


    public static SPStorageUtil get(Context ctx) {
        return get(ctx, SP_NAME);
    }

    public static SPStorageUtil get(Context ctx, String cacheName) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(cacheName, Activity.MODE_PRIVATE);
            mEditor = sp.edit();
        }
        if (cache == null) {
            cache = new SPStorageUtil();
        }
        return cache;
    }

    /*旧方法***********************************************************************/
    public static void saveBoolean(Context ct, String key, boolean value) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key, value).commit();
    }

    public static void saveBooleanApply(Context ct, String key, boolean value) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context ct, String key, boolean defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);

        return sp.getBoolean(key, defValue);
    }

    public static boolean getBoolean(String key){
        return sp.getBoolean(key, false);
    }

    public static void saveString(Context ct, String key, String value) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        sp.edit().putString(key, value).commit();
    }

    public static void saveStringApply(Context ct, String key, String value) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        sp.edit().putString(key, value).apply();
    }

    public static String getString(Context ct, String key, String defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);

        return sp.getString(key, defValue);
    }

    public static void saveInt(Context ct, String key, int value) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);

        sp.edit().putInt(key, value).commit();
    }

    public static void saveIntApply(Context ct, String key, int value) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);

        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(Context ct, String key, int defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);

        return sp.getInt(key, defValue);
    }


    public static Long getLong(Context ct, String key, long defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);

        return sp.getLong(key, defValue);
    }

    public static void saveLong(Context ct, String key, long defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);

        sp.edit().putLong(key, defValue).commit();
    }

    public static void saveFloat(Context ct, String key, float defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        sp.edit().putFloat(key, defValue).commit();
    }

    public static float getFloat(Context ct, String key, float defValue) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);

        return sp.getFloat(key, defValue);
    }

    public static void remove(Context ct, String key) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        sp.edit().remove(key).commit();
    }

    public static void removeAll(Context ct, ArrayList<String> keys) {
        if (sp == null)
            sp = ct.getSharedPreferences(SP_NAME, 0);
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                sp.edit().remove(key).commit();
            }
        }
    }
   /*旧方法***********************************************************************/


    // =======================================
    // ============ String数据 读写 ==============
    // =======================================

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的数据
     */
    public void put(String key, Object value) { // 保存单个文件
        mEditor = sp.edit();
        if (value instanceof String) {
            mEditor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            mEditor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            mEditor.putLong(key, (Long) value);
        }
        mEditor.commit();
    }

    public void putApply(String key, Object value) { // 保存单个文件
        mEditor = sp.edit();
        if (value instanceof String) {
            mEditor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            mEditor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            mEditor.putLong(key, (Long) value);
        }
        mEditor.apply();
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的String数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, Object value, int saveTime) {
        if (value instanceof String) {
            put(key, Utils.newStringWithDateInfo(saveTime, (String) value));
        } else {
            put(key, value);
        }
    }

    public Object get(String key, Object def) {
//        sp.get
        if (def instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) def);
        } else if (def instanceof String) {
            return getAsString(key, (String) def);
        } else if (def instanceof Integer) {
            return sp.getInt(key, (Integer) def);
        } else if (def instanceof Long) {
            return sp.getLong(key, (Long) def);
        }
        return null;
    }

    /**
     * 读取 String数据
     *
     * @param key
     * @return String 数据
     */
    public String getAsString(String key, String def) {

        String readString = sp.getString(key, def);
        if (readString == null) {
            return null;
        }
        if (!Utils.isDue(readString)) {
            return Utils.clearDateInfo(readString);
        } else {
            remove(key);// 缓存到期了
            return null;
        }

    }

    public boolean getAsBoolean(String key){
        return sp.getBoolean(key, false);
    }

    public String getAsString(String key) {
        String readString = sp.getString(key, null);
        if (readString == null) {
            return null;
        }
        if (!Utils.isDue(readString)) {
            return Utils.clearDateInfo(readString);
        } else {
            remove(key);// 缓存到期了
            return null;
        }

    }

    public  Long getLong(String key, long defValue) {

        return sp.getLong(key, defValue);
    }

    public static void saveLong( String key, long defValue) {
        sp.edit().putLong(key, defValue).commit();
    }

    // =======================================
    // ============= JSONObject 数据 读写 ==============
    // =======================================

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSON数据
     */
    public void put(String key, JSONObject value) {
        put(key, value.toString());
    }

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONObject数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, JSONObject value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * 读取JSONObject数据
     *
     * @param key
     * @return JSONObject数据
     */
    public JSONObject getAsJSONObject(String key) {
        String JSONString = getAsString(key);
        try {
            JSONObject obj = new JSONObject(JSONString);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // =======================================
    // ============ JSONArray 数据 读写 =============
    // =======================================

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSONArray数据
     */
    public void put(String key, JSONArray value) {
        put(key, value.toString());
    }

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONArray数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, JSONArray value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * 读取JSONArray数据
     *
     * @param key
     * @return JSONArray数据
     */
    public JSONArray getAsJSONArray(String key) {
        String JSONString = getAsString(key);
        try {
            JSONArray obj = new JSONArray(JSONString);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 移除某个key
     *
     * @param key
     * @return 是否移除成功
     */
    public boolean remove(String key) {
        mEditor = sp.edit();
        mEditor.remove(key);
        return mEditor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        mEditor = sp.edit();
        mEditor.clear();
        mEditor.commit();
    }

    /**
     * @author 杨福海（michael） www.yangfuhai.com
     * @version 1.0
     * @title 时间计算工具类
     */
    private static class Utils {

        /**
         * 判断缓存的String数据是否到期
         *
         * @param str
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(String str) {
            return isDue(str.getBytes());
        }

        /**
         * 判断缓存的byte数据是否到期
         *
         * @param data
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromDate(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr = strs[0];
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr
                            .substring(1, saveTimeStr.length());
                }
                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strs[1]);
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true;
                }
            }
            return false;
        }

        private static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

        private static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
            byte[] data1 = createDateInfo(second).getBytes();
            byte[] retdata = new byte[data1.length + data2.length];
            System.arraycopy(data1, 0, retdata, 0, data1.length);
            System.arraycopy(data2, 0, retdata, data1.length, data2.length);
            return retdata;
        }

        private static String clearDateInfo(String strInfo) {
            if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
                strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1,
                        strInfo.length());
            }
            return strInfo;
        }

        private static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, mSeparator) + 1,
                        data.length);
            }
            return data;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null && data.length > 15 && data[13] == '-'
                    && indexOf(data, mSeparator) > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14,
                        indexOf(data, mSeparator)));
                return new String[]{saveDate, deleteAfter};
            }
            return null;
        }

        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0)
                throw new IllegalArgumentException(from + " > " + to);
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0,
                    Math.min(original.length - from, newLength));
            return copy;
        }

        private static final char mSeparator = ' ';

        private static String createDateInfo(int second) {
            String currentTime = System.currentTimeMillis() + "";
            while (currentTime.length() < 13) {
                currentTime = "0" + currentTime;
            }
            return currentTime + "-" + second + mSeparator;
        }

        /*
         * Bitmap → byte[]
         */
        private static byte[] Bitmap2Bytes(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        /*
         * byte[] → Bitmap
         */
        private static Bitmap Bytes2Bimap(byte[] b) {
            if (b.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        /*
         * Drawable → Bitmap
         */
        private static Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            }
            // 取 drawable 的长宽
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            // 取 drawable 的颜色格式
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            // 建立对应 bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            // 建立对应 bitmap 的画布
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            // 把 drawable 内容画到画布中
            drawable.draw(canvas);
            return bitmap;
        }

        /*
         * Bitmap → Drawable
         */
        @SuppressWarnings("deprecation")
        private static Drawable bitmap2Drawable(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            return new BitmapDrawable(bm);
        }
    }
}
