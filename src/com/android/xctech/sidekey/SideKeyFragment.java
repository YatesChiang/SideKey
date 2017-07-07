package com.android.xctech.sidekey;

import java.util.Observable;
import java.util.Observer;
import android.app.Activity;
import android.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

public class SideKeyFragment extends Fragment implements Observer,
        AdapterView.OnItemClickListener{

    protected Activity mActivity;
    private static final String TAG = SideKeyFragment.class.getName();
    AttributeSet attribute;

    public SideKeyFragment() {
        mActivity = MainActivity.mActivity;
    }

    final void addParentActivity(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void update(Observable observable, Object data) {
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }

}
