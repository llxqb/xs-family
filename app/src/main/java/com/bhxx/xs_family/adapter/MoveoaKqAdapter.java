package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.DynamicDetailsActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.CheckworkModel;
import com.bhxx.xs_family.beans.DynamicModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MoveoaKqAdapter extends CommonAdapter<CheckworkModel> {
    public MoveoaKqAdapter(List<CheckworkModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final CheckworkModel data) {
        if (!TextUtils.isEmpty(data.getCwCreateTime())) {
            holder.setText(R.id.moveoa_kq_date, data.getCwCreateTime());
        }

        if(clock(data.getCwCreateTime()).equals("am")){
            holder.setImageResource(R.id.moveoa_kq_img,R.mipmap.user_card_am);
        }else{
            holder.setImageResource(R.id.moveoa_kq_img,R.mipmap.user_card_pm);
        }

        if(!TextUtils.isEmpty(data.getCwCreateTime())){
            holder.setText(R.id.moveoa_kq_date,data.getCwCreateTime());
        }
        if (!TextUtils.isEmpty(data.getCwPlace())) {
            holder.setText(R.id.moveoa_kq_place, data.getCwPlace());
        }

    }

    private String clock(String time){
        // text.substring(0, text.length() - 3);
        String aa = time.substring(time.length()-5,time.length());
        String[] bb = aa.split(":");
        int a = Integer.parseInt(bb[0]);
        int b = Integer.parseInt(bb[1]);
        int totaltime = a*60+b-12*60;
        if(totaltime>=0){
            return "pm";
        }else{
            return "am";
        }
    }
}
