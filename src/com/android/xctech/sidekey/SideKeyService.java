package com.android.xctech.sidekey;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.graphics.Color;
import java.lang.IllegalArgumentException;
import android.os.UEventObserver;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import java.util.List;
import android.content.ComponentName;
import com.android.xctech.sidekey.operate.SettingUtils;

public class SideKeyService extends Service {

    private static final String TAG = "SideKeyService";
    private static final int OPEN_LONGCLICK_APPLICATION = 2;
    private static final int OPEN_SINGLECLICK_APPLICATION = 3;
    private static final int WAKEUP_SCREEN = 4;

    private static final String SIDE_SLIP_MATCH = "SIDE_SLIP_STATE";

    private Context mContext;
    private PowerManager mPowerManager;
    private KeyguardManager mKeyguardManager;
    private static final int MSG_SIDE_SLIP_STATE_CHANGE = 1;
    String[] quickOperateListKey;
    private boolean bSingleClick = false;
    private boolean bSwitchCamera = false;
    private boolean bCallRecorder = false;

    protected int _splashTime = 750;
    private long firstTime = 0;
    private long secondTime = 0;
    private String mCurrentSideKey;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) mContext.getSystemService(KEYGUARD_SERVICE);
        startObserver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopObserver();
    }

    private void startObserver() {
        if (null != mSideSlipUEventObserver) {
            mSideSlipUEventObserver.startObserving(SIDE_SLIP_MATCH);
        }
    }

    private void stopObserver() {
        if (null != mSideSlipUEventObserver) {
            mSideSlipUEventObserver.stopObserving();
        }
    }

    private UEventObserver mSideSlipUEventObserver = new UEventObserver() {
        @Override
        public void onUEvent(UEventObserver.UEvent event) {
            if (isXchengMidtestPackage()) {
                return;
            }

            final String slip_state = event.get(SIDE_SLIP_MATCH);
            Log.d(TAG, "onUEvent() :: SIDE_SLIP_STATE: " + slip_state);
            bSwitchCamera = SettingUtils.getSideKeySetting(mContext,SettingUtils.SWITCH_CAMERA_STATE);
            bCallRecorder = SettingUtils.getSideKeySetting(mContext,SettingUtils.CALL_RECORDER_STATE);

            final Thread splashThread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        while(waited < _splashTime) {
                            sleep(200);
                            waited += 200;
                        }
                    } catch(InterruptedException e) {
                        Log.e(TAG, "splashThread Exception = " + e);
                    } finally {
                        secondTime = System.currentTimeMillis();
                        if (secondTime - firstTime > 750) {
                            if (bSingleClick) {
                                bSingleClick = false;
                                return;
                            } else {
                                mCurrentSideKey = "key_preference_longclick";
                                mUeventHandler.sendEmptyMessage(OPEN_SINGLECLICK_APPLICATION);
                            }
                        }
                    }
                }
            };
            if ("near".equals(slip_state)) {
                bSingleClick = false;
                firstTime = System.currentTimeMillis();
                splashThread.start();
                Log.i(TAG, "onUEvent() :: firstTime == "+firstTime);
            }
            if ("far".equals(slip_state)) {
                secondTime = System.currentTimeMillis();
                if (secondTime - firstTime < 500) {
                    bSingleClick = true;
                    mCurrentSideKey = "key_preference_singleclick";
                    updateSideKey();
                }
            }
        }
    };

    private void updateSideKey() {
        //@xionghy add of SWELE-360 start
        if(android.telecom.TelecomManager.from(mContext).isInCall()){
            if (bCallRecorder) {
                Intent intentCallRecorder = new Intent("android.intent.action.callrecorder");
                mContext.sendBroadcast(intentCallRecorder);
            }
        }else{
          if (isCameraActivityOnTop() && bSwitchCamera) {
              Intent intentSwitchCamera = new Intent("android.intent.action.switchcamera");
              mContext.sendBroadcast(intentSwitchCamera);
          } else {
              mUeventHandler.sendEmptyMessage(OPEN_SINGLECLICK_APPLICATION);
          }
        }
        //@xionghy add of SWELE-360 end
    }

    private Handler mUeventHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_SINGLECLICK_APPLICATION:
                    if(onClickSideKeyNone()){
                        return;
                    }
                    mUeventHandler.removeMessages(OPEN_SINGLECLICK_APPLICATION);
                    openSideKeyApp();
                    mUeventHandler.sendEmptyMessage(WAKEUP_SCREEN);
                    break;
                case WAKEUP_SCREEN:
                    mUeventHandler.removeMessages(WAKEUP_SCREEN);
                    Log.d(TAG, "WAKEUP_SCREEN");
                    if (isKeyguardSecure()) {
                        wakeUpScreen();
                    } else {
                        if (isKeyguardLocked()) {
                            if (!mPowerManager.isScreenOn()) {
                                mUeventHandler.sendEmptyMessageDelayed(WAKEUP_SCREEN, 50);
                            }
                        } else {
                            wakeUpScreen();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void wakeUpScreen() {
        if (!mPowerManager.isScreenOn()) {
            mPowerManager.wakeUp(SystemClock.uptimeMillis());
        }
    }

    private boolean onClickSideKeyNone(){
        String quickStartApp = SettingUtils.getSideKeyDetail(mContext, SettingUtils.getDetailKey(mCurrentSideKey));
        return ("keyQuickOperate_none".equals(quickStartApp));
    }

    private void openSideKeyApp() {
        Intent intent = new Intent("android.intent.action.gesture");
        String quickStartApp;
        boolean startRecording = false;
        quickStartApp = SettingUtils.getSideKeyDetail(mContext, SettingUtils.getDetailKey(mCurrentSideKey));
        Log.d(TAG, "openSideKeyApp() quickStartApp == "+quickStartApp);
        quickOperateListKey = mContext.getResources().getStringArray(R.array.quickoperate_setting_list_key);
        for (int i=0;i<quickOperateListKey.length;i++) {
            if (quickStartApp.equals(quickOperateListKey[i])) {
                if ("keyQuickOperate_record".equals(quickStartApp)) {
                    quickStartApp = "com.android.soundrecorder;com.android.soundrecorder.SoundRecorder";
                    startRecording = true;
                    if(isSoundRecorderActivity()){
                        Intent intentQuickOperateRecord = new Intent("android.intent.action.quickoperate");
                        intentQuickOperateRecord.putExtra("sidequickoperate", "keyQuickOperate_record");
                        mContext.sendBroadcast(intentQuickOperateRecord);
                        return;
                    }
                } else if ("keyQuickOperate_offscreencamera".equals(quickStartApp)) {
                    quickStartApp = "com.mediatek.camera;com.android.camera.CameraActivity";
                    intent.putExtra("open_cameraId", 0);
                } else if ("keyQuickOperate_frontcamera".equals(quickStartApp)){
                    quickStartApp = "com.mediatek.camera;com.android.camera.CameraActivity";
                    intent.putExtra("open_cameraId", 1);
                } else if ("keyQuickOperate_silentmode".equals(quickStartApp)) {//Fixed by guanxiubiao for SWELE-507 add silent mode function 20160804
                    switchSilentMode(mContext);
                    wakeUpScreen();
                    return;
                } else if ("keyQuickOperate_camera".equals(quickStartApp)) {
                     quickStartApp = "com.mediatek.camera;com.android.camera.CameraLauncher";
                } else if ("keyQuickOperate_browser".equals(quickStartApp)) {
                     quickStartApp = "com.android.browser;com.android.browser.BrowserActivity";
                } else if ("keyQuickOperate_settings".equals(quickStartApp)){
                     quickStartApp = "com.android.settings;com.android.settings.Settings";
                } else if ("keyQuickOperate_email".equals(quickStartApp)) {
                     quickStartApp = "com.android.email;com.android.email.activity.Welcome";
                } else {
                    Intent intentQuickOperate = new Intent("android.intent.action.quickoperate");
                    intentQuickOperate.putExtra("sidequickoperate", quickStartApp);
                    mContext.sendBroadcast(intentQuickOperate);
                }
            }
        }

        if(quickStartApp.startsWith("com.mobiistar.camera")){
            quickStartApp = "com.mobiistar.camera;com.mobiistar.camera.activity.CameraActivity";
        }

        String quickStartAppInfo[] = quickStartApp.split(";");
        if (quickStartAppInfo.length < 2) {
            Log.d(TAG, "quickStartAppInfo = " + quickStartAppInfo.length);
            return;
        }
        if(quickStartAppInfo[0].equals("com.mediatek.camera")){
            quickStartAppInfo[1] = "com.android.camera.CameraActivity";
        }

        intent.putExtra("packageName", quickStartAppInfo[0]);
        intent.putExtra("activityName", quickStartAppInfo[1]);
        intent.putExtra("slidekey", startRecording);
        mContext.sendBroadcast(intent);
    }

    //Fixed by guanxiubiao for SWELE-507 add silent mode function 20160804 start
    private void switchSilentMode(Context context){
        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = mAudioManager.getRingerMode();
        if(ringerMode!=AudioManager.RINGER_MODE_SILENT){
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }else{
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

    }
    //Fixed by guanxiubiao for SWELE-507 add silent mode function 20160804 end

    private boolean isCameraActivityOnTop() {
        if ("com.android.camera.CameraLauncher".equals(getTopActivity())
                || "com.android.camera.CameraActivity".equals(getTopActivity())) {
            return !isKeyguardLocked();
        } else if ("com.android.camera.SecureCameraActivity".equals(getTopActivity())) {
            return true;
        }
        return false;
    }

    private boolean isXchengMidtestPackage() {
        if ("com.xcheng.midtest".equals(getTopPackageName())
                || "com.xcheng.qcapp".equals(getTopPackageName())) {
            return true;
        }
        return false;
    }

    private boolean isSoundRecorderActivity() {
        if ("com.android.soundrecorder.SoundRecorder".equals(getTopActivity())) {
            return !isKeyguardLocked();
        }
        return false;
    }

    private boolean isKeyguardLocked() {
        Log.d(TAG, "isKeyguardLocked = " + mKeyguardManager.isKeyguardLocked());
        return mKeyguardManager.isKeyguardLocked();
    }

    private boolean isKeyguardSecure() {
        Log.d(TAG, "isKeyguardSecure = " + mKeyguardManager.isKeyguardSecure());
        return mKeyguardManager.isKeyguardSecure();
    }

    private String getTopActivity() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
        if (runningTasks != null && runningTasks.size() > 0) {
            ComponentName comName = runningTasks.get(0).topActivity;
            Log.d(TAG, "getTopActivity = " + comName.getClassName());
            return comName.getClassName();
        }
        return "";
    }

    // XCSW add by like 2016.08.25 (Begin)
    private String getTopPackageName() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
        if (runningTasks != null && runningTasks.size() > 0) {
            ComponentName comName = runningTasks.get(0).topActivity;
            Log.d(TAG, "getTopPackageName = " + comName.getPackageName());
            return comName.getPackageName();
        }
        return "";
    }
    // XCSW add by like 2016.08.25 (End)

}
