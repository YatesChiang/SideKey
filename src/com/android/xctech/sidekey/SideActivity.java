package com.android.xctech.sidekey;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.app.ActionBar;
import android.view.MenuItem;


public class SideActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate() start == "+SideActivity.class.getSimpleName());
        setContentView(R.layout.xcside_activity);

        ActionBar mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item); 
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
