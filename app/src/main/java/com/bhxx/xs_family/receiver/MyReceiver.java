package com.bhxx.xs_family.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bhxx.xs_family.activity.LoginActivity;
import com.bhxx.xs_family.activity.MainActivity;
import com.bhxx.xs_family.activity.SysMessActivity;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.SystemMessageBean;
import com.bhxx.xs_family.db.dao.SysMessDao;
import com.bhxx.xs_family.utils.LogUtils;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private String messType = "";
    private String content = "";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        //遍历数据
        printBundle(bundle);
        try {
            if (!TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                messType = json.getString("messType");
                LogUtils.i( "messType=" + messType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            // Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);

            if (messType.equals("")||messType.equals("0")) {
                if (!android.text.TextUtils.isEmpty(content)) {
                    //接受系统消息
                    SystemMessageBean messageBean = new SystemMessageBean();
                    messageBean.setContent(content);
                    messageBean.setTime(getCurrentDay());
                    if(!TextUtils.isEmpty(App.app.getData("uId"))){
                        SysMessDao.insertType(messageBean, App.app.getData("uId"));
                    }
                }
            } else if (messType.equals("1")) {
                //跳转提示登录
                Intent i = new Intent(context, LoginActivity.class);
                i.putExtra("from", "hintlogin");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }


        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            if (messType.equals("")||messType.equals("0")) {
                //跳转系统消息
                Intent i = new Intent(context, SysMessActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            } else if (messType.equals("1")) {
                //跳转提示登录
                Intent i = new Intent(context, LoginActivity.class);
                i.putExtra("from", "hintlogin");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        } else {
        }
    }

    /**
     * 获取当年时间
     *
     * @return
     */
    private String getCurrentDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    // 打印所有的 intent extra 数据
    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:1" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:2" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
                if (key.equals("cn.jpush.android.ALERT")) {
                    content = bundle.getString("cn.jpush.android.ALERT");
                }
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        if (LoginActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(LoginActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(LoginActivity.KEY_MESSAGE, message);
            if (!TextUtils.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (null != extraJson && extraJson.length() > 0) {
                        msgIntent.putExtra(LoginActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }
            }
            context.sendBroadcast(msgIntent);
        }
    }
}