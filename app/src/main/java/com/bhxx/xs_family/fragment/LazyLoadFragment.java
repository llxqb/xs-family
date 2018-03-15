package com.bhxx.xs_family.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bhxx.xs_family.application.App;

public abstract class LazyLoadFragment extends Fragment {

    protected boolean isVisible;
    private FrameLayout containerLayout;
    private View loadingLayout;
    private View errorLayout;
    private View contentLayout;
    private View nothingLayout;
    private ProgressDialog pd;
    private Toast toast;

    protected void showToast(int resId) {
        if (toast == null) {
            toast = Toast.makeText(App.app.getApplicationContext(), resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
        }
        toast.show();
    }

    protected void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(App.app.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        containerLayout = new FrameLayout(getActivity());
        containerLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        if (getLoadingLayoutId() > 0) {
            loadingLayout = inflater.inflate(getLoadingLayoutId(), containerLayout, false);
            containerLayout.addView(loadingLayout,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        if (getErrorLayoutId() > 0) {
            errorLayout = inflater.inflate(getErrorLayoutId(), containerLayout, false);
            containerLayout.addView(errorLayout,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            errorLayout.setVisibility(View.INVISIBLE);
        }

        if (getContentLayoutId() > 0) {
            contentLayout = inflater.inflate(getContentLayoutId(), null);
            containerLayout.addView(contentLayout,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            contentLayout.setVisibility(View.INVISIBLE);
        }
        if (getNothingLayoutId() > 0) {
            nothingLayout = inflater.inflate(getNothingLayoutId(), null);
            containerLayout.addView(nothingLayout,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            nothingLayout.setVisibility(View.INVISIBLE);
        }
        return containerLayout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {

            isVisible = true;
            onVisible();
        } else {

            isVisible = false;
            onInvisible();
        }
    }

    protected void onInvisible() {

    }

    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected abstract int getLoadingLayoutId();

    protected abstract int getErrorLayoutId();

    protected abstract int getContentLayoutId();

    protected abstract int getNothingLayoutId();

    /**
     * 显示加载中页面
     */
    public void showLoading() {

        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(View.INVISIBLE);
        }
        if (contentLayout != null) {
            contentLayout.setVisibility(View.INVISIBLE);
        }
        if (nothingLayout != null) {
            nothingLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示错误页面
     */
    public void showError() {

        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.INVISIBLE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(View.VISIBLE);
        }
        if (contentLayout != null) {
            contentLayout.setVisibility(View.INVISIBLE);
        }
        if (nothingLayout != null) {
            nothingLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示自定义View
     *
     * @param layoutId
     * @return
     */
    public View showCustomeLayout(int layoutId) {

        View targetView = containerLayout.findViewById(layoutId);
        if (targetView == null) {

            targetView = LayoutInflater.from(getActivity()).inflate(layoutId, containerLayout, true);
        }
        int childCount = containerLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {

            View childView = containerLayout.getChildAt(i);
            if (childView.equals(targetView)) {
                childView.setVisibility(View.VISIBLE);
                continue;
            }
            childView.setVisibility(View.INVISIBLE);
        }
        return targetView;
    }

    /**
     * 显示内容布局
     */
    public void showContent() {

        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.INVISIBLE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(View.INVISIBLE);
        }
        if (nothingLayout != null) {
            nothingLayout.setVisibility(View.INVISIBLE);
        }
        if (contentLayout != null) {
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示无内容布局
     */
    public void showNothing() {

        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.INVISIBLE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(View.INVISIBLE);
        }
        if (nothingLayout != null) {
            nothingLayout.setVisibility(View.VISIBLE);
        }
        if (contentLayout != null) {
            contentLayout.setVisibility(View.INVISIBLE);
        }
    }

    public View getLoadingLayout() {
        return loadingLayout;
    }

    public View getErrorLayout() {
        return errorLayout;
    }

    public View getContentLayout() {
        return contentLayout;
    }

    public View getNothingLayout() {
        return nothingLayout;
    }

    protected void showProgressBar(String message) {

        if (pd == null) {
            pd = new ProgressDialog(getActivity());
            pd.setMessage(message);
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
        }
        pd.show();
    }

    protected void dismissProgressBar() {

        if (pd == null) {
            return;
        }
        pd.dismiss();
        pd = null;
    }
}
