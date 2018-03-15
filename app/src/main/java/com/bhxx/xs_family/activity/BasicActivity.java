package com.bhxx.xs_family.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectInit;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.views.CustomProgressDialog;

public abstract class BasicActivity extends AppCompatActivity {
    protected Toast toast;
    protected NesConnectionChangeReceiver changeReceiver;
    public CustomProgressDialog progressDialog = null;

    @InjectInit
    protected abstract void init();

    protected abstract void click(View view);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver();
    }

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

    /**
     * 方法说明 对话框显示
     *
     * @param
     * @return
     * @author
     */
    protected void showProgressDialog(Context context) {
        if (null == progressDialog) {
            progressDialog = CustomProgressDialog.createDialog(context);
        }
        progressDialog.show();
    }

    protected void showProgressDialog(Context context, String messager) {
        if (null == progressDialog) {
            progressDialog = CustomProgressDialog.createDialog(context);
        }
        progressDialog.setMessage(messager);
        progressDialog.show();
    }

    /**
     * 方法说明 对话框关闭
     *
     * @param
     * @return
     * @author
     */
    protected void dismissProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 设置可见
     *
     * @param view
     */
    protected void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 设置不可见
     *
     * @param view
     */
    protected void setGone(View view) {
        view.setVisibility(View.GONE);
    }

    /**
     * 设置暂不可见
     *
     * @param view
     */
    protected void setInVisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }


    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        changeReceiver = new NesConnectionChangeReceiver();
        this.registerReceiver(changeReceiver, filter);
    }

    private void unRegisterReceiver() {
        this.unregisterReceiver(changeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    protected class NesConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                // 改变背景或者 处理网络的全局变量'
                showToast("网络不可用");
            }
        }
    }


}
