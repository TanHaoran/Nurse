package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.jerry.nurse.R;
import com.jerry.nurse.listener.OnPhotographFinishListener;
import com.jerry.nurse.listener.OnSelectFromAlbumListener;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.AlarmManager;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.KeyBoardUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LocalContactCache;
import com.jerry.nurse.util.LocalGroupCache;
import com.jerry.nurse.util.MediaManager;
import com.jerry.nurse.util.MessageManager;
import com.jerry.nurse.util.PictureUtil;
import com.jerry.nurse.util.ScreenUtil;
import com.jerry.nurse.util.StreamUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.AudioRecordButton;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


public class ChatActivity extends BaseActivity implements EMMessageListener {

    /**
     * 默认每次刷新显示的条目数
     */
    private static final int DEFAULT_PAGE_MESSAGE_COUNT = 10;

    private static final int MESSAGE_RECEIVED = 0x101;
    private static final int MESSAGE_READ = 0x102;
    /**
     * 目前加载的页数
     */
    private int mPageIndex = 0;

    public static final String EXTRA_CONTACT_ID = "extra_contact_id";
    public static final String EXTRA_IS_GROUP = "extra_is_group";

    private static final int REQUEST_GROUP_CHAT = 0x101;

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

    @Bind(R.id.tv_name)
    TextView mNameTextView;

    @Bind(R.id.ll_option)
    LinearLayout mOptionLayout;

    @Bind(R.id.ll_photo)
    LinearLayout mPhotoLayout;

    @Bind(R.id.ll_photograph)
    LinearLayout mPhotographLayout;

    private boolean mIsRecordState;

    private List<ChatMessage> mChatMessages;
    private ChatAdapter mAdapter;

    private LoginInfo mLoginInfo;
    private String mContactId;
    private boolean mGroup;

    // 从数据库中读取的联系人资料
    private ContactInfo mContactInfo;
    // 从数据库中读取的群组的资料
    private GroupInfo mGroupInfo;

    private View mSendAnimView;

    private boolean mIsAdding;

    private com.jerry.nurse.model.Message mHomePageMessage;


    // 用来记录跳转页面时候的位置
//    private int mCurrentPosition;


