package com.rjx.skinlib;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.rjx.skinlib.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

/**
 * @author Jianxiong Rao
 */
public class SkinManager extends Observable {
    /**
     * Activity生命周期回调
     */
    private ApplicationActivityLifecycle skinActivityLifecycle;
    private Application mContext;
    private volatile boolean isInit = false;
    private SkinManager() {
    }

    private static class SingleHolder {
        private static SkinManager S_INSTANCE = new SkinManager();
    }

    public static SkinManager getInstance() {
        return SingleHolder.S_INSTANCE;
    }

    /**
     * 注意首次初始化时需要从配置文件读取使用哪个皮肤,然后应用
     * @param application
     */
    public  void init(Application application) {
        if(isInit){
            return;
        }
        mContext = application;
        //记录当前使用的皮肤
        SkinPreference.init(application);
        //资源管理类 用于从 app/皮肤 中加载资源
        SkinResources.init(application);
        //注册Activity生命周期,并设置被观察者
        skinActivityLifecycle = new ApplicationActivityLifecycle(this);
        application.registerActivityLifecycleCallbacks(skinActivityLifecycle);
        //加载上次使用保存的皮肤
        loadSkin(SkinPreference.getInstance().getSkin());
    }
    /**
     * 记载皮肤并应用
     *
     * @param skinPath 皮肤路径 如果为空则使用默认皮肤
     */
    public void loadSkin(String skinPath) {
        if (TextUtils.isEmpty(skinPath)) {
            //还原默认皮肤
            SkinPreference.getInstance().reset();
            SkinResources.getInstance().reset();
        } else {
            try {
                //宿主app的 resources;
                Resources appResource = mContext.getResources();
//
                //反射创建AssetManager 与 Resource
                AssetManager assetManager = AssetManager.class.newInstance();
                //资源路径设置 目录或压缩包
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
                        String.class);
                addAssetPath.invoke(assetManager, skinPath);

                //根据当前的设备显示器信息 与 配置(横竖屏、语言等) 创建Resources
                Resources skinResource = new Resources(assetManager, appResource.getDisplayMetrics
                        (), appResource.getConfiguration());

                //获取外部Apk(皮肤包) 包名
                PackageManager mPm = mContext.getPackageManager();
                PackageInfo info = mPm.getPackageArchiveInfo(skinPath, PackageManager
                        .GET_ACTIVITIES);
                String packageName = info.packageName;
                SkinResources.getInstance().applySkin(skinResource, packageName);

                //记录
                SkinPreference.getInstance().setSkin(skinPath);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //通知采集的View 更新皮肤
        //被观察者改变 通知所有观察者
        setChanged();
        notifyObservers(null);
    }
}
