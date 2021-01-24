package com.rjx.skinlib;

/**
 * @author Jianxiong Rao
 */
public class SkinPair {
    /**属性名*/
    String attributeName;
    /**对应的资源ID*/
    int resId;

    public SkinPair(String attributeName, int resId) {
        this.attributeName = attributeName;
        this.resId = resId;
    }
}
