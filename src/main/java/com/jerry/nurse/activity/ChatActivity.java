package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.jerry.nurse.R;
import com.jerry.nurse.adapter.ChatAdapterForRv;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.AudioRecordButton;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class ChatActivity extends BaseActivity implements EMMessageListener {

    @Bind(R.id.rv_content)
    RecyclerView mRecyclerView;

    @Bind(R.id.ib_left)
    ImageButton mTypeButton;

    @Bind(R.id.ib_add)
    ImageButton mAddButton;

    @Bind(R.id.acb_send)
    AppCompatButton mSendButton;

    @Bind(R.id.et_message)
    EditText mMessageEditText;

    @Bind(R.id.arb_record)
    AudioRecordButton mRecordButton;

    private boolean mIsRecordState;

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
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = mMessageEditText.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    mSendButton.setVisibility(View.VISIBLE);
                    mAddButton.setVisibility(View.GONE);
                } else {
                    mSendButton.setVisibility(View.GONE);
                    mAddButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRecordButton.setAudioFinishRecordListener(new AudioRecordButton.AudioFinishRecordListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds, filePath);

            }
        });

        final List<ChatMessage> mDatas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ChatMessage msg;
            if (i % 2 == 0) {

                msg = new ChatMessage(R.drawable.icon_avatar_default, "小黑", "有大码？", "", true);
            } else {

                msg = new ChatMessage(R.drawable.icon_avatar_default, "人马", "有大码？", "", false);
            }
            mDatas.add(msg);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ChatAdapterForRv adapter = new ChatAdapterForRv(this, mDatas);

        final LoadMoreWrapper mLoadMoreWrapper = new LoadMoreWrapper(adapter);
        mLoadMoreWrapper.setLoadMoreView(LayoutInflater.from(this).inflate(R.layout.default_loading, mRecyclerView, false));
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean coming = Math.random() > 0.5;
                        ChatMessage msg = null;
                        msg = new ChatMessage(coming ? R.drawable.icon_avatar_default : R.drawable.icon_avatar_default,
                                coming ? "人马" : "xiaohei", "where are you " + mDatas.size(),
                                null, coming);
                        mDatas.add(msg);
                        mLoadMoreWrapper.notifyDataSetChanged();

                    }
                }, 3000);
            }
        });




        mRecyclerView.setAdapter(mLoadMoreWrapper);

    }

    @OnClick(R.id.acb_send)
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

    @OnClick(R.id.ib_left)
    void onLeft(View view) {
        if (mIsRecordState) {
            mTypeButton.setBackgroundResource(R.drawable.icon_record);
            mMessageEditText.setVisibility(View.VISIBLE);
            mRecordButton.setVisibility(View.GONE);
        } else {
            mTypeButton.setBackgroundResource(R.drawable.icon_keybord);
            mRecordButton.setVisibility(View.VISIBLE);
            mMessageEditText.setVisibility(View.GONE);
        }
        mIsRecordState = !mIsRecordState;

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


    class Recorder {
        float time;
        String filePath;

        public Recorder(float time, String filePath) {
            this.time = time;
            this.filePath = filePath;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }


}
