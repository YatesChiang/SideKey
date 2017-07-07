package com.android.xctech.sidekey;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.android.xctech.sidekey.operate.PickAppsFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public final class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    private ViewPager mViewPager;
    private final PageChangeListener mPageChangeListener = new PageChangeListener();
    private final List<SideKeyFragment> mFragmentList = new ArrayList<SideKeyFragment>();
    private TextView mFragmentTitle, mQuickOperateTitle, mPickAppTitle;
    private ViewPagerAdapter mViewPagerAdapter;
    static MainActivity mActivity;
    private String[] mFragmentInfos;

    private final Observable mObservable = new Observable() {
        @Override
        public boolean hasChanged() {
            return true;
        }
    };

    public MainActivity() {
        mActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mQuickOperateTitle = (TextView) findViewById(R.id.quickoperate_title);
        mPickAppTitle = (TextView) findViewById(R.id.pickapp_title);
        mQuickOperateTitle.setOnClickListener(this);
        mPickAppTitle.setOnClickListener(this);
        initialiseFragments();
        mViewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
    }

    public void showFragment(int item) {
        mViewPager.setCurrentItem(item);
        mPageChangeListener.setCurrentPosition(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public int getFragmnetPosition() {
        for (int i = 0; i < mFragmentList.size(); i++) {
            Class<?> classTmp = mFragmentList.get(i).getClass();
            if (PickAppsFragment.class.equals(classTmp)) {
                return i;
            }
        }
        return -1;
    }

    public String getFragmentSystemInfo(int id) {
        return mFragmentInfos[id];
    }

    public void initialiseFragments() {
        mFragmentList.clear();
        FragmentsTable.initialise(mFragmentList);
        for (int i = 0; i < mFragmentList.size(); i++) {
            mFragmentList.get(i).addParentActivity(this);
            mObservable.addObserver(mFragmentList.get(i));
        }
        mQuickOperateTitle.setBackgroundResource(R.drawable.btn_sidetitle_all_sel);
    }

    private void updateFragmentTitle(int pos) {
        if (pos == 1) {
            mQuickOperateTitle.setBackgroundResource(R.drawable.btn_sidetitle_all);
            mPickAppTitle.setBackgroundResource(R.drawable.btn_sidetitle_all_sel);
        } else if (pos == 0) {
            mQuickOperateTitle.setBackgroundResource(R.drawable.btn_sidetitle_all_sel);
            mPickAppTitle.setBackgroundResource(R.drawable.btn_sidetitle_all);
        }

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return getFragmentCount();
        }
    }

    private int getFragmentCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    private SideKeyFragment getFragment(int id) {
        if (id >= mFragmentList.size()) {

            id = mFragmentList.size() - 1;
        }

        return mFragmentList.get(id);
    }

    private class PageChangeListener implements OnPageChangeListener {
        private int mCurrentPosition = 0;
        private int mNextPosition = -1;

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mNextPosition = position;
            mCurrentPosition = position;
        }

        public void setCurrentPosition(int position) {
            mCurrentPosition = position;
        }

        public int getCurrentPosition() {
            return mCurrentPosition;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
            case ViewPager.SCROLL_STATE_IDLE: {
                if (mNextPosition >= 0) {
                    mCurrentPosition = mNextPosition;
                }
                updateFragmentTitle(mCurrentPosition);
                break;
            }
            case ViewPager.SCROLL_STATE_DRAGGING: {
                break;
            }
            case ViewPager.SCROLL_STATE_SETTLING: {
                break;
            }
            default:
                break;
            }
        }
    }

    int getCurrentFragment() {
        return mPageChangeListener.getCurrentPosition();
    }

    private void changeButton(View view) {
        if (view != mQuickOperateTitle) {
            mQuickOperateTitle.setBackgroundResource(R.drawable.btn_sidetitle_all);
        } else {
            mQuickOperateTitle.setBackgroundResource(R.drawable.btn_sidetitle_all_sel);
        }
        if (view != mPickAppTitle) {
            mPickAppTitle.setBackgroundResource(R.drawable.btn_sidetitle_all);
        } else {
            mPickAppTitle.setBackgroundResource(R.drawable.btn_sidetitle_all_sel);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.quickoperate_title:
            changeButton(view);
            showFragment(FragmentsTable.FRAGMENT_QUICKOPERATE_POSITION);
            break;
        case R.id.pickapp_title:
            changeButton(view);
            showFragment(FragmentsTable.FRAGMENT_PICKAPP_POSITION);
            break;
        default:
            break;
        }
    }

}
