package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.MultiTypeSupport;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.FoodDishe;
import com.bhxx.xs_family.beans.FoodRecipe;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.util.TextUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/25.
 */
public class FoodMenuAdapter extends CommonAdapter<FoodDishe> {

    public FoodMenuAdapter(List<FoodDishe> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, FoodDishe data) {
        if (!TextUtils.isEmpty(data.getDsTitle())) {
            holder.setText(R.id.food_name, data.getDsTitle());
        }

        if (!TextUtils.isEmpty(data.getDsTitle())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + data.getDsPic(), (ImageView) holder.getView(R.id.food_image), LoadImage.getDefaultOptions());
        }
    }
}
