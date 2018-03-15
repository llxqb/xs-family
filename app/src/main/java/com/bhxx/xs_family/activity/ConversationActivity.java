package com.bhxx.xs_family.activity;
/**
 * 聊天界面
 */
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.LogUtils;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class ConversationActivity extends BasicActivity implements View.OnClickListener {
    private String mTargetId;//对话对象id
    private ImageView conversation_back;
    private TextView conversation_name;
//    private final static String POWER = Manifest.permission.CAMERA;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        conversation_back = (ImageView) findViewById(R.id.conversation_back);
        conversation_name = (TextView) findViewById(R.id.conversation_name);
        conversation_back.setOnClickListener(this);
        ActivityCollector.addActivity(ConversationActivity.this);
        if (getIntent() != null) {
            initIntent(getIntent());
        }
    }

    private void initIntent(Intent intent) {
        mTargetId = intent.getData().getQueryParameter("targetId");
        conversation_name.setText(intent.getData().getQueryParameter("title"));
        mConversationType = Conversation.ConversationType.valueOf("PRIVATE");
        enterFragment(mConversationType,mTargetId);
    }
    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     *            会话类型
     * @param mTargetId
     *            目标 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager()
                .findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                .appendPath(mConversationType.getName().toLowerCase()).appendQueryParameter("targetId", mTargetId)
                .build();

        fragment.setUri(uri);
    }

    @Override
    protected void init() {
        checkPermission();
    }

    @Override
    protected void click(View view) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.conversation_back:
                finish();
                break;
        }
    }

    protected void checkPermission() {
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkPermissionResult = ConversationActivity.this.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (checkPermissionResult != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                return;
            } else {
                //获取到权限
            }
        } else {
            //获取到权限
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //获取到权限
            } else {
                //没有获取到权限
            }
        }
    }



}
