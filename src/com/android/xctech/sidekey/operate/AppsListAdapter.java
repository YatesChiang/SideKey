package com.android.xctech.sidekey.operate;

import com.android.xctech.sidekey.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AppsListAdapter extends BaseAdapter{

    private Context context;
    private PickAppsFragment mFragment;

    public AppsListAdapter(Context context,PickAppsFragment fragment){
        this.context = context;
        this.mFragment = fragment;
    }
    @Override
    public int getCount() {
        return mFragment.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.pick_apps_list, parent, false);
        }
        AppListItem appListItem = new AppListItem(view, position);
        mFragment.bindItemData(appListItem);
        return view;
    }
}
