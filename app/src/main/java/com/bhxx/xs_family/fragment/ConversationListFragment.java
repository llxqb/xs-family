package com.bhxx.xs_family.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.internet.CallBack;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.ConversationActivity;
import com.bhxx.xs_family.activity.SysMessActivity;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.rongcloud.CustomerUserInfoProvider;
import com.bhxx.xs_family.utils.ChatUtils;
import com.bhxx.xs_family.utils.LogUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.IStringCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class ConversationListFragment extends BaseFragment {
    private LinearLayout conversation_sys_mess;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversitionlist_fragment, container, false);
        conversation_sys_mess = (LinearLayout) view.findViewById(R.id.conversation_sys_mess);
        io.rong.imkit.fragment.ConversationListFragment fragment = (io.rong.imkit.fragment.ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);

        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();

        fragment.setUri(uri);


        conversation_sys_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysMessActivity.start(getActivity());
            }
        });
        return view;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }

}