    /**
     * @param context
     * @param contactId 聊天对方的Id
     * @param isGroup   是否是群聊
     * @return
     */
    public static Intent getIntent(Context context, String contactId, boolean isGroup) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contactId);
        intent.putExtra(EXTRA_IS_GROUP, isGroup);
        return intent;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVED:
                    String name = ActivityCollector.getTopActivity().getLocalClassName();
                    if (!name.equals("activity.ChatActivity")) {
                        return;
                    }

                    // 播放系统提示音
                    AlarmManager.playAlarm(getApplicationContext());

                    // 一条一条解析消息
                    List<EMMessage> messages = (List<EMMessage>) msg.obj;
                    for (final EMMessage emMessage : messages) {
                        L.i("收到一条消息:" + emMessage.getMsgId());
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setRegisterId(mLoginInfo.getRegisterId());
                        if (emMessage.getType() == EMMessage.Type.TXT) {
                            EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
                            chatMessage.setContent(messageBody.getMessage());
                            chatMessage.setType(ChatMessage.TYPE_TXT);
                        } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                            EMVoiceMessageBody messageBody = (EMVoiceMessageBody) emMessage.getBody();
                            chatMessage.setSecond(messageBody.getLength());
                            chatMessage.setPath(messageBody.getLocalUrl());
                            chatMessage.setType(ChatMessage.TYPE_VOICE);
                        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                            EMImageMessageBody messageBody = (EMImageMessageBody) emMessage.getBody();
                            chatMessage.setLocalUrl(messageBody.getLocalUrl());
                            chatMessage.setRemoteUrl(messageBody.getRemoteUrl());
                            chatMessage.setType(ChatMessage.TYPE_IMAGE);
                            // TODO 图片文件传输如果立刻传输有时会无法床见文件，这里暂时延迟处理
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        // 判断是否是当前聊天界面从而更新
                        if (!mGroup && emMessage.getChatType() == EMMessage.ChatType.Chat) {
                            if (mContactId.equals(emMessage.getFrom())) {
                                // 设置消息已读
                                makeMessagesRead();
                                updateChatMessageList(chatMessage);
                            }
                        } else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                            if (mContactId.equals(emMessage.getTo())) {
                                // 设置消息已读
                                makeMessagesRead();
                                updateChatMessageList(chatMessage);
                            }
                        }

                        // 单聊
                        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
                            chatMessage.setGroup(false);
                            chatMessage.setTo(mLoginInfo.getRegisterId());
                            // 构建首页消息
                            mHomePageMessage = null;
                            try {
                                mHomePageMessage = DataSupport.where("mType=? and mRegisterId=? and mContactId=?", "1",
                                        EMClient.getInstance()
                                                .getCurrentUser(),
                                        emMessage.getFrom())
                                        .findFirst(com.jerry.nurse.model.Message.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            new LocalContactCache() {
                                @Override
                                protected void onLoadContactInfoSuccess(ContactInfo info) {
                                    if (mHomePageMessage == null) {
                                        mHomePageMessage = new com.jerry.nurse.model.Message();
                                    }
                                    mHomePageMessage.setType(com.jerry.nurse.model.Message.TYPE_CHAT);
                                    mHomePageMessage.setImageUrl(info.getAvatar());
                                    mHomePageMessage.setTitle(info.getNickName());
                                    mHomePageMessage.setTime(new Date().getTime());
                                    mHomePageMessage.setRegisterId(EMClient.getInstance().getCurrentUser());
                                    mHomePageMessage.setContactId(emMessage.getFrom());
                                    mHomePageMessage.save();
                                }
                            }.getContactInfo(EMClient.getInstance().getCurrentUser(),
                                    emMessage.getFrom());
                        }
                        // 群聊
                        else if (emMessage.getChatType() == EMMessage
                                .ChatType.GroupChat) {
                            chatMessage.setGroup(true);
                            chatMessage.setTo(emMessage.getTo());
                            // 因为这里要异步获取发消息人的信息，所以使用了全局变量来记录
                            mHomePageMessage = null;
                            try {
                                mHomePageMessage = DataSupport.where("mType=? and mRegisterId=? and mContactId=?", "2",
                                        EMClient.getInstance().getCurrentUser(), emMessage.getTo())
                                        .findFirst(com.jerry.nurse.model.Message.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            new LocalGroupCache() {
                                @Override
                                protected void onLoadGroupInfoSuccess
                                        (final GroupInfo gInfo) {
                                    new LocalContactCache() {
                                        @Override
                                        protected void onLoadContactInfoSuccess(ContactInfo info) {
                                            if (mHomePageMessage == null) {
                                                mHomePageMessage = new com.jerry.nurse.model.Message();
                                            }
                                            mHomePageMessage.setType(com.jerry.nurse.model.Message.TYPE_CHAT_GROUP);
                                            mHomePageMessage.setImageResource(R.drawable.icon_qlt);
                                            mHomePageMessage.setTitle(gInfo.getHXNickName());
                                            mHomePageMessage.setTime(new Date().getTime());
                                            mHomePageMessage.setRegisterId(EMClient.getInstance().getCurrentUser());
                                            mHomePageMessage.setContactId(emMessage.getTo());
                                            mHomePageMessage.save();
                                        }
                                    }.getContactInfo(EMClient.getInstance().getCurrentUser(),
                                            emMessage.getFrom());

                                }
                            }.getGroupInfo(EMClient.getInstance().getCurrentUser(),
                                    emMessage.getTo());
                        }
                        chatMessage.setTime(emMessage.getMsgTime());
                        chatMessage.setFrom(emMessage.getFrom());
                        chatMessage.save();
                    }
                    break;
                case MESSAGE_READ:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    // 更新聊天界面
    private void updateChatMessageList(ChatMessage chatMessage) {
        // 保存聊天消息并更新界面
        mChatMessages.add(chatMessage);
        mAdapter.notifyDataSetChanged();

        // TODO 滚动问题
        int itemCount = mAdapter.getItemCount();
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(itemCount);
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_chat;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 获取传递过来的值
        mContactId = getIntent().getStringExtra(EXTRA_CONTACT_ID);
        mGroup = getIntent().getBooleanExtra(EXTRA_IS_GROUP, false);
        // 获取我的信息
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);

        // 设置消息已读
        makeMessagesRead();

        // 单聊
        if (!mGroup) {
            L.i("联系人的Id是：" + mContactId);
            // 查询联系人的信息
            mContactInfo = DataSupport.where("mRegisterId=?",
                    mContactId).findFirst(ContactInfo.class);
            // 显示昵称
            if (mContactInfo != null) {
                mNameTextView.setText(mContactInfo.getDisplayName());
            }

            // 读取数据库中存在的数据并显示
            try {
                mChatMessages = DataSupport.where("mRegisterId=? and " +
                                "mIsGroup=0 " +
                                "and((mFrom=? and mTo=?) or (mFrom=? and mTo=?))",
                        mLoginInfo.getRegisterId(), mLoginInfo.getRegisterId(), mContactId,
                        mContactId, mLoginInfo.getRegisterId()).find(ChatMessage.class);

            } catch (Exception e) {
                e.printStackTrace();
                mChatMessages = new ArrayList<>();
            }

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new ChatAdapter(this, mChatMessages);
            mRecyclerView.setAdapter(mAdapter);
            // TODO 滑动问题
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
                mChatMessages = DataSupport.where("mRegisterId=? and " +
                                "mIsGroup=1 " +
                                "and ((mFrom=? and mTo=?) or (mTo=?))",
                        mLoginInfo.getRegisterId(), mLoginInfo.getRegisterId(), mContactId,
                        mContactId).find(ChatMessage.class);

            } catch (Exception e) {
                e.printStackTrace();
                mChatMessages = new ArrayList<>();
            }

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new ChatAdapter(this, mChatMessages);
            mRecyclerView.setAdapter(mAdapter);
            // TODO 滑动问题
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        }

//        mRecyclerView.scrollToPosition(mCurrentPosition);

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

        // 点击输入框，隐藏操作面板
        mMessageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionLayout.setVisibility(View.GONE);
            }
        });
        // 聚焦输入框，隐藏操作面板
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
                // 创建语音消息
                EMMessage emMessage = EMMessage.createVoiceSendMessage(filePath,
                        (int) seconds, mContactId);
                // 单聊
                if (!mGroup) {
                    emMessage.setChatType(EMMessage.ChatType.Chat);
                }
                // 群聊
                else {
                    emMessage.setChatType(EMMessage.ChatType.GroupChat);
                }
                // 发送消息
                easeMobSendMessage(emMessage);
            }
        });

        // 注册拍照和发图片两个事件监听
        setSelectFromAlbumListener(mPhotoLayout, new OnSelectFromAlbumListener() {
            @Override
            public void onPhotoFinished(Bitmap bitmap, File file) {
                // 创建图片消息并发送
                createImageMessageAndSend(file);

            }
        });
        setPhotographListener(mPhotographLayout, new OnPhotographFinishListener() {
            @Override
            public void onPhotographFinished(Bitmap bitmap, File file) {
                // 创建图片消息并发送
                createImageMessageAndSend(file);
            }
        });

        // 添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(this);
        MediaManager.resume();
    }

    /**
     * 将所有消息设置为已读
     */
    private void makeMessagesRead() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mContactId);

        // 将所有未读消息设置为已读
        if (conversation != null) {
            for (EMMessage emMessage : conversation.getAllMessages()) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(emMessage.getFrom(), emMessage.getMsgId());
                    L.i("设置已读消息:" + emMessage.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        } else {
            L.i("谈话为空");
        }
    }

    /**
     * 创建图片消息并发送
     *
     * @param file
     */
    private void createImageMessageAndSend(File file) {
        // 创建消息
        EMMessage emMessage = EMMessage.createImageSendMessage(
                file.getAbsolutePath(), true, mContactId);
        // 单聊
        if (!mGroup) {
            emMessage.setChatType(EMMessage.ChatType.Chat);
        }
        // 群聊
        else {
            emMessage.setChatType(EMMessage.ChatType.GroupChat);
        }
        easeMobSendMessage(emMessage);
    }

    /**
     * 发送文字信息
     *
     * @param view
     */
    @OnClick(R.id.acb_send)
    public void onSend(View view) {
        String message = mMessageEditText.getText().toString();
        if (message == "") {
            T.showShort(this, R.string.message_can_not_be_empty);
            return;
        }
        // 发送完信息清空输入框
        mMessageEditText.setText("");
        // 创建文字消息
        EMMessage emMessage = EMMessage.createTxtSendMessage(message, mContactId);
        if (!mGroup) {
            emMessage.setChatType(EMMessage.ChatType.Chat);
        } else {
            emMessage.setChatType(EMMessage.ChatType.GroupChat);
        }
        // 发送消息
        easeMobSendMessage(emMessage);
    }

    /**
     * 发送环信消息
     *
     * @param emMessage
     */
    private void easeMobSendMessage(final EMMessage emMessage) {
        // 给消息添加本人的头像和昵称
        emMessage.setAttribute("avatar", mLoginInfo.getAvatar());
        emMessage.setAttribute("nick", mLoginInfo.getNickName());
        // 调用环信SDK发送信息
        EMClient.getInstance().chatManager().sendMessage(emMessage);
        // 监听消息发送状态
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

        ChatMessage chatMessage = MessageManager.saveChatMessageData(emMessage, true);

        // 添加进显示列表并滑动到底
        mChatMessages.add(chatMessage);
        mAdapter.notifyDataSetChanged();
        // TODO 滑动问题
        mRecyclerView.smoothScrollToPosition(mChatMessages.size());
    }

    @OnClick(R.id.ib_left)
    void onLeft(View view) {
        // 录音状态
        if (mIsRecordState) {
            mTypeButton.setBackgroundResource(R.drawable.icon_record);
            mMessageEditText.setVisibility(View.VISIBLE);
            mRecordButton.setVisibility(View.GONE);
        }
        // 文字状态
        else {
            mTypeButton.setBackgroundResource(R.drawable.icon_keyboard);
            mMessageEditText.setVisibility(View.GONE);
            mRecordButton.setVisibility(View.VISIBLE);
        }
        mOptionLayout.setVisibility(View.GONE);
        mIsRecordState = !mIsRecordState;
    }

    @Override
    protected void onStop() {
        super.onStop();
        LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//        mCurrentPosition = lm.findFirstVisibleItemPosition();
    }

    /**
     * 收到消息的回调
     *
     * @param messages
     */
    @Override
    public void onMessageReceived(final List<EMMessage> messages) {
        L.i("收到一条消息");
        Message message = new Message();
        message.obj = messages;
        message.what = MESSAGE_RECEIVED;
        mHandler.sendMessage(message);
    }

    /**
     * 收到透传消息
     *
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        L.i("收到一条透传消息");
    }

    /**
     * 收到已读回执
     *
     * @param messages
     */
    @Override
    public void onMessageRead(List<EMMessage> messages) {
        for (ChatMessage cm : mChatMessages) {
            if (!cm.isRead()) {
                cm.setRead(true);
                L.i("设置一条已读");
                cm.save();
            }
        }
        mHandler.sendEmptyMessage(MESSAGE_READ);
    }

    /**
     * 收到已送达回执
     *
     * @param messages
     */
    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        L.i("ChatActivity中收到已送达回执");
    }

    /**
     * 消息状态改变
     *
     * @param message
     * @param change
     */
    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        L.i("消息状态改变");
    }

    @OnClick(R.id.iv_back)
    void onBack(View view) {
        finish();
    }

    @OnClick(R.id.iv_create_group)
    void onCreateGroup(View view) {
        if (!mGroup) {
            Intent intent = CreateGroupActivity.getIntent(this, null, null);
            startActivity(intent);
        } else {
            Intent intent = GroupInfoActivity.getIntent(this, mContactId);
            startActivityForResult(intent, REQUEST_GROUP_CHAT);
        }
    }

    class ChatAdapter extends MultiItemTypeAdapter<ChatMessage> {
        public ChatAdapter(Context context, List<ChatMessage> datas) {
            super(context, datas);
            // 创建两种不同的布局
            SendItemDelagate sendItemDelagate = new SendItemDelagate();
            ReceiveItemDelagate receiveItemDelagate = new ReceiveItemDelagate();
            addItemViewDelegate(sendItemDelagate);
            addItemViewDelegate(receiveItemDelagate);
        }
    }

    /**
     * 发送消息布局适配器
     */
    class SendItemDelagate implements ItemViewDelegate<ChatMessage> {

        // 定义语音消息显示最长宽度和最窄宽度
        private int mMaxItemWidth;
        private int mMinItemWidth;

        public SendItemDelagate() {
            // 首先获取屏幕宽度
            int screenWidth = ScreenUtil.getScreenWidth(ChatActivity.this);
            mMaxItemWidth = (int) (screenWidth * 0.8f);
            mMinItemWidth = (int) (screenWidth * 0.1f);
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
            Glide.with(ChatActivity.this).load(mLoginInfo.getAvatar())
                    .error(R.drawable.icon_avatar_default).into(imageView);
            // 时间要处理成和微信一致的效果
            holder.setText(R.id.tv_time, DateUtil.parseDateToChatDate(new Date(chatMessage.getTime())));
            // 设置已读和未读状态
            TextView statusTextView = holder.getView(R.id.tv_status);
            if (chatMessage.isGroup()) {
                statusTextView.setVisibility(View.GONE);
            } else {
                if (chatMessage.isRead()) {
                    statusTextView.setText(R.string.read);
                    statusTextView.setTextColor(getResources().getColor(R.color.gray_textColor));
                } else {
                    statusTextView.setText(R.string.unread);
                    statusTextView.setTextColor(getResources().getColor(R.color.primary));
                }
            }
            switch (chatMessage.getType()) {
                // 文字消息
                case ChatMessage.TYPE_TXT:
                    holder.setVisible(R.id.tv_message, true);
                    holder.setText(R.id.tv_message, chatMessage.getContent());
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    holder.setVisible(R.id.iv_image, false);
                    holder.setVisible(R.id.tv_voice, false);
                    break;
                // 语音消息
                case ChatMessage.TYPE_VOICE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, true);
                    holder.setVisible(R.id.tv_record_length, true);
                    holder.setVisible(R.id.tv_voice, true);
                    holder.setVisible(R.id.iv_image, false);
                    holder.setText(R.id.tv_record_length, (int) chatMessage.getSecond() + "''");
                    holder.setText(R.id.tv_voice, (int) chatMessage.getSecond() + "''");
                    ViewGroup.LayoutParams lp = holder.getView(R.id.fl_anim).getLayoutParams();
                    lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * chatMessage.getSecond()));
                    holder.setOnClickListener(R.id.fl_anim, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 指定静止时候显示的图片
                            if (mSendAnimView != null) {
                                mSendAnimView.setBackgroundResource(R.drawable.talk_yy_me_3);
                                mSendAnimView = null;
                            }
                            // 播放动画
                            mSendAnimView = holder.getView(R.id.v_anim);
                            mSendAnimView.setBackgroundResource(R.drawable.voice_send_play);
                            AnimationDrawable anim = (AnimationDrawable) mSendAnimView.getBackground();
                            anim.start();
                            // 播放音频
                            MediaManager.playSound(chatMessage.getPath(), new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    // 播放完毕重新归位
                                    mSendAnimView.setBackgroundResource(R.drawable.talk_yy_me_3);
                                }
                            });
                        }
                    });
                    break;
                // 图片消息
                case ChatMessage.TYPE_IMAGE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    holder.setVisible(R.id.iv_image, true);
                    holder.setVisible(R.id.tv_voice, false);
                    // 压缩原图
                    // TODO 显示接受图片的时候大小有问题
                    Bitmap in = PictureUtil.getScaleBitmap(chatMessage.getLocalUrl(), 150, 150);
                    // 指定背景形状
                    Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.chat_send_normal);
                    // 裁剪前景后放入背景
                    Bitmap bmp = StreamUtil.getRoundCornerImage(bg, in);
                    holder.setImageBitmap(R.id.iv_image, bmp);
                    holder.setOnClickListener(R.id.iv_image, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = PhotoActivity.getIntent(ChatActivity.this,
                                    chatMessage.getLocalUrl(), null);
                            startActivity(intent);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 接受消息布局适配器
     */
    class ReceiveItemDelagate implements ItemViewDelegate<ChatMessage> {

        // 定义语音消息显示最长宽度和最窄宽度
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
            final ImageView imageView = holder.getView(R.id.iv_avatar);
            holder.setText(R.id.tv_time, DateUtil.parseDateToChatDate(new Date(chatMessage.getTime())));
            // 单聊
            if (!mGroup) {
                Glide.with(ChatActivity.this).load(mContactInfo.getAvatar())
                        .placeholder(R.drawable.icon_avatar_default).into(imageView);
                holder.setVisible(R.id.tv_nickname, false);
            }
            // 群聊
            else {
                // 读取群组中发送消息方资料
                new LocalContactCache() {
                    @Override
                    protected void onLoadContactInfoSuccess(ContactInfo info) {
                        Glide.with(ChatActivity.this).load(info.getAvatar())
                                .placeholder(R.drawable.icon_avatar_default).into(imageView);
                        holder.setVisible(R.id.tv_nickname, true);
                        holder.setText(R.id.tv_nickname, info.getDisplayName());

                    }
                }.getContactInfo(mLoginInfo.getRegisterId(), chatMessage.getFrom());
            }
            switch (chatMessage.getType()) {
                case ChatMessage.TYPE_TXT:
                    holder.setVisible(R.id.tv_message, true);
                    holder.setText(R.id.tv_message, chatMessage.getContent());
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    holder.setVisible(R.id.iv_image, false);
                    break;
                case ChatMessage.TYPE_VOICE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, true);
                    holder.setVisible(R.id.tv_record_length, true);
                    holder.setVisible(R.id.iv_image, false);
                    holder.setText(R.id.tv_record_length, (int) chatMessage.getSecond() + "''");
                    ViewGroup.LayoutParams lp = holder.getView(R.id.fl_anim).getLayoutParams();
                    lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * chatMessage.getSecond()));
                    holder.getView(R.id.fl_anim).setLayoutParams(lp);
                    holder.setOnClickListener(R.id.fl_anim, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 指定静止时候显示的图片
                            if (mSendAnimView != null) {
                                mSendAnimView.setBackgroundResource(R.drawable.talk_yy_you_3);
                                mSendAnimView = null;
                            }
                            // 播放动画
                            mSendAnimView = holder.getView(R.id.v_anim);
                            mSendAnimView.setBackgroundResource(R.drawable.voice_receive_play);
                            AnimationDrawable anim = (AnimationDrawable) mSendAnimView.getBackground();
                            anim.start();
                            // 播放音频
                            MediaManager.playSound(chatMessage.getPath(), new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    // 播放完毕重新归位
                                    mSendAnimView.setBackgroundResource(R.drawable.talk_yy_you_3);
                                }
                            });
                        }
                    });
                    break;
                case ChatMessage.TYPE_IMAGE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    holder.setVisible(R.id.iv_image, true);
                    // 压缩原图
                    Bitmap in = PictureUtil.getScaleBitmap(parseImagePath(chatMessage.getLocalUrl()), 300, 300);
                    // 指定背景形状
                    Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.chat_receive_normal);
                    // 裁剪前景后放入背景
                    if (in != null) {
                        Bitmap bmp = StreamUtil.getRoundCornerImage(bg, in);
                        holder.setImageBitmap(R.id.iv_image, bmp);
                        holder.setOnClickListener(R.id.iv_image, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = PhotoActivity.getIntent(ChatActivity.this,
                                        null, chatMessage.getRemoteUrl());
                                startActivity(intent);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
            holder.setOnClickListener(R.id.iv_avatar, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ContactDetailActivity.getIntent(ChatActivity.this, chatMessage.getFrom());
                    startActivity(intent);
                }
            });
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

    @OnClick(R.id.rv_content)
    void onContent(View view) {
        L.i("点击了中间区域");
        mOptionLayout.setVisibility(View.GONE);
        KeyBoardUtil.closeKeybord(mMessageEditText, this);
    }


    /**
     * 将环信给的图片路径转换成可以查询到的路径
     *
     * @param localUrl
     * @return
     */
    @NonNull
    public static String parseImagePath(String localUrl) {
        int index = localUrl.lastIndexOf("/");
        String fileName = localUrl.substring(index + 1);
        localUrl = localUrl.substring(0, index + 1);
        String path = localUrl + "thumb_" + fileName;
        // IOS会没有这个后缀
        if (path.endsWith(".jpg")) {
            path = path.substring(0, path.length() - 4);
        }
        return path;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GROUP_CHAT) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(this);
        MediaManager.release();
    }
}
