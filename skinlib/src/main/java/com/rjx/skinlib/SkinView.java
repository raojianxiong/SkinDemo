package com.rjx.skinlib;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.rjx.skinlib.utils.SkinResources;

import java.util.List;

/**
 * @author Jianxiong Rao
 * 每个换肤需要操作的View以及它的属性集合
 */
public class SkinView {
    View view;
    List<SkinPair> skinPairs;

    public SkinView(View view, List<SkinPair> skinPairs) {
        this.view = view;
        this.skinPairs = skinPairs;
    }
    public void applySkin(){
        applySkinSupport();
        for(SkinPair skinPair:skinPairs){
            Drawable left = null, top = null, right = null, bottom = null;
            switch (skinPair.attributeName) {
                case "background":
                    Object background = SkinResources.getInstance().getBackground(skinPair
                            .resId);
                    //背景可能是 @color 也可能是 @drawable
                    if (background instanceof Integer) {
                        view.setBackgroundColor((int) background);
                    } else {
                        ViewCompat.setBackground(view, (Drawable) background);
                    }
                    break;
                case "src":
                    background = SkinResources.getInstance().getBackground(skinPair
                            .resId);
                    if (background instanceof Integer) {
                        ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                background));
                    } else {
                        ((ImageView) view).setImageDrawable((Drawable) background);
                    }
                    break;
                case "textColor":
                    ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList
                            (skinPair.resId));
                    break;
                case "drawableLeft":
                    left = SkinResources.getInstance().getDrawable(skinPair.resId);
                    break;
                case "drawableTop":
                    top = SkinResources.getInstance().getDrawable(skinPair.resId);
                    break;
                case "drawableRight":
                    right = SkinResources.getInstance().getDrawable(skinPair.resId);
                    break;
                case "drawableBottom":
                    bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                    break;
                default:
                    break;
            }
            if (null != left || null != right || null != top || null != bottom) {
                ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right,
                        bottom);
            }
        }
    }

    /**
     * 自定义实现SkinViewSupport的类
     */
    private void applySkinSupport(){
        if (view instanceof SkinViewSupport) {
            ((SkinViewSupport) view).applySkin();
        }
    }
}
