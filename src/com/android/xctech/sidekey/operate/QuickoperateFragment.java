package com.android.xctech.sidekey.operate;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.xctech.sidekey.R;
import com.android.xctech.sidekey.SideKeyFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class QuickoperateFragment extends SideKeyFragment implements OnClickListener, OnItemClickListener{
    private static final String TAG = "QuickoperateFragment";
    private ListView appsList;
    private int clickPosition = -1;
    private ArrayList<HashMap<String, Object>> allApps;
    private MyAdapter adapter;
    private String mPreferenceKey = "";
    OperateListItem appListItem;
    String[] operateListName;
    String[] quickOperateListKey;
    public QuickoperateFragment() {
    }

    int operateListImage[] = {
            R.drawable.sidekey_none,
            R.drawable.sidekey_record,
            R.drawable.sidekey_offscreencamera,
            R.drawable.sidekey_flashlight,
            R.drawable.sidekey_screenshot,
            R.drawable.sidekey_silentmode,
            R.drawable.sidekey_frontcamera,
    };

    // XCSW SWEL-249 add by like 2016.08.09 (Begin)
    int  operateListImage_customer[] = {
            R.drawable.sidekey_camera,
            R.drawable.sidekey_silentmode,
            R.drawable.sidekey_browser,
            R.drawable.sidekey_settings,
            R.drawable.sidekey_email,
    };
    // XCSW SWEL-249 add by like 2016.08.09 (End)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "onCreateView() QuickoperateFragment == ");
        if (mActivity == null) {
            return null;
        }
        View fragmentView = inflater.inflate(R.layout.quickoperate_fragment, container, false);
        appsList = (ListView) fragmentView.findViewById(R.id.operate_apps_list);
        allApps = getAppsList();
        adapter = new MyAdapter();
        appsList.setAdapter(adapter);
        appsList.setOnItemClickListener(this);
        mPreferenceKey = mActivity.getIntent().getStringExtra(SettingUtils.KEY);
        initDeafultSideKey(mPreferenceKey);
        return fragmentView;
    }

    public ArrayList<HashMap<String, Object>> getAppsList(){
        ArrayList<HashMap<String, Object>>  list = new ArrayList<HashMap<String, Object>>();
        operateListName = mActivity.getResources().getStringArray(R.array.quickoperate_setting_list_name);
        // XCSW SWEL-249 add by like 2016.08.09 (Begin)
        Boolean quickstart_customer = getResources().getBoolean(R.bool.quickstart_customer);
        if (quickstart_customer) {
             for (int i = 0; i < operateListImage_customer.length; i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("quick_image", operateListImage_customer[i]);
                    map.put("quick_name", operateListName[i]);
                    list.add(map);
             }
          } else {
               for (int i = 0; i < operateListImage.length; i++) {
                      HashMap<String, Object> map = new HashMap<String, Object>();
                      map.put("quick_image", operateListImage[i]);
                      map.put("quick_name", operateListName[i]);
                      list.add(map);
              }
          }
          // XCSW SWEL-249 add by like 2016.08.09 (End)
        return list;
    }

    void bindItemData(OperateListItem listItem) {
        listItem.mAppNameView.setText((CharSequence) allApps.get(listItem.getPosition()).get("quick_name"));
        listItem.mImgView.setBackgroundResource((Integer) allApps.get(listItem.getPosition()).get("quick_image"));
        listItem.mRadioButton.setClickable(false);
        if (listItem.getPosition() == clickPosition) {
            listItem.mRadioButton.setChecked(true);
        } else {
            listItem.mRadioButton.setChecked(false);
        }
    }

    private void initDeafultSideKey(String preferenceKey){
        if (preferenceKey != null && preferenceKey.length() > 0) {
            String detail = SettingUtils.getSideKeyDetail(mActivity, SettingUtils.getDetailKey(preferenceKey));
            quickOperateListKey = mActivity.getResources().getStringArray(R.array.quickoperate_setting_list_key);
            for (int i=0;i<quickOperateListKey.length;i++) {
                if (detail.equals(quickOperateListKey[i])) {
                    clickPosition = (int) adapter.getItemId(i);
                }
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
        default:
            break;
        }
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allApps.size();
        }

        public MyAdapter() {
        }

        @Override
        public Object getItem(int position) {
            return allApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = LayoutInflater.from(mActivity).inflate(R.layout.operate_apps_list, parent, false);
            }
            appListItem = new OperateListItem(view, position);
            bindItemData(appListItem);
            return view;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        super.onItemClick(arg0, arg1, arg2, arg3);
        clickPosition = arg2;
        adapter.notifyDataSetChanged();
        Intent intent = new Intent();
        intent.putExtra(SettingUtils.KEY, mPreferenceKey);
        intent.putExtra(SettingUtils.VALUE, quickOperateListKey[arg2]);
        if (mActivity.getIntent().getBooleanExtra("bSingleClick", false)) {
            mActivity.setResult(-1, intent);
        }
        if (mActivity.getIntent().getBooleanExtra("bLongClick", false)) {
            mActivity.setResult(-2, intent);
        }
        mActivity.finish();
    }

}
