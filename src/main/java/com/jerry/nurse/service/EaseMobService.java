package com.jerry.nurse.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactDetailResult;
import com.jerry.nurse.model.Message;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;

import static com.jerry.nurse.activity.MainActivity.ACTION_CHAT_MESSAGE_RECEIVE;
import static com.jerry.nurse.activity.MainActivity.ACTION_FRIEND_APPLY_RECEIVE;
import static com.jerry.nurse.activity.MainActivity.EXTRA_CHAT_MESSAGE;
import static com.jerry.nurse.activity.MainActivity.EXTRA_FRIEND_APPLY_CONTACT;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

/**
 * Created by Jerry on 2017/8/9.
 */

public class EaseMobService extends Service {

    private EMMessageListener mMessageListener;
    private EMContactListener mContactListener;

    private String mRegisterId;

    public EaseMobService() {
        L.i("环信后台监控服务已起用");
        mRegisterId = (String) SPUtil.get(ActivityCollector.getTopActivity(), SPUtil.REGISTER_ID, "");
        startContactListener();
        startMessageListener();
    }

    /**
     * 消息监听
     */
    private void startMessageListener() {
        mMessageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                String name = ActivityCollector.getTopActivity().getLocalClassName();
                if (name.equals("activity.ChatActivity")) {
                    return;
                }
                for (final EMMessage emMessage : messages) {
                    L.i(emMessage.toString());
                    EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
                    String message = messageBody.getMessage();
                    L.i("消息内容：" + message);

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setFrom(emMessage.getFrom());
                    chatMessage.setTo(emMessage.getTo());
                    chatMessage.setSend(false);
                    chatMessage.setContent(message);
                    chatMessage.setTime(emMessage.getMsgTime());
                    chatMessage.save();
                    L.i("从服务中已存入数据库");

                    Intent intent = new Intent(ACTION_CHAT_MESSAGE_RECEIVE);
                    intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                L.i("收到透传消息");
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                L.i("收到已读回执");
            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {
                L.i("收到已送达回执");
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                L.i("消息状态变动");
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    /**
     * 监听好友状态
     */
    private void startContactListener() {
        mContactListener = new EMContactListener() {
            //增加了联系人时回调此方法
            @Override
            public void onContactAdded(String username) {
                L.i("增加了联系人时回调此方法");
            }

            //被删除时回调此方法
            @Override
            public void onContactDeleted(String username) {
                L.i("被删除时回调此方法：" + username);

            }

            //收到好友邀请
            @Override
            public void onContactInvited(String username, String reason) {
                L.i("收到好友邀请：" + username);
                getFriendInfo(username, reason);
            }

            //好友请求被同意
            @Override
            public void onFriendRequestAccepted(String username) {
                L.i("好友请求被同意：" + username);
                OkHttpUtils.get().url(ServiceConstant.GET_USER_DETAIL_INFO)
                        .addParams("MyId", mRegisterId)
                        .addParams("FriendId", username)
                        .build()
                        .execute(new FilterStringCallback() {

                            @Override
                            public void onFilterResponse(String response, int id) {
                                ContactDetailResult contactDetailResult = new Gson().fromJson(response, ContactDetailResult.class);
                                if (contactDetailResult.getCode() == RESPONSE_SUCCESS) {
                                    if (contactDetailResult.getBody() != null) {
                                        Contact contact = contactDetailResult.getBody();
                                        updateAddFriendApplyLocalData(contact, true);
                                    }
                                } else {
                                    T.showShort(EaseMobService.this, contactDetailResult.getMsg());
                                }
                            }
                        });
            }

            //好友请求被拒绝
            @Override
            public void onFriendRequestDeclined(String username) {
                L.i("好友请求被拒绝：" + username);
                OkHttpUtils.get().url(ServiceConstant.GET_USER_DETAIL_INFO)
                        .addParams("MyId", mRegisterId)
                        .addParams("FriendId", username)
                        .build()
                        .execute(new FilterStringCallback() {

                            @Override
                            public void onFilterResponse(String response, int id) {
                                ContactDetailResult contactDetailResult = new Gson().fromJson(response, ContactDetailResult.class);
                                if (contactDetailResult.getCode() == RESPONSE_SUCCESS) {
                                    if (contactDetailResult.getBody() != null) {
                                        Contact contact = contactDetailResult.getBody();
                                        updateAddFriendApplyLocalData(contact, false);
                                    }
                                } else {
                                    T.showShort(EaseMobService.this, contactDetailResult.getMsg());
                                }
                            }
                        });

            }
        };
        EMClient.getInstance().contactManager().setContactListener(mContactListener);
    }

    /**
     * 获取好友资料
     *
     * @param friendId
     * @param reason
     */
    private void getFriendInfo(final String friendId, final String reason) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_DETAIL_INFO)
                .addParams("MyId", mRegisterId)
                .addParams("FriendId", friendId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        ContactDetailResult contactDetailResult = new Gson().fromJson(response, ContactDetailResult.class);
                        if (contactDetailResult.getCode() == RESPONSE_SUCCESS) {
                            if (contactDetailResult.getBody() != null) {
                                Contact contact = contactDetailResult.getBody();

                                Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                                intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, contact);

                                // 发送广播
                                sendBroadcast(intent);
                                // 插入本地数据库
                                saveAddFriendApplyLocalData(contact, reason);
                            }
                        } else {
                            T.showShort(EaseMobService.this, contactDetailResult.getMsg());
                        }
                    }
                });
    }

    /**
     * 插入本地数据库
     *
     * @param contact
     * @param reason
     */
    private void saveAddFriendApplyLocalData(Contact contact, String reason) {
        // 构建首页消息
        Message message = null;
        try {
            message = DataSupport.where("mType=? and mRegisterId=?", "0", mRegisterId).findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_ADD_FRIEND_APPLY);
        message.setImageResource(R.drawable.icon_xzhy);
        message.setTitle("好友申请");
        message.setTime(new Date().getTime());
        message.setRegisterId(mRegisterId);
        message.setContactId(contact.getFriendId());
        message.setContent("收到" + contact.getNickName() + "好友请求");
        message.save();

        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    mRegisterId, contact.getFriendId()).findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setAvatar(contact.getAvatar());
        apply.setNickname(contact.getNickName());
        apply.setStatus(AddFriendApply.STATUS_RECEIVE_ING);
        apply.setContactId(contact.getFriendId());
        apply.setRegisterId(mRegisterId);
        apply.setTime(new Date().getTime());
        apply.setReason(reason);
        apply.save();
    }

    /**
     * 根据对方同意或拒绝更新数据库
     *
     * @param contact
     * @param agree   是否同意
     */
    private void updateAddFriendApplyLocalData(Contact contact, boolean agree) {
        // 构建首页消息
        Message message = null;
        try {
            message = DataSupport.where("mType=? and mRegisterId=?", "0", mRegisterId).findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_ADD_FRIEND_APPLY);
        message.setImageResource(R.drawable.icon_pb);
        message.setTitle("好友申请");
        message.setTime(new Date().getTime());
        message.setRegisterId(mRegisterId);
        message.setContactId(contact.getFriendId());
        if (agree) {
            message.setContent(contact.getNickName() + "已同意好友申请");
        } else {
            message.setContent(contact.getNickName() + "已拒绝好友申请");
        }
        message.save();

        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    mRegisterId, contact.getFriendId()).findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setAvatar(contact.getAvatar());
        apply.setNickname(contact.getNickName());
        apply.setContactId(contact.getFriendId());
        apply.setRegisterId(mRegisterId);
        if (agree) {
            apply.setStatus(AddFriendApply.STATUS_AGREE);
        } else {
            apply.setStatus(AddFriendApply.STATUS_REFUSE);
        }
        apply.setTime(new Date().getTime());
        apply.save();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
        EMClient.getInstance().contactManager().removeContactListener(mContactListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}