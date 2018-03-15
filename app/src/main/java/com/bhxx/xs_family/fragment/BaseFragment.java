package com.bhxx.xs_family.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectInit;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.views.CustomProgressDialog;

public abstract class BaseFragment extends Fragment {
    private Toast toast;
    public CustomProgressDialog progressDialog = null;

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

    @InjectInit
    protected abstract void init();

    protected abstract void click(View view);
}
