package com.rjx.skindemo;

import android.app.Application;

import com.rjx.skinlib.SkinManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().init(this);
    }
}
