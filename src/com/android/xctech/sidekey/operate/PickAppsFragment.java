package com.android.xctech.sidekey.operate;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import com.android.xctech.sidekey.R;
import com.android.xctech.sidekey.SideKeyFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

public class PickAppsFragment extends SideKeyFragment {

    private static final String TAG = "PickAppsFragment";
    private View mFragmentView;
    private ListView appsList;
    private AppsListAdapter appsListAdapter;
    private List<AppsListBean> allApps;

    private int clickPosition = 0;
    private String mPreferenceKey = "";
    private String appPackName = "";
    private String ActivityName="";

    private static final String[][] SKIP_LIST = {{"com.google.android.apps.plus", "com.google.android.apps.plus.phone.HomeActivity"},
                                    {"com.google.android.talk", "com.google.android.talk.SigningInActivity"}};

    public PickAppsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mFragmentView = inflater.inflate(R.layout.pick_apps_fragment, container, false);
        mPreferenceKey = mActivity.getIntent().getStringExtra(SettingUtils.KEY);
        initDeafultSideKey(mPreferenceKey);
        appsList = (ListView) mFragmentView.findViewById(R.id.apps_list);
        allApps = getAppsList();
        appsListAdapter = new AppsListAdapter(mActivity, this);
        appsList.setAdapter(appsListAdapter);
        appsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String value = "";
                ActivityName = allApps.get(arg2).getAppsActivityName();
                appPackName = allApps.get(arg2).getAppsPackName();
                value = appPackName + ";" + ActivityName;
                clickPosition = arg2;
                appsListAdapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra(SettingUtils.KEY, mPreferenceKey);
                intent.putExtra(SettingUtils.VALUE, value);
                if (mActivity.getIntent().getBooleanExtra("bSingleClick", false)) {
                    mActivity.setResult(-1, intent);
                }
                if (mActivity.getIntent().getBooleanExtra("bLongClick", false)) {
                    mActivity.setResult(-2, intent);
                }
                mActivity.finish();
            }

        });
        return mFragmentView;
    }

    private void initDeafultSideKey(String preferenceKey){
        if (preferenceKey != null && preferenceKey.length() > 0) {
            String detail = SettingUtils.getSideKeyDetail(mActivity, SettingUtils.getDetailKey(preferenceKey));
            if (detail != null && detail.length() > 0 && detail.contains(";")) {
                String detailArray[] = detail.split(";");
                if (detailArray.length == 2) {
                    appPackName = detailArray[0];
                    ActivityName = detailArray[1];
                } else {
                    appPackName = "";
                    ActivityName = "";
                }
            } else {
                appPackName = "";
                ActivityName = "";
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    int getItemCount() {
        return allApps == null ? 0 : allApps.size();
    }

    void bindItemData(AppListItem listItem) {
        listItem.mCacheNameView.setText(allApps.get(listItem.getPosition()).getAppName());
        listItem.mImgView.setImageDrawable(allApps.get(listItem.getPosition()).getImageView());
        listItem.mRadioButton.setClickable(false);
        if (listItem.getPosition() == clickPosition) {
            listItem.mRadioButton.setChecked(true);
        } else {
            listItem.mRadioButton.setChecked(false);
        }
    }

    private List<AppsListBean> getAppsList() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager pManager = mActivity.getPackageManager();
        List<AppsListBean>listApps = new ArrayList<AppsListBean>();
        List<ResolveInfo> resolveInfos = pManager.queryIntentActivities(mainIntent, PackageManager.GET_INTENT_FILTERS);
        int i = 0;
        for (ResolveInfo resolveInfo : resolveInfos) {
            String name = (String) resolveInfo.loadLabel(pManager);
            String activityName = resolveInfo.activityInfo.name;
            String packname = resolveInfo.activityInfo.packageName;

            Drawable icon = resolveInfo.activityInfo.loadIcon(pManager);
            if(name == null) {
                name = resolveInfo.activityInfo.name;
            }
            AppsListBean appListBean = new AppsListBean();
            appListBean.setImageView(icon);
            appListBean.setAppName(name);
            appListBean.setAppsActivityName(activityName);
            appListBean.setAppsPackName(packname);
            if(packname.equals(appPackName) && activityName.equals(ActivityName)){
                clickPosition=i;
            }
            if (!isInSkipList(packname, activityName)) {
                listApps.add(appListBean);
                i++;
            }
        }

        if (ActivityName == null || ActivityName.length() <= 0) {
            clickPosition=i;
        }
        return listApps;
    }

    private boolean isInSkipList(String packname, String activityName) {
        int size = SKIP_LIST.length;
        for (int i = 0; i < size; i++) {
            if (packname.equals(SKIP_LIST[i][0]) && activityName.equals(SKIP_LIST[i][1])) {
                return true;
            }
        }
        return false;
    }

    class AppListInfo {
        Drawable icon;
        String name;
        long cachesize;
        String packageName;
    }
}
