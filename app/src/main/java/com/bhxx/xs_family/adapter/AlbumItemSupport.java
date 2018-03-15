package com.bhxx.xs_family.adapter;

import android.text.TextUtils;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.MultiTypeSupport;
import com.bhxx.xs_family.beans.AlbumModel;

public class AlbumItemSupport implements MultiTypeSupport<AlbumModel> {
    private static final int ITEM_TYPE_NO_PIC = 0;
    private static final int ITEM_TYPE_SING_PIC = 1;
    private static final int ITEM_TYPE_DOUBLE_PIC = 2;
    private static final int ITEM_TYPE_LIST_PICS = 3;

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position, AlbumModel data) {
        //获取所有图片  得到数组
        if (!TextUtils.isEmpty(data.getAbPics())) {
            String[] pics = data.getAbPics().split(";");
            if (pics.length == 1) {
                return ITEM_TYPE_SING_PIC;
            } else if (pics.length == 2) {
                return ITEM_TYPE_DOUBLE_PIC;
            } else if (pics.length > 2) {
                return ITEM_TYPE_LIST_PICS;
            } else {
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
                return R.layout.album_no_pic_item;
            case ITEM_TYPE_SING_PIC:
                return R.layout.album_single_pic_item;
            case ITEM_TYPE_DOUBLE_PIC:
                return R.layout.album_double_pic_item;
            case ITEM_TYPE_LIST_PICS:
                return R.layout.album_list_pics_item;
            default:
                break;
        }
        return 0;
    }
}
