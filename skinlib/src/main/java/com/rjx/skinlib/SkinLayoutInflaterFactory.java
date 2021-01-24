package com.rjx.skinlib;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rjx.skinlib.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Jianxiong Rao
 * @description 接管系统View的构造过程
 * @see android.view.LayoutInflater.Factory2
 */
public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {
    /**
     *  com.android.internal.policy.PhoneLayoutInflater
     */
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            "android.view."
    };

    static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();


    /** 当选择新皮肤后需要替换View与之对应的属性
     * 页面属性管理器
     */
    private SkinAttribute skinAttribute;
    /**
     * 用于获取窗口的状态框的信息
     */
    private Activity activity;

    public SkinLayoutInflaterFactory(Activity activity) {
        this.activity = activity;
        this.skinAttribute = new SkinAttribute();
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        //换肤就是在需要时候替换 View的属性(src、background等)
        //创建 View,从而修改View属性
        View view = createSdkView(name, context, attrs);
        if (view == null) {
            view = createView(name, context, attrs);
        }
        if (view != null) {
            //找到这个view 保存并更新视图
            skinAttribute.look(view, attrs);
        }
        return view;
    }

    private View createSdkView(String name, Context context, AttributeSet attrs) {
        //包含 . 的话说明是自定义继承SkinViewSupport或者support中的视图,androidx,不是SDK中的view
        if (name.contains(".")) {
            return null;
        }
        //不包含就需要在解析的节点name前加上 android.widget 去反射
        for (int i = 0; i < mClassPrefixList.length; i++) {
            View view = createView(mClassPrefixList[i] + name, context, attrs);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return null;
    }

    private View createView(String name, Context context, AttributeSet
            attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
        }
        return null;
    }

    private Constructor<? extends View> findConstructor(Context context, String name) {
        Log.i("rjx","name -> "+name);
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                //两个参数
                constructor = clazz.getConstructor(mConstructorSignature);
                sConstructorMap.put(name, constructor);
            } catch (Exception e) {

            }

        }
        return constructor;
    }

    @Override
    public void update(Observable o, Object arg) {
        //作为观察者被通知更新
        SkinThemeUtils.updateStatusBarColor(activity);
        if(skinAttribute != null){
            skinAttribute.applySkin();
        }
    }
}
