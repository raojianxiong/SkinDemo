package com.rjx.skinlib;

import android.util.AttributeSet;
import android.view.View;

import com.rjx.skinlib.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jianxiong Rao
 * @description : 属性集合 需要针对哪些属性进行替换
 */
public class SkinAttribute {
    private static final List<String> mAttribute = new ArrayList<>();

    static {
        mAttribute.add("background");
        mAttribute.add("src");
        mAttribute.add("textColor");
        mAttribute.add("drawableLeft");
        mAttribute.add("drawableTop");
        mAttribute.add("drawableRight");
        mAttribute.add("drawableBottom");
    }

    /**记录换肤需要操作的View*/
    private List<SkinView> mSkinViews = new ArrayList<>();

    /**记录View中哪几个属性需要换肤,找到当前页面的所有属性*/
   public  void look(View view, AttributeSet attributeSet){
        List<SkinPair> mSkinPairs = new ArrayList<>();

        for(int i = 0;i<attributeSet.getAttributeCount();i++){
            //获得属性名
            String attributeName = attributeSet.getAttributeName(i);
            if(mAttribute.contains(attributeName)){
                //属性名写法
                //#ffff
                //?attr/xxx
                //@string/xxx
                String attributeValue = attributeSet.getAttributeValue(i);
                //如果是写死的字符串 #fffff
                if(attributeValue.startsWith("#")){
                    continue;
                }
                int resId;
                //以?开头的表示用属性
                if(attributeValue.startsWith("?")){
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(),new int[]{attrId})[0];
                }else{
                    //以@开头的
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                SkinPair skinPair = new SkinPair(attributeName,resId);
                mSkinPairs.add(skinPair);
            }
        }
        if(!mSkinPairs.isEmpty() || view instanceof  SkinViewSupport){
            //选择皮肤 更新
            SkinView skinView = new SkinView(view,mSkinPairs);
            skinView.applySkin();
            mSkinViews.add(skinView);
        }
    }

    public void applySkin(){
       for(SkinView skinView:mSkinViews){
           skinView.applySkin();
       }
    }
}
