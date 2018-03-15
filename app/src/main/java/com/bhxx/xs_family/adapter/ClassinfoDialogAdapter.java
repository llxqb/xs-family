package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.DynamicDetailsActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.ClassModel;
import com.bhxx.xs_family.beans.DynamicModel;
import com.bhxx.xs_family.entity.LookCheckStatusEntity;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ClassinfoDialogAdapter extends CommonAdapter<ClassModel> {
    public ClassinfoDialogAdapter(List<ClassModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final ClassModel data) {
       if(!TextUtils.isEmpty(data.getClName())){
           holder.setText(R.id.classinfodialog_classname,data.getClName());
       }

    }
}
