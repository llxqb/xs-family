package com.bhxx.xs_family.rongcloud;

import android.net.Uri;
import android.text.TextUtils;

import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import java.util.LinkedHashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;

public class CustomerUserInfoProvider implements RongIM.UserInfoProvider {
    private UserInfo uInfo;

    @Override
    public UserInfo getUserInfo(String userId) {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("uId", userId);
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
                        String uId = model.getuId() + "";
                        String uName = model.getuName();
                        String uPic = model.getuHeadPic();
                        if (!TextUtils.isEmpty(uPic)) {
                            uInfo = new UserInfo(uId, uName, Uri.parse(GlobalValues.IMG_IP + uPic));
                        } else {
                            uInfo = new UserInfo(uId, uName, null);
                        }
                        //刷新用户信息  可以刷新会话列表
                        RongIM.getInstance().refreshUserInfoCache(uInfo);
                    }
                }
            }
        });

        return uInfo;
    }
}
