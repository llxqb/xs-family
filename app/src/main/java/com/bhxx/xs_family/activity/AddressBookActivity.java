package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.AddressBookAdapter;
import com.bhxx.xs_family.application.App;
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

@InjectLayer(R.layout.activity_address_book)
public class AddressBookActivity extends BasicActivity {

    @InjectAll
    private Views v;
    private AddressBookAdapter adapter;
    private int page = 1;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView address_book_back;
        PullToRefreshLayout address_book_pull;
        PullableListView address_book_list;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(AddressBookActivity.this);
        initList();
        v.address_book_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                adapter = null;
                initList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initList();
            }
        });
    }

    private void initList() {
        showProgressDialog(AddressBookActivity.this, "加载中...");
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("uId", App.app.getData("uId"));
        String token = TokenUtils.getInstance().configParams(GlobalValues.QUERY_ADDRESS_BOOK + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.QUERY_ADDRESS_BOOK, "ADDRESS_BOOK", params, new MyStringCallback());
    }

    /**
     * 跳转通讯录页面
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, AddressBookActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.address_book_back:
                finish();
                break;
        }
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            if (page == 1) {
                showToast("加载失败");
            }
            v.address_book_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.address_book_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            Log.i("Test",response);
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<UserModel> beans = JsonUtils.getBeans(response, CommonListBean.class, UserModel.class);
                if (beans != null) {
                    if (beans.getSuccess()) {
                        if (beans.getRows() != null && beans.getRows().size() > 0) {
                            if (adapter == null) {
                                adapter = new AddressBookAdapter(beans.getRows(), AddressBookActivity.this, R.layout.address_book_item);
                                v.address_book_list.setAdapter(adapter);
                            } else {
                                adapter.addDataListAtLast(beans.getRows());
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            if (page == 1) {
                                showToast("暂无内容");
                            }
                        }
                    } else {
                        if (page == 1) {
                            showToast("加载失败");
                        }
                    }
                } else {
                    if (page == 1) {
                        showToast("加载失败");
                    }
                }
            } else {
                if (page == 1) {
                    showToast("加载失败");
                }
            }
            v.address_book_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.address_book_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }
}
