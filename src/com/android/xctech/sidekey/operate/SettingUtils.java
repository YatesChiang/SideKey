package com.android.xctech.sidekey.operate;

import com.android.xctech.sidekey.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.util.Log;

public class SettingUtils {
    private final static String TAG = "SettingUtils";
    public final static String KEY = "key";
    public final static String VALUE = "value";
    public final static String SIDEKEY_DETAIL_NONE = "none";
    public final static String PREFERENCE_KEY_PREFIX = "key_preference_";
    public final static String DETAIL_KEY_SUFFIX = "_detail";
    private final static String SIDEKEY_PREFERENCE = "sidekey_preference";
    public final static String CALL_RECORDER_STATE = "key_call_recorder_state";
    public final static String SWITCH_CAMERA_STATE = "key_switch_camera_state";

    private static SharedPreferences getSideKeySharedPreferences(Context context) {
        return context.getSharedPreferences(SIDEKEY_PREFERENCE, Context.MODE_MULTI_PROCESS | Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
    }

    public static String getDetailKey(String preferenceKey) {
        return preferenceKey + DETAIL_KEY_SUFFIX;
    }

    public static String getTitle(Context context, String preferenceKey) {
        final PackageManager pManager = context.getPackageManager();
        String detailArrary[]= getSideKeyDetail(context, getDetailKey(preferenceKey)).split(";");
        String title = context.getResources().getString(R.string.preference_title_none);
        if (detailArrary.length == 2) {
            try {
                ActivityInfo ai = pManager.getActivityInfo(new ComponentName(detailArrary[0], detailArrary[1]), 0);
                title = ai.loadLabel(pManager).toString();
            } catch (PackageManager.NameNotFoundException e) {
            }
        } else if (detailArrary.length == 1) {
            String[] operateListName = context.getResources().getStringArray(R.array.quickoperate_setting_list_name);
            String[] operateListKey = context.getResources().getStringArray(R.array.quickoperate_setting_list_key);
            for (int i=0;i<operateListKey.length;i++) {
                if (detailArrary[0].equals(operateListKey[i])) {
                    title = operateListName[i];
                }
            }
        }
        return title;
    }

    public static boolean getSideKeySetting (Context context, String preferenceKey) {
        SharedPreferences sp = getSideKeySharedPreferences(context);
        boolean defaultFlag = false;
        if(preferenceKey.equals(SWITCH_CAMERA_STATE)){
            defaultFlag = context.getResources().getBoolean(R.bool.default_switch_camera_state);
        }else if(preferenceKey.equals(CALL_RECORDER_STATE)){
            defaultFlag = context.getResources().getBoolean(R.bool.default_call_recorder_state);
        }
        return sp.getBoolean(preferenceKey, defaultFlag);
    }

    public static void setSideKeySetting (Context context, String preferenceKey, boolean value) {
        SharedPreferences sp = getSideKeySharedPreferences(context);
        sp.edit().putBoolean(preferenceKey, value).commit();
    }

    public static String getSideKeyDetail (Context context, String detailKey) {
        SharedPreferences sp = getSideKeySharedPreferences(context);
        return sp.getString(detailKey, getDefaultSideKeyDetail(context, detailKey));
    }

    public static String getDefaultSideKeyDetail (Context context, String detailKey) {
        String[] defaultDetailList = context.getResources().getStringArray(R.array.default_sidekey_detail_list);
        for (int i = 0; i < defaultDetailList.length; i ++) {
            final String[] defaultDetailItem = defaultDetailList[i].split(";");
            final String key = defaultDetailItem[0];
            if (detailKey.equals(key)) {
                String defaultDetail = defaultDetailList[i].replace(key + ";", "");
                //if (defaultDetail.split(";").length == 2) {
                    return defaultDetail;
                //}
            }
        }
        return SIDEKEY_DETAIL_NONE;
    }

    public static void setSideKeyDetail (Context context, String detailKey, String value) {
        SharedPreferences sp = getSideKeySharedPreferences(context);
        sp.edit().putString(detailKey, value).commit();
    }
}
