package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jerry.nurse.R;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.KeyBoardUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.MediaManager;
import com.jerry.nurse.util.MessageManager;
import com.jerry.nurse.util.ScreenUtil;
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


public class ChatActivity extends BaseActivity implements EMMessageListener {

    public static final String EXTRA_CONTACT_ID = "extra_contact_id";
    public static final String EXTRA_IS_GROUP = "extra_is_group";

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

    @Bind(R.id.ll_option)
    LinearLayout mOptionLayout;

    private boolean mIsRecordState;

    private List<ChatMessage> mChatMessages;
    private ChatAdapter mAdapter;

    private LoginInfo mLoginInfo;
    private String mContactId;
    private boolean mIsGroup;

    // 从数据库中读取的联系人资料
    private ContactInfo mContactInfo;
    // 从数据库中读取的群组的资料
    private GroupInfo mGroupInfo;

    private View mSendAnimView;

    private boolean mIsAdding;

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

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSend(false);
                if (emMessage.getType() == EMMessage.Type.TXT) {
                    EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
                    String message = messageBody.getMessage();
                    chatMessage.setContent(message);
                    chatMessage.setType(ChatMessage.TYPE_TXT);
                } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                    EMVoiceMessageBody messageBody = (EMVoiceMessageBody) emMessage.getBody();
                    int second = messageBody.getLength();
                    String path = messageBody.getLocalUrl();
                    chatMessage.setSecond(second);
                    chatMessage.setPath(path);
                    chatMessage.setType(ChatMessage.TYPE_VOICE);
                }

                // 单聊
                if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
                    chatMessage.setTo(mLoginInfo.getRegisterId());
                    chatMessage.setTime(emMessage.getMsgTime());
                    chatMessage.setFrom(emMessage.getFrom());
                    mChatMessages.add(chatMessage);
                    mAdapter.notifyDataSetChanged();

                    int itemCount = mAdapter.getItemCount();
                    mRecyclerView.scrollToPosition(itemCount);
                }
                // 群聊
                else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                    chatMessage.setTo(mContactId);
                    chatMessage.setTime(emMessage.getMsgTime());
                    chatMessage.setFrom(emMessage.getFrom());
                    mChatMessages.add(chatMessage);
                    mAdapter.notifyDataSetChanged();

                    int itemCount = mAdapter.getItemCount();
                    mRecyclerView.scrollToPosition(itemCount);
                }

                chatMessage.save();
            }
        }
    };

    public static Intent getIntent(Context context, String contactId, boolean isGroup) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contactId);
        intent.putExtra(EXTRA_IS_GROUP, isGroup);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_chat;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 初始化获取对方的Id
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        mContactId = getIntent().getStringExtra(EXTRA_CONTACT_ID);
        mIsGroup = getIntent().getBooleanExtra(EXTRA_IS_GROUP, false);

        // 单聊
        if (!mIsGroup) {
            L.i("联系人的Id是：" + mContactId);
            // 查询对方的信息
            mContactInfo = DataSupport.where("mRegisterId=?",
                    mContactId).findFirst(ContactInfo.class);
            if (!TextUtils.isEmpty(mContactInfo.getNickName())) {
                mNameTextView.setText(mContactInfo.getNickName());
            }

            // 读取数据库中存在的数据并显示
            try {
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
        }
        // 群聊
        else {
            L.i("群组的Id是：" + mContactId);
            mGroupInfo = DataSupport.where("HXGroupId=?", mContactId).findFirst(GroupInfo.class);
            if (!TextUtils.isEmpty(mGroupInfo.getHXNickName())) {
                mNameTextView.setText(mGroupInfo.getHXNickName());
            }
            // 读取数据库中存在的数据并显示
            try {
                DataSupport.findAll(ChatMessage.class);
                mChatMessages = DataSupport.where("(mFrom=? and mTo=?) or (mTo=?)",
                        mLoginInfo.getRegisterId(), mContactId,
                        mContactId).find(ChatMessage.class);

            } catch (Exception e) {
                e.printStackTrace();
                mChatMessages = new ArrayList<>();
            }

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mAdapter = new ChatAdapter(this, mChatMessages);

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.scrollToPosition(mAdapter.getItemCount());

        }

        // 设置输入框的监听事件
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

        // 设置输入框和添加框的下显示
        mMessageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionLayout.setVisibility(View.GONE);
            }
        });
        mMessageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mOptionLayout.setVisibility(View.GONE);
                }
            }
        });

        // 注册录音完成的回调方法
        mRecordButton.setAudioFinishRecordListener(new AudioRecordButton.AudioFinishRecordListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                easeMobSendVoiceMessage(seconds, filePath);

            }
        });

        // 添加消息监听
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
        easeMobSendTxtMessage(message);
    }


    /**
     * 发送环信文字消息
     *
     * @param message
     */
    private void easeMobSendTxtMessage(String message) {
        // 创建消息
        EMMessage emMessage = EMMessage.createTxtSendMessage(message, mContactId);
        if (!mIsGroup) {
            emMessage.setChatType(EMMessage.ChatType.Chat);
        } else {
            emMessage.setChatType(EMMessage.ChatType.GroupChat);
        }
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

        ChatMessage chatMessage = MessageManager.saveSendChatMessageLocalData(emMessage, message);

        mChatMessages.add(chatMessage);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mAdapter.getItemCount());
        chatMessage.save();
    }

    /**
     * 发送环信语音消息
     *
     * @param seconds
     * @param path
     */
    private void easeMobSendVoiceMessage(float seconds, String path) {
        // 创建消息
        EMMessage emMessage = EMMessage.createVoiceSendMessage(path, (int) seconds, mContactId);
        // 单聊
        if (!mIsGroup) {
            emMessage.setChatType(EMMessage.ChatType.Chat);
        }
        // 群聊
        else {
            emMessage.setChatType(EMMessage.ChatType.GroupChat);
        }
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
        ChatMessage chatMessage = MessageManager.saveSendChatMessageLocalData(emMessage, (int) seconds, path);

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

    @OnClick(R.id.iv_back)
    void onBack(View view) {
        finish();
    }

    @OnClick(R.id.iv_create_group)
    void onCreateGroup(View view) {
        Intent intent = CreateGroupActivity.getIntent(this);
        startActivity(intent);
    }


    class ChatAdapter extends MultiItemTypeAdapter<ChatMessage> {
        public ChatAdapter(Context context, List<ChatMessage> datas) {
            super(context, datas);

            SendItemDelagate sendItemDelagate = new SendItemDelagate();
            ReceiveItemDelagate receiveItemDelagate = new ReceiveItemDelagate();
            addItemViewDelegate(sendItemDelagate);
            addItemViewDelegate(receiveItemDelagate);
        }
    }

    class SendItemDelagate implements ItemViewDelegate<ChatMessage> {

        private int mMaxItemWidth;
        private int mMinItemWidth;

        public SendItemDelagate() {
            // 首先获取屏幕宽度
            int screenWidth = ScreenUtil.getScreenWidth(ChatActivity.this);
            mMaxItemWidth = (int) (screenWidth * 0.7f);
            mMinItemWidth = (int) (screenWidth * 0.15f);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_chat_send;
        }

        @Override
        public boolean isForViewType(ChatMessage item, int position) {
            return item.isSend();
        }

        @Override
        public void convert(final ViewHolder holder, final ChatMessage chatMessage, int position) {
            // 发消息群聊和单聊没有区别
            ImageView imageView = holder.getView(R.id.iv_avatar);
            Glide.with(ChatActivity.this).load(mLoginInfo.getAvatar()).into(imageView);
            holder.setText(R.id.tv_time, DateUtil.parseDateToString(new Date(chatMessage.getTime())));
            switch (chatMessage.getType()) {
                case ChatMessage.TYPE_TXT:
                    holder.setVisible(R.id.tv_message, true);
                    holder.setText(R.id.tv_message, chatMessage.getContent());
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    break;
                case ChatMessage.TYPE_IMAGE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    break;
                case ChatMessage.TYPE_VOICE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, true);
                    holder.setVisible(R.id.tv_record_length, true);
                    holder.setText(R.id.tv_record_length, (int) chatMessage.getSecond() + "''");
                    ViewGroup.LayoutParams lp = holder.getView(R.id.fl_anim).getLayoutParams();
                    lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * chatMessage.getSecond()));
                    holder.setOnClickListener(R.id.fl_anim, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mSendAnimView != null) {
                                mSendAnimView.setBackgroundResource(R.drawable.talk_yy_me_3);
                                mSendAnimView = null;
                            }
                            // 播放动画
                            mSendAnimView = holder.getView(R.id.v_anim);
                            mSendAnimView.setBackgroundResource(R.drawable.voice_play);
                            AnimationDrawable anim = (AnimationDrawable) mSendAnimView.getBackground();
                            anim.start();
                            // 播放音频
                            MediaManager.playSound(chatMessage.getPath(), new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mSendAnimView.setBackgroundResource(R.drawable.talk_yy_me_3);
                                }
                            });
                        }
                    });
                    break;
            }
        }
    }

    class ReceiveItemDelagate implements ItemViewDelegate<ChatMessage> {

        private int mMaxItemWidth;
        private int mMinItemWidth;

        public ReceiveItemDelagate() {
            // 首先获取屏幕宽度
            int screenWidth = ScreenUtil.getScreenWidth(ChatActivity.this);
            mMaxItemWidth = (int) (screenWidth * 0.7f);
            mMinItemWidth = (int) (screenWidth * 0.15f);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_chat_receive;
        }

        @Override
        public boolean isForViewType(ChatMessage item, int position) {
            return !item.isSend();
        }

        @Override
        public void convert(final ViewHolder holder, final ChatMessage chatMessage, int position) {
            ImageView imageView = holder.getView(R.id.iv_avatar);
            // 单聊
            if (!mIsGroup) {
                Glide.with(ChatActivity.this).load(mContactInfo.getAvatar())
                        .placeholder(R.drawable.icon_avatar_default).into(imageView);
            }
            // 群聊
            else {
                // 查询对方的信息
                List<ContactInfo> cis = DataSupport.findAll(ContactInfo.class);
                ContactInfo ci = DataSupport.where("mRegisterId=?",
                        chatMessage.getFrom()).findFirst(ContactInfo.class);
                Glide.with(ChatActivity.this).load(ci.getAvatar())
                        .placeholder(R.drawable.icon_avatar_default).into(imageView);
            }
            holder.setText(R.id.tv_time, DateUtil.parseDateToString(new Date(chatMessage.getTime())));
            switch (chatMessage.getType()) {
                case ChatMessage.TYPE_TXT:
                    holder.setVisible(R.id.tv_message, true);
                    holder.setText(R.id.tv_message, chatMessage.getContent());
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    break;
                case ChatMessage.TYPE_IMAGE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    break;
                case ChatMessage.TYPE_VOICE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, true);
                    holder.setVisible(R.id.tv_record_length, true);
                    holder.setText(R.id.tv_record_length, (int) chatMessage.getSecond() + "''");
                    ViewGroup.LayoutParams lp = holder.getView(R.id.fl_anim).getLayoutParams();
                    lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * chatMessage.getSecond()));
                    holder.setOnClickListener(R.id.fl_anim, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mSendAnimView != null) {
                                mSendAnimView.setBackgroundResource(R.drawable.talk_yy_me_3);
                                mSendAnimView = null;
                            }
                            // 播放动画
                            mSendAnimView = holder.getView(R.id.v_anim);
                            mSendAnimView.setBackgroundResource(R.drawable.voice_play);
                            AnimationDrawable anim = (AnimationDrawable) mSendAnimView.getBackground();
                            anim.start();
                            // 播放音频
                            MediaManager.playSound(chatMessage.getPath(), new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mSendAnimView.setBackgroundResource(R.drawable.talk_yy_me_3);
                                }
                            });
                        }
                    });
                    break;
            }

        }
    }

    @OnClick(R.id.ib_add)
    void onAdd(View view) {
        if (mIsAdding) {
            mOptionLayout.setVisibility(View.GONE);
        } else {
            mOptionLayout.setVisibility(View.VISIBLE);
            KeyBoardUtil.closeKeybord(mMessageEditText, this);
        }
        mIsAdding = !mIsAdding;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }
}
