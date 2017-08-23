package com.jerry.nurse.activity;

import android.Manifest;
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
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jerry.nurse.R;
import com.jerry.nurse.listener.OnPhotographFinishListener;
import com.jerry.nurse.listener.OnSelectFromAlbumListener;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.ContactInfoCache;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.KeyBoardUtil;
import com.jerry.nurse.util.L;
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

    public static final String EXTRA_CONTACT_ID = "extra_contact_id";
    public static final String EXTRA_IS_GROUP = "extra_is_group";

    private static final int REQUEST_GROUP_CHAT = 0x101;

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

    @Bind(R.id.ll_photo)
    LinearLayout mPhotoLayout;

    @Bind(R.id.ll_photograph)
    LinearLayout mPhotographLayout;

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

    private com.jerry.nurse.model.Message mHomePageMessage;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
                } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                    EMImageMessageBody messageBody = (EMImageMessageBody) emMessage.getBody();
                    String localUrl = messageBody.getLocalUrl();
                    String path = parseImagePath(localUrl);
                    chatMessage.setPath(path);
                    chatMessage.setType(ChatMessage.TYPE_IMAGE);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                // 单聊
                if (!mIsGroup) {
                    chatMessage.setTo(mLoginInfo.getRegisterId());
                    chatMessage.setTime(emMessage.getMsgTime());
                    chatMessage.setFrom(emMessage.getFrom());
                    mChatMessages.add(chatMessage);
                    mAdapter.notifyDataSetChanged();

                    int itemCount = mAdapter.getItemCount();
                    if (mRecyclerView != null) {
                        mRecyclerView.scrollToPosition(itemCount);
                    }

                    // 构建首页消息
                    com.jerry.nurse.model.Message message = null;
                    try {
                        message = DataSupport.where("mType=? and mRegisterId=?", "1",
                                EMClient.getInstance().getCurrentUser())
                                .findFirst(com.jerry.nurse.model.Message.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (message == null) {
                        message = new com.jerry.nurse.model.Message();
                    }
                    message.setType(com.jerry.nurse.model.Message.TYPE_CHAT);
                    message.setImageUrl(mContactInfo.getAvatar());
                    message.setTitle(mContactInfo.getNickName());
                    message.setTime(new Date().getTime());
                    message.setRegisterId(EMClient.getInstance().getCurrentUser());
                    message.setContactId(mContactId);
                    message.save();
                }
                // 群聊
                else {
                    chatMessage.setTo(mContactId);
                    chatMessage.setTime(emMessage.getMsgTime());
                    chatMessage.setFrom(emMessage.getFrom());
                    mChatMessages.add(chatMessage);
                    mAdapter.notifyDataSetChanged();

                    int itemCount = mAdapter.getItemCount();
                    mRecyclerView.scrollToPosition(itemCount);

                    // 构建首页消息
                    mHomePageMessage = null;
                    try {
                        mHomePageMessage = DataSupport.where("mType=? and mRegisterId=? and mContactId=?", "2",
                                EMClient.getInstance().getCurrentUser(), mContactId)
                                .findFirst(com.jerry.nurse.model.Message.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    new ContactInfoCache() {
                        @Override
                        protected void onLoadContactInfoSuccess(ContactInfo info) {

                            if (mHomePageMessage == null) {
                                mHomePageMessage = new com.jerry.nurse.model.Message();
                            }
                            mHomePageMessage.setType(com.jerry.nurse.model.Message.TYPE_CHAT_GROUP);
                            mHomePageMessage.setImageResource(R.drawable.icon_qlt);
                            mHomePageMessage.setTitle(mGroupInfo.getHXNickName());
                            mHomePageMessage.setTime(new Date().getTime());
                            mHomePageMessage.setRegisterId(EMClient.getInstance().getCurrentUser());
                            mHomePageMessage.setContactId(mContactId);
                            mHomePageMessage.save();
                        }
                    }.tryToGetContactInfo(EMClient.getInstance().getCurrentUser(),
                            emMessage.getFrom());

                }
                chatMessage.save();
            }
        }
    };

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
        path = path.substring(0, path.length() - 4);
        return path;
    }

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
        mRecyclerView.scrollToPosition(mChatMessages.size());
        chatMessage.save();
    }

    /**
     * 发送环信图片消息
     *
     * @param path
     */
    private void easeMobSendImageMessage(String path) {
        // 创建消息
        EMMessage emMessage = EMMessage.createImageSendMessage(path, false, mContactId);
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
        ChatMessage chatMessage = MessageManager.saveSendImageChatMessageLocalData(emMessage, path);

        mChatMessages.add(chatMessage);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mChatMessages.size());
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
        mRecyclerView.scrollToPosition(mChatMessages.size());
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
            mOptionLayout.setVisibility(View.GONE);
        }
        mIsRecordState = !mIsRecordState;

    }


    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    public void onMessageReceived(final List<EMMessage> messages) {
        L.i("收到一条消息");
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
        if (!mIsGroup) {
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
                    holder.setVisible(R.id.iv_image, false);
                    break;
                case ChatMessage.TYPE_IMAGE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    holder.setVisible(R.id.iv_image, true);
                    Bitmap in = PictureUtil.getScaleBitmap(chatMessage.getPath(), 150, 150);
                    Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.chat_send_normal);
                    Bitmap bmp = StreamUtil.getRoundCornerImage(bg, in);
                    holder.setImageBitmap(R.id.iv_image, bmp);
                    break;
                case ChatMessage.TYPE_VOICE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, true);
                    holder.setVisible(R.id.tv_record_length, true);
                    holder.setVisible(R.id.iv_image, false);
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
                            mSendAnimView.setBackgroundResource(R.drawable.voice_send_play);
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
                holder.setVisible(R.id.tv_nickname, false);
            }
            // 群聊
            else {
                // 查询对方的信息
                ContactInfo ci = DataSupport.where("mRegisterId=?",
                        chatMessage.getFrom()).findFirst(ContactInfo.class);
                Glide.with(ChatActivity.this).load(ci.getAvatar())
                        .placeholder(R.drawable.icon_avatar_default).into(imageView);
                holder.setVisible(R.id.tv_nickname, true);
                holder.setText(R.id.tv_nickname, ci.getNickName());
            }
            holder.setText(R.id.tv_time, DateUtil.parseDateToString(new Date(chatMessage.getTime())));
            switch (chatMessage.getType()) {
                case ChatMessage.TYPE_TXT:
                    holder.setVisible(R.id.tv_message, true);
                    holder.setText(R.id.tv_message, chatMessage.getContent());
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    holder.setVisible(R.id.iv_image, false);
                    break;
                case ChatMessage.TYPE_IMAGE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, false);
                    holder.setVisible(R.id.tv_record_length, false);
                    holder.setVisible(R.id.iv_image, true);
                    Bitmap in = PictureUtil.getScaleBitmap(chatMessage.getPath(), 150, 150);
                    Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.chat_receive_normal);
                    Bitmap bmp = StreamUtil.getRoundCornerImage(bg, in);
                    holder.setImageBitmap(R.id.iv_image, bmp);
                    break;
                case ChatMessage.TYPE_VOICE:
                    holder.setVisible(R.id.tv_message, false);
                    holder.setVisible(R.id.fl_anim, true);
                    holder.setVisible(R.id.tv_record_length, true);
                    holder.setVisible(R.id.iv_image, false);
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
                            mSendAnimView.setBackgroundResource(R.drawable.voice_send_play);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GROUP_CHAT) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseActivity.requestRuntimePermission(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        }, new PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermission) {

            }
        });

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

        // 注册拍照和发图片两个事件监听
        setSelectFromAlbumListener(mPhotoLayout, new OnSelectFromAlbumListener() {
            @Override
            public void onPhotoFinished(Bitmap bitmap, File file) {
                easeMobSendImageMessage(file.getAbsolutePath());
            }
        });
        setPhotographListener(mPhotographLayout, new OnPhotographFinishListener() {
            @Override
            public void onPhotographFinished(Bitmap bitmap, File file) {
                easeMobSendImageMessage(file.getAbsolutePath());
            }
        });


        // 添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(this);
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
