package com.android.xctech.sidekey.operate;

import android.graphics.drawable.Drawable;

public class AppsListBean {
    private Drawable drawable;
    private String appName;
    private String appsActivityName;
    private String appsPackName;
    public String getAppsPackName() {
        return appsPackName;
    }
    public void setAppsPackName(String appsPackName) {
        this.appsPackName = appsPackName;
    }
    public Drawable getDrawable() {
        return drawable;
    }
    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
    public String getAppsActivityName() {
        return appsActivityName;
    }
    public void setAppsActivityName(String appsActivityName) {
        this.appsActivityName = appsActivityName;
    }
    public Drawable getImageView() {
        return drawable;
    }
    public void setImageView(Drawable drawable) {
        this.drawable = drawable;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public AppsListBean() {
        super();
    }

}
