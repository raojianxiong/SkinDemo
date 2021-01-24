package com.rjx.skinlib;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

import com.rjx.skinlib.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.Observable;

/**
 * @author Jianxiong Rao
 */
public class ApplicationActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private Observable mObservable;
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new ArrayMap<>();

    public ApplicationActivityLifecycle(Observable observable) {
        mObservable = observable;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        //更新状态栏
        SkinThemeUtils.updateStatusBarColor(activity);
        //更新布局
        LayoutInflater layoutInflater = activity.getLayoutInflater();

        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        //反射
        try {
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);

        }else{
            try {
                Field field = LayoutInflater.class.getDeclaredField("mFactory2");
                field.setAccessible(true);
                field.set(layoutInflater,skinLayoutInflaterFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        mObservable.addObserver(skinLayoutInflaterFactory);

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        SkinLayoutInflaterFactory observer = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
    }
}
