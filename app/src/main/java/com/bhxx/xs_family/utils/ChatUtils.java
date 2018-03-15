package com.bhxx.xs_family.utils;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import android.text.TextUtils;

import com.bhxx.xs_family.application.App;

/**
 * 即时聊天工具类
 * @author Administrator
 *
 */
public class ChatUtils {

	/**
	 * 刷新用户信息（超过一天刷新）
	 * @param userInfo 需要刷新的用户
	 * @param imediately 是否立即刷新
	 */
	public static void refreshUserInfo(UserInfo userInfo,boolean imediately) {

		String lastUpdateTime = App.app.getData("avatorLastUpdateTime");
		if (!TextUtils.isEmpty(lastUpdateTime)) {

			long time = Long.valueOf(lastUpdateTime);

			if (System.currentTimeMillis() - time > 24 * 60 * 60 * 1000) {

				RongIM.getInstance().refreshUserInfoCache(userInfo);
				App.app.setData("avatorLastUpdateTime",
						System.currentTimeMillis() + "");
			}
		} else {

			App.app.setData("avatorLastUpdateTime", System.currentTimeMillis()
					+ "");
		}
	}
}
