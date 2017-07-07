package com.android.xctech.sidekey;


import com.android.xctech.sidekey.operate.SettingUtils;
import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class SideActivityFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener{

    private static final String KEY_SINGLECLICK = "single_click_pref";
    private static final String KEY_LONGCLICK = "long_click_pref";
    private static final String KEY_CALL_RECORDING = "call_recording_bar";
    private static final String KEY_CAMERA_SWITCH = "camera_switch_bar";

    private String mSingleClickKey = null;
    private String mLongClickKey = null;
    public static final int REQUEST_PICK_SIDEKEY = 1;
    private Context context;

    private Preference mSingleClick;
    private Preference mLongClick;
    private SwitchPreference mCallRecordingPreference;
    private SwitchPreference mCameraSwitchPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        addPreferencesFromResource(R.xml.side_activity);

        mSingleClick = (Preference) findPreference(KEY_SINGLECLICK);
        mLongClick = (Preference) findPreference(KEY_LONGCLICK);
        mCallRecordingPreference = (SwitchPreference) findPreference(KEY_CALL_RECORDING);
        mCameraSwitchPreference = (SwitchPreference) findPreference(KEY_CAMERA_SWITCH);

        mSingleClick.setOnPreferenceClickListener(this);
        mLongClick.setOnPreferenceClickListener(this);
        mCallRecordingPreference.setOnPreferenceChangeListener(this);
        mCameraSwitchPreference.setOnPreferenceChangeListener(this);
        init();
    }

    private void init() {
        String[] configList = getResources().getStringArray(R.array.sidekey_support_list);
        for (int i = 0; i < configList.length; i ++) {
            final String[] configItem = configList[i].split(";");
            final String singleClickKey = configItem[0];
            final String longClickKey = configItem[1];
            mSingleClick.setSummary(SettingUtils.getTitle(context, singleClickKey));
            mLongClick.setSummary(SettingUtils.getTitle(context, longClickKey));

            mSingleClickKey = singleClickKey;
            mLongClickKey = longClickKey;
        }

        String dirPath= "/data/data/com.android.xctech.sidekey/shared_prefs/";
        File file= new File(dirPath);
        if(!file.exists()) {
            boolean bDefCallRecorder = getResources().getBoolean(R.bool.default_call_recorder_state);
            boolean bDefSwitchCamera = getResources().getBoolean(R.bool.default_switch_camera_state);
            if (bDefCallRecorder) {
                SettingUtils.setSideKeySetting(context, SettingUtils.CALL_RECORDER_STATE, true);
                mCallRecordingPreference.setChecked(true);
            } else {
                SettingUtils.setSideKeySetting(context, SettingUtils.CALL_RECORDER_STATE, false);
                mCallRecordingPreference.setChecked(false);
            }

            if (bDefSwitchCamera) {
                SettingUtils.setSideKeySetting(context, SettingUtils.SWITCH_CAMERA_STATE, true);
                mCameraSwitchPreference.setChecked(true);
            } else {
                SettingUtils.setSideKeySetting(context, SettingUtils.SWITCH_CAMERA_STATE, false);
                mCameraSwitchPreference.setChecked(false);
            }
        }
    }

    @Override
    public void onResume() {
        if (SettingUtils.getSideKeySetting(context, SettingUtils.CALL_RECORDER_STATE)) {
            mCallRecordingPreference.setChecked(true);
        } else {
            mCallRecordingPreference.setChecked(false);
        }

        if (SettingUtils.getSideKeySetting(context, SettingUtils.SWITCH_CAMERA_STATE)) {
            mCameraSwitchPreference.setChecked(true);
        } else {
            mCameraSwitchPreference.setChecked(false);
        }
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String preferenceKey = null;
        String value = null;
        if (data != null) {
            preferenceKey = data.getStringExtra(SettingUtils.KEY);
            value = data.getStringExtra(SettingUtils.VALUE);
            updateDetailSetting(SettingUtils.getDetailKey(preferenceKey), value);
        }
        if (REQUEST_PICK_SIDEKEY == requestCode) {
            if (-1 == resultCode) {
                mSingleClick.setSummary(SettingUtils.getTitle(context, preferenceKey));
            } else if (-2 == resultCode) {
                mLongClick.setSummary(SettingUtils.getTitle(context, preferenceKey));
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        final String key = preference.getKey();
        boolean value = (Boolean) objValue;

        switch (key) {
            case KEY_CALL_RECORDING:
                SettingUtils.setSideKeySetting(context, SettingUtils.CALL_RECORDER_STATE, value);
                return true;
            case KEY_CAMERA_SWITCH:
                SettingUtils.setSideKeySetting(context, SettingUtils.SWITCH_CAMERA_STATE, value);
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        if (preference == mSingleClick) {
            intent.putExtra("bSingleClick", true);
            intent.putExtra(SettingUtils.KEY, mSingleClickKey);
            startActivityForResult(intent, REQUEST_PICK_SIDEKEY);
            return true;
        } else if (preference == mLongClick){
            intent.putExtra("bLongClick", true);
            intent.putExtra(SettingUtils.KEY, mLongClickKey);
            startActivityForResult(intent, REQUEST_PICK_SIDEKEY);
            return true;
        }
        return false;
    }

    private void updateDetailSetting(String detailKey, String value) {
        SettingUtils.setSideKeyDetail(context, detailKey, value);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}