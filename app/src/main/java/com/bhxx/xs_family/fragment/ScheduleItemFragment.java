package com.bhxx.xs_family.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.ScheduleAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ScheduleModel;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

public class ScheduleItemFragment extends LazyLoadFragment {

    private int type;
    private ListView rest_schedule_list;
    private boolean isPrepared = false;
    private boolean hasLoadOnce = false;
    private ScheduleAdapter adapter;
    private ArrayList<ScheduleModel> datas;
    private TextView schedule_title;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Handler_Inject.injectFragment(this, view);
        if (getContentLayoutId() > 0) {
            this.rest_schedule_list = (ListView) view.findViewById(R.id.rest_schedule_list);
        }
        isPrepared = true;
        lazyLoad();
    }

    /**
     * 传入不同值来实例化不同的作息表页面
     *
     * @param type
     */
    public static ScheduleItemFragment getInstance(ArrayList<ScheduleModel> datas, int type) {
        Log.i("Tag","type="+type);
        ScheduleItemFragment fragment = new ScheduleItemFragment();
        Bundle bd = new Bundle();
        bd.putParcelableArrayList("datas", datas);
        bd.putInt("type", type);
        fragment.setArguments(bd);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.datas = getArguments().getParcelableArrayList("datas");
            this.type = getArguments().getInt("type");
        }
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || hasLoadOnce || !isVisible) {
            return;
        }
        hasLoadOnce = true;
        showLoading();

        showContent();
        View head = LayoutInflater.from(getActivity()).inflate(R.layout.rest_schedule_head, null);
        schedule_title = (TextView) head.findViewById(R.id.schedule_title);
        switch (this.type) {

            case 0:
                schedule_title.setText("小班");
                break;
            case 1:
                schedule_title.setText("中班");
                break;
            case 2:
                schedule_title.setText("大班");
                break;
            case 3:
                schedule_title.setText("托班");
                break;
        }
        rest_schedule_list.addHeaderView(head);
        adapter = new ScheduleAdapter(this.datas, getActivity(), R.layout.rest_schedule_item);
        rest_schedule_list.setAdapter(adapter);
    }

    @Override
    protected int getLoadingLayoutId() {
        return R.layout.loading_layout;
    }

    @Override
    protected int getErrorLayoutId() {
        return 0;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.rest_schedule_layout;
    }

    @Override
    protected int getNothingLayoutId() {
        return 0;
    }
}
