package com.bhxx.xs_family.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/25.
 */
public class FoodRecipe implements Serializable {

    // 食谱id
    private int repId;
    // 食谱创建时间
    private String repCreateTime;
    //食谱时间
    private String repTime;
    // 食谱描述
    private String repDesction;
    // 食谱点赞人数
    private int repLikeCount;
    // 食谱反对人数
    private int repUnLikeCount;
    // 院所id
    private int reParkId;
    //菜品
    private List<FoodDishe> appDishes;
    //点心
    private List<FoodDishe>appDessert;

    public int getRepId() {
        return repId;
    }

    public void setRepId(int repId) {
        this.repId = repId;
    }

    public String getRepCreateTime() {
        return repCreateTime;
    }

    public void setRepCreateTime(String repCreateTime) {
        this.repCreateTime = repCreateTime;
    }

    public String getRepTime() {
        return repTime;
    }

    public void setRepTime(String repTime) {
        this.repTime = repTime;
    }

    public List<FoodDishe> getAppDishes() {
        return appDishes;
    }

    public void setAppDishes(List<FoodDishe> appDishes) {
        this.appDishes = appDishes;
    }

    public List<FoodDishe> getAppDessert() {
        return appDessert;
    }

    public void setAppDessert(List<FoodDishe> appDessert) {
        this.appDessert = appDessert;
    }

    public int getReParkId() {
        return reParkId;
    }

    public void setReParkId(int reParkId) {
        this.reParkId = reParkId;
    }

    public int getRepUnLikeCount() {
        return repUnLikeCount;
    }

    public void setRepUnLikeCount(int repUnLikeCount) {
        this.repUnLikeCount = repUnLikeCount;
    }

    public int getRepLikeCount() {
        return repLikeCount;
    }

    public void setRepLikeCount(int repLikeCount) {
        this.repLikeCount = repLikeCount;
    }

    public String getRepDesction() {
        return repDesction;
    }

    public void setRepDesction(String repDesction) {
        this.repDesction = repDesction;
    }
}
