package com.bhxx.xs_family.activity;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.AlbumAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.Album;
import com.bhxx.xs_family.beans.AlbumModel;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_album)
public class AlbumActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private int page = 1;
    private AlbumAdapter adapter;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView album_back;
        PullToRefreshLayout album_pull;
        PullableListView album_list;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(AlbumActivity.this);
        initAlbumList();
        v.album_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                adapter = null;
                page = 1;
                initAlbumList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initAlbumList();
            }
        });
    }

    private void initAlbumList() {
        showProgressDialog(AlbumActivity.this, "加载中...");
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("uId", App.app.getData("uId"));
        params.put("abClassId", App.app.getData("classId"));
        String token = TokenUtils.getInstance().configParams(GlobalValues.CLASS_ALBUM + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.CLASS_ALBUM, "ALBUM", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast("加载失败");
            v.album_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
            v.album_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            Log.i("TAG", response);
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<AlbumModel> beans = JsonUtils.getBeans(response, CommonListBean.class, AlbumModel.class);
                if (beans != null) {
                    if (beans.getSuccess()) {
                        if (beans.getRows() != null && beans.getRows().size() > 0) {
                            if (adapter == null) {
                                adapter = new AlbumAdapter(beans.getRows(), AlbumActivity.this);
                                v.album_list.setAdapter(adapter);
                            } else {
                                adapter.addDataListAtLast(beans.getRows());
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            if (page == 1) {
                                showToast("暂无内容");
                            }
                        }
                    }
                }
            } else {
                showToast("加载失败");
            }
            v.album_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
            v.album_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
    }

    /**
     * 跳转相册界面
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, AlbumActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.album_back:
                finish();
                break;
        }
    }
}
