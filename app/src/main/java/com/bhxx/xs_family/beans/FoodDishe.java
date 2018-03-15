package com.bhxx.xs_family.beans;

/**
 * Created by Administrator on 2016/8/25.
 */
public class FoodDishe {

    //菜品id
    private int dsId;
    //菜品图片
    private String dsPic;
    //食谱id
    private int dsRecipesId;
    //菜品标题
    private String dsTitle;

    public int getDsId() {
        return dsId;
    }

    public void setDsId(int dsId) {
        this.dsId = dsId;
    }

    public String getDsPic() {
        return dsPic;
    }

    public void setDsPic(String dsPic) {
        this.dsPic = dsPic;
    }

    public int getDsRecipesId() {
        return dsRecipesId;
    }

    public void setDsRecipesId(int dsRecipesId) {
        this.dsRecipesId = dsRecipesId;
    }

    public String getDsTitle() {
        return dsTitle;
    }

    public void setDsTitle(String dsTitle) {
        this.dsTitle = dsTitle;
    }
}
