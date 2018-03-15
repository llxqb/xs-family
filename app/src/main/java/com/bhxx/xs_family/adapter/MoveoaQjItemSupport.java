package com.bhxx.xs_family.adapter;

import android.text.TextUtils;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.MultiTypeSupport;
import com.bhxx.xs_family.beans.LeaveModel;
import com.bhxx.xs_family.beans.ReimbursementModel;

public class MoveoaQjItemSupport implements MultiTypeSupport<LeaveModel> {
    private static final int ITEM_TYPE_NO_PIC = 0;
    private static final int ITEM_TYPE_SING_PIC = 1;
    private static final int ITEM_TYPE_DOUBLE_PIC = 2;
    private static final int ITEM_TYPE_THREE_PICS = 3;
    private static final int ITEM_TYPE_FOUR_PICS = 4;

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position, LeaveModel data) {
        //获取所有图片  得到数组
        if (!TextUtils.isEmpty(data.getLePics())) {
            String[] pics = data.getLePics().split(";");
            if (pics.length == 1) {
                return ITEM_TYPE_SING_PIC;
            } else if (pics.length == 2) {
                return ITEM_TYPE_DOUBLE_PIC;
            } else if (pics.length ==3) {
                return ITEM_TYPE_THREE_PICS;
            } else if(pics.length ==4){
                return ITEM_TYPE_FOUR_PICS;
            }
            else {
                return ITEM_TYPE_NO_PIC;
            }
        } else {
            return ITEM_TYPE_NO_PIC;
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case ITEM_TYPE_NO_PIC:
                return R.layout.moveoaqj_no_pic_item;
            case ITEM_TYPE_SING_PIC:
                return R.layout.moveoaqj_one_pic_item;
            case ITEM_TYPE_DOUBLE_PIC:
                return R.layout.moveoaqj_two_pic_item;
            case ITEM_TYPE_THREE_PICS:
                return R.layout.moveoaqj_three_pic_item;
            case ITEM_TYPE_FOUR_PICS:
                return R.layout.moveoaqj_four_pic_item;
            default:
                break;
        }
        return 0;
    }
}
