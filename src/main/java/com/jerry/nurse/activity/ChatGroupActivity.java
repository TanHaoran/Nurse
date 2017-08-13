package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jerry.nurse.R;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.MessageManager;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.AudioRecordButton;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class ChatGroupActivity extends BaseActivity implements EMMessageListener {

    public static final String EXTRA_CONTACT_ID = "extra_contact_id";

    @Bind(R.id.rv_content)
    XRecyclerView mRecyclerView;

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

    @Bind(R.id.tv_name)
    TextView mNameTextView;

    private boolean mIsRecordState;

    private List<ChatMessage> mChatMessages;
    private ChatAdapter mAdapter;

    private LoginInfo mLoginInfo;
    private String mContactId;

    // 从数据库中读取的群资料
    private GroupInfo mInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            L.i("环信收到消息");
            String name = ActivityCollector.getTopActivity().getLocalClassName();
            if (!name.equals("activity.ChatActivity")) {
                return;
            }
            List<EMMessage> messages = (List<EMMessage>) msg.obj;
            for (final EMMessage emMessage : messages) {
                EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
                String message = messageBody.getMessage();
                L.i("消息内容：" + message);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setTo(mLoginInfo.getRegisterId());
                chatMessage.setSend(false);
                chatMessage.setContent(message);
                chatMessage.setTime(emMessage.getMsgTime());

                mChatMessages.add(chatMessage);
                mAdapter.notifyDataSetChanged();
                int itemCount = mAdapter.getItemCount();
                mRecyclerView.scrollToPosition(itemCount);

                chatMessage.save();
            }
        }
    };

    public static Intent getIntent(Context context, String contactId) {
        Intent intent = new Intent(context, ChatGroupActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contactId);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_chat_group;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        mContactId = getIntent().getStringExtra(EXTRA_CONTACT_ID);
        L.i("群Id是：" + mContactId);

        mInfo = DataSupport.where("HXGroupId=?",
                mContactId).findFirst(GroupInfo.class);

        mNameTextView.setText(mInfo.getHXNickName());

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

        try {
            List<ChatMessage> cms = DataSupport.findAll(ChatMessage.class);
            L.i("消息一共：" + cms.size());
            for (ChatMessage cm : cms) {
                L.i(cm.toString());
            }

            mChatMessages = DataSupport.where("(mFrom=? and mTo=?) or (mFrom=? and mTo=?)",
                    mLoginInfo.getRegisterId(), mContactId,
                    mContactId, mLoginInfo.getRegisterId()).find(ChatMessage.class);

        } catch (Exception e) {
            e.printStackTrace();
            mChatMessages = new ArrayList<>();
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ChatAdapter(this, mChatMessages);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount());

        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @OnClick(R.id.acb_send)
    public void onSend(View view) {
        String message = mMessageEditText.getText().toString();
        if (message == "") {
            T.showShort(this, R.string.message_can_not_be_empty);
            return;
        }

        mMessageEditText.setText("");

        // 发送环信消息
        easeMobSend(message);
    }


    /**
     * 发送环信消息
     *
     * @param message
     */
    private void easeMobSend(String message) {
        // 创建消息
        EMMessage emMessage = EMMessage.createTxtSendMessage(message, mContactId);
        emMessage.setChatType(EMMessage.ChatType.GroupChat);
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(emMessage);
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
        ChatMessage chatMessage = MessageManager.saveSendChatGroupMessageLocalData(emMessage, message);

        mChatMessages.add(chatMessage);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mAdapter.getItemCount());
        chatMessage.save();
    }

    @OnClick(R.id.ib_left)
    void onLeft(View view) {
        if (mIsRecordState) {
            mTypeButton.setBackgroundResource(R.drawable.icon_record);
            mMessageEditText.setVisibility(View.VISIBLE);
            mRecordButton.setVisibility(View.GONE);
        } else {
            mTypeButton.setBackgroundResource(R.drawable.icon_keyboard);
            mRecordButton.setVisibility(View.VISIBLE);
            mMessageEditText.setVisibility(View.GONE);
        }
        mIsRecordState = !mIsRecordState;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMessageReceived(final List<EMMessage> messages) {
        Message message = new Message();
        message.obj = messages;
        mHandler.sendMessage(message);
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

    @OnClick(R.id.iv_back)
    void onBack(View view) {
        finish();
    }

    @OnClick(R.id.iv_group_info)
    void onGroupInfo(View view) {
        Intent intent = ChatGroupInfoActivity.getIntent(this);
        startActivity(intent);
    }

    class ChatAdapter extends MultiItemTypeAdapter<ChatMessage> {
        public ChatAdapter(Context context, List<ChatMessage> datas) {
            super(context, datas);

            addItemViewDelegate(new SendItemDelagate());
            addItemViewDelegate(new ReceiveItemDelagate());
        }
    }

    class SendItemDelagate implements ItemViewDelegate<ChatMessage> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_chat_send;
        }

        @Override
        public boolean isForViewType(ChatMessage item, int position) {
            return item.isSend();
        }

        @Override
        public void convert(ViewHolder holder, ChatMessage chatMessage, int position) {
            holder.setText(R.id.tv_message, chatMessage.getContent());
            holder.setText(R.id.tv_time, DateUtil.parseDateToString(new Date(chatMessage.getTime())));
            ImageView imageView = holder.getView(R.id.iv_avatar);
            Glide.with(ChatGroupActivity.this).load(mLoginInfo.getAvatar()).into(imageView);
        }
    }

    class ReceiveItemDelagate implements ItemViewDelegate<ChatMessage> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_chat_receive;
        }

        @Override
        public boolean isForViewType(ChatMessage item, int position) {
            return !item.isSend();
        }

        @Override
        public void convert(ViewHolder holder, ChatMessage chatMessage, int position) {
            holder.setText(R.id.tv_message, chatMessage.getContent());
            holder.setText(R.id.tv_time, DateUtil.parseDateToString(new Date(chatMessage.getTime())));
            ImageView imageView = holder.getView(R.id.iv_avatar);
            //   Glide.with(ChatGroupActivity.this).load(mInfo.getAvatar()).into(imageView);

        }
    }

}
