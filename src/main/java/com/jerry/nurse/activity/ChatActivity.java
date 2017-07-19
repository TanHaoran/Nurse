package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.jerry.nurse.R;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.T;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class ChatActivity extends BaseActivity implements EMMessageListener {

    @Bind(R.id.tv_content)
    TextView mContentTextView;

    @Bind(R.id.et_message)
    EditText mMessageEditText;

    private static final String mOtherId = "thr";

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_chat;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick(R.id.btn_send)
    public void onSend(View view) {
        addMessageToContent("发送信息：\n");
        String message = mMessageEditText.getText().toString();
        if (message == "") {
            T.showShort(this, R.string.message_can_not_be_empty);
            return;
        }
        mMessageEditText.setText("");
        // 创建消息
        EMMessage emMessage = EMMessage.createTxtSendMessage(message, mOtherId);
        emMessage.setChatType(EMMessage.ChatType.Chat);
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(emMessage);
        // 添加消息
        addMessageToContent(message);
        emMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                L.i("发送成功！");
            }

            @Override
            public void onError(int code, String error) {
                L.i("发送失败，错误码：" + code + "，错误信息：" + error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 将信息添加在中央区域
     *
     * @param message
     */
    private void addMessageToContent(String message) {
        mContentTextView.setText(mContentTextView.getText().toString() + "\n" + message);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        L.i("环信收到消息");
        for (final EMMessage emMessage : messages) {
            EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
            String message = messageBody.getMessage();
            L.i("消息内容：" + message);
            addMessageToContent(message);
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }
}
