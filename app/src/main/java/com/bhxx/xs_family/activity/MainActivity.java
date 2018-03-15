package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.fragment.ConversationListFragment;
import com.bhxx.xs_family.fragment.HomePage;
import com.bhxx.xs_family.fragment.KindergartenMineFragment;
import com.bhxx.xs_family.fragment.MonitorFragment;
import com.bhxx.xs_family.fragment.ParentMinePage;
import com.bhxx.xs_family.fragment.TeacherMineFragment;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.StatusBar;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import java.util.LinkedHashMap;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.FileInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.LocationInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;

/**
 * APP:希盛国际幼儿园
 * createAuthor:祝钊、刘立（QQ:2567575495）
 */
@InjectLayer(R.layout.activity_main)
public class MainActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private Fragment home;
    private Fragment msg;
    private Fragment video;
    private Fragment user;
    private long exitTime = 0;
    private boolean unreadnews = false;//记录是否点击消息按钮
    private Handler handler = new Handler();

    private class Views {
        FrameLayout main_frame;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageButton main_home_bt, main_msg_bt, main_video_bt, main_mine_bt;
    }

    @Override
    protected void init() {
        ActivityCollector.finishAll();
        ActivityCollector.addActivity(MainActivity.this);
        //增加沉浸式状态栏    加上会出现底部有按钮的手机虚拟键会覆盖app功能键(如华为手机)
//        new StatusBar(this).initState();
        showHome();
        //定时器去查看是否有未读消息
        requestUnreadNews();
        if (!TextUtils.isEmpty(App.app.getData("uId"))) {
            getUserInfo();
        }
        if (App.app.getData("uRole").equals("0")) {
            setGone(v.main_msg_bt);
            setGone(v.main_video_bt);
        } else if (App.app.getData("uRole").equals("1")) {
            setGone(v.main_video_bt);
        }
        if (!App.app.getData("uRole").equals("0")) {
            if (!TextUtils.isEmpty(App.app.getData("rToken"))) {
                connect(App.app.getData("rToken"));
                //设置会话界面的功能
                InputProvider.ExtendProvider[] ep = {
                        new CameraInputProvider(RongContext.getInstance()),
                        new ImageInputProvider(RongContext.getInstance()),
//                        new LocationInputProvider(RongContext.getInstance()),//地理位置
//                        new FileInputProvider(RongContext.getInstance())//传输文件
                };
                //我需要让他在什么会话类型中的 bar 展示
                RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, ep);
            }
        }
    }

    private void requestUnreadNews() {
        handler.postDelayed(runnable, 1000);
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("uId", App.app.getData("uId"));
        String token = TokenUtils.getInstance().configParams(GlobalValues.USER_INFO + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.USER_INFO, "USER_INFO", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<UserModel> user = JsonUtils.getBean(response, CommonBean.class, UserModel.class);
                    if (user != null && user.isSuccess()) {
                        UserModel model = user.getRows();
                        if (model != null) {
                            App.app.saveObjData("user", model);
                        }
                    }
                }
            }
        });
    }

    /**
     * 每次点击清除状态
     */
    private void clearState() {
        unreadnews = false;
        v.main_home_bt.setImageResource(R.mipmap.tab_home);
//        v.main_msg_bt.setImageResource(R.mipmap.tab_news);
        v.main_video_bt.setImageResource(R.mipmap.tab_monitored);
        v.main_mine_bt.setImageResource(R.mipmap.tab_user);
    }

    @Override
    protected void click(View view) {
        clearState();
        switch (view.getId()) {
            case R.id.main_home_bt:
                v.main_home_bt.setImageResource(R.mipmap.tab_home_pre);
                showHome();
                break;
            case R.id.main_msg_bt:
                unreadnews = true;
//                v.main_msg_bt.setImageResource(R.mipmap.tab_news_pre);
                showMainMsg();
                break;
            case R.id.main_video_bt:
                v.main_video_bt.setImageResource(R.mipmap.tab_monitored_pre);
                showMainVideo();
                break;
            case R.id.main_mine_bt:
                v.main_mine_bt.setImageResource(R.mipmap.tab_user_pre);
                showMainUser();
                break;
        }
    }

    /**
     * 家长首页
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 展示首页
     */
    private void showHome() {
        home = getSupportFragmentManager().findFragmentByTag("home");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (home == null) {
            home = new HomePage();
            transaction.add(R.id.main_frame, home, "home");
        } else {
            transaction.show(home);
        }
        if (msg != null) {
            transaction.hide(msg);
        }
        if (video != null) {
            transaction.hide(video);
        }
        if (user != null) {
            transaction.hide(user);
        }
        transaction.commit();
    }

    /**
     * 展示消息
     */
    private void showMainMsg() {
        msg = getSupportFragmentManager().findFragmentByTag("msg");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (msg == null) {
            msg = new ConversationListFragment();
            transaction.add(R.id.main_frame, msg, "msg");
        } else {
            transaction.show(msg);
        }
        if (home != null) {
            transaction.hide(home);
        }
        if (video != null) {
            transaction.hide(video);
        }
        if (user != null) {
            transaction.hide(user);
        }
        transaction.commit();
    }

    /**
     * 监控
     */
    private void showMainVideo() {
        video = getSupportFragmentManager().findFragmentByTag("video");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (video == null) {
            video = new MonitorFragment();
            transaction.add(R.id.main_frame, video, "video");
        } else {
            transaction.show(video);
        }
        if (home != null) {
            transaction.hide(home);
        }
        if (msg != null) {
            transaction.hide(msg);
        }
        if (user != null) {
            transaction.hide(user);
        }
        transaction.commit();
    }

    /**
     * 展示个人主页
     */
    private void showMainUser() {
        user = getSupportFragmentManager().findFragmentByTag("user");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (user == null) {
            if (App.app.getData("uRole").equals("0")) {
                user = new KindergartenMineFragment();
            } else if (App.app.getData("uRole").equals("1")) {
                user = new TeacherMineFragment();
            } else {
                user = new ParentMinePage();
            }
            transaction.add(R.id.main_frame, user, "user");
        } else {
            transaction.show(user);
        }
        if (home != null) {
            transaction.hide(home);
        }
        if (msg != null) {
            transaction.hide(msg);
        }
        if (video != null) {
            transaction.hide(video);
        }
        transaction.commit();
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {
        /**
         * IMKit SDK调用第二步,建立与服务器的连接
         */
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            /**
             * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
             */
            @Override
            public void onTokenIncorrect() {
                LogUtils.i("--onTokenIncorrect");
            }

            /**
             * 连接融云成功
             * @param userid 当前 token
             */
            @Override
            public void onSuccess(String userid) {
                LogUtils.i("--onSuccess" + userid);
            }

            /**
             * 连接融云失败
             * @param errorCode 错误码，可到官网 查看错误码对应的注释
             */
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                LogUtils.i("--onError" + errorCode);
            }
        });
    }


    /**
     * 捕捉返回事件按钮
     * <p>
     * 因为此 Activity 继承 TabActivity 用 onKeyDown 无响应，所以改用 dispatchKeyEvent 一般的
     * Activity 用 onKeyDown 就可以了
     */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        //停止定时器
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            int unReadNewsCount = RongIM.getInstance().getTotalUnreadCount();
            if (unReadNewsCount != 0) {
                if (unreadnews) {
                    v.main_msg_bt.setImageResource(R.mipmap.tab_news_pre_unreadnews);
                } else {
                    v.main_msg_bt.setImageResource(R.mipmap.tab_news_unreadnews);
                }
            } else {
                if (unreadnews) {
                    v.main_msg_bt.setImageResource(R.mipmap.tab_news_pre);
                } else {
                    v.main_msg_bt.setImageResource(R.mipmap.tab_news);
                }
            }
            handler.postDelayed(this, 1000);
        }
    };
}
