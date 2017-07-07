package com.android.xctech.sidekey.operate;

import com.android.xctech.sidekey.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class AppListItem {

    TextView mCacheNameView;
    RadioButton mRadioButton;
    ImageView mImgView;
    public int mPos;

    AppListItem(View view, int position) {
        mCacheNameView = (TextView) view.findViewById(R.id.apps_list_name);
        mRadioButton = (RadioButton) view.findViewById(R.id.apps_list_radio);
        mImgView = (ImageView) view.findViewById(R.id.apps_list_icon);
        mPos = position;
    }

    int getPosition() {
        return mPos;
    }
}
