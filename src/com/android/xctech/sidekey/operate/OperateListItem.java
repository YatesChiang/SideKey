package com.android.xctech.sidekey.operate;

import com.android.xctech.sidekey.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class OperateListItem {

    TextView mAppNameView;
    RadioButton mRadioButton;
    ImageView mImgView;
    public int mPos;

    OperateListItem(View view, int position) {
        mAppNameView = (TextView) view.findViewById(R.id.operate_apps_name);
        mRadioButton = (RadioButton) view.findViewById(R.id.operate_apps_radio);
        mImgView = (ImageView) view.findViewById(R.id.operate_apps_icon);
        mPos = position;
    }

    int getPosition() {
        return mPos;
    }
}
