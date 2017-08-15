package com.jerry.nurse.app;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactDetailResult;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.GetContactInfoUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.MessageManager;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.jerry.nurse.activity.MainActivity.ACTION_CHAT_MESSAGE_RECEIVE;
import static com.jerry.nurse.activity.MainActivity.ACTION_FRIEND_APPLY_RECEIVE;
import static com.jerry.nurse.activity.MainActivity.EXTRA_CHAT_MESSAGE;
import static com.jerry.nurse.activity.MainActivity.EXTRA_FRIEND_APPLY_CONTACT;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;


/**
 * Created by Jerry on 2017/7/17.
 */

public class MyApplication extends LitePalApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        // 设置字体大小不随系统而变化
        initFontSize();

        // 初始化LitePal        
        initLitePal();

        // 初始化友盟
        initMobclickAgent();
        // 初始化环信
        initEaseMob();
        // 初始化OkHttp封装类
        initOkHttp();
    }

    /**
     * 设置字体大小不随系统而变化
     */
    private void initFontSize() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    /**
     * 初始化LitePal
     */
    private void initLitePal() {
        LitePal.initialize(this);
    }

    /**
     * 初始化友盟
     */
    private void initMobclickAgent() {
        // 禁止默认的页面统计方式
        MobclickAgent.openActivityDurationTrack(false);
    }

    /**
     * 初始化环信
     */
    private void initEaseMob() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 自动登录
        options.setAutoLogin(true);

        // 初始化前要验证
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
            L.i("enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        // 开启消息监听和申请好友监听
        startMessageListener();
        startContactListener();
    }


    /**
     * 消息监听
     */
    private void startMessageListener() {
        EMMessageListener emMessageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                L.i("收到消息");
                String name = ActivityCollector.getTopActivity().getLocalClassName();
                if (name.equals("activity.ChatActivity")) {
                    return;
                }
                for (final EMMessage emMessage : messages) {
                    L.i(emMessage.toString());
                    L.i("收到消息的类型是：" + emMessage.getType());
                    L.i("收到消息的Chat类型是：" + emMessage.getChatType());
                    L.i("收到消息的发出者是：" + emMessage.getFrom());
                    L.i("收到消息的接收者是：" + emMessage.getTo());

                    ContactInfo ci = DataSupport.where("mRegisterId=?", emMessage.getFrom()).findFirst(ContactInfo.class);
                    if (ci == null) {
                        GetContactInfoUtil getContactInfoUtil = new GetContactInfoUtil();
                        getContactInfoUtil.setOnLoadSuccess(new GetContactInfoUtil.OnLoadSuccess() {
                            @Override
                            public void onLoadSuccess(ContactInfo ci) {
                                saveMessageDataAndSendBroadcast(emMessage);
                            }
                        });
                        getContactInfoUtil.getContactDetail(EMClient.getInstance().getCurrentUser(),
                                emMessage.getFrom());
                    } else {
                        saveMessageDataAndSendBroadcast(emMessage);
                    }

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
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }

    /**
     * 保存聊天信息并发送广播
     *
     * @param emMessage
     */
    private void saveMessageDataAndSendBroadcast(EMMessage emMessage) {

        ChatMessage chatMessage = null;
        if (emMessage.getType() == EMMessage.Type.TXT) {
            EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
            String msg = messageBody.getMessage();
            L.i("文字消息内容：" + msg);

            if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
                chatMessage = MessageManager.saveReceiveChatMessageLocalData(emMessage, msg);
            } else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                chatMessage = MessageManager.saveReceiveChatGroupMessageLocalData(emMessage, msg);
            }
        } else if (emMessage.getType() == EMMessage.Type.VOICE) {
            EMVoiceMessageBody messageBody = (EMVoiceMessageBody) emMessage.getBody();
            L.i("音频时长：" + messageBody.getLength());
            L.i("音频路径：" + messageBody.getLocalUrl());

            if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
                chatMessage = MessageManager.saveReceiveChatMessageLocalData(emMessage,
                        messageBody.getLength(), messageBody.getLocalUrl());
            } else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                // TODO
            }
        }


        Intent intent = new Intent(ACTION_CHAT_MESSAGE_RECEIVE);
        intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
        getContext().sendBroadcast(intent);
    }

    /**
     * 监听好友状态
     */
    private void startContactListener() {
        EMContactListener emContactListener = new EMContactListener() {
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
            public void onContactInvited(final String username, final String reason) {
                L.i("收到好友邀请：" + username);
                ContactInfo ci = DataSupport.where("mRegisterId=?",
                        username).findFirst(ContactInfo.class);
                if (ci == null) {
                    GetContactInfoUtil util = new GetContactInfoUtil();
                    util.setOnLoadSuccess(new GetContactInfoUtil.OnLoadSuccess() {
                        @Override
                        public void onLoadSuccess(final ContactInfo info) {
                            // 保存好友申请到数据库
                            AddFriendApply apply = MessageManager.saveReceiveAddFriendApplyLocalData(info, reason);
                            Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                            intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                            getContext().sendBroadcast(intent);
                        }
                    });
                    util.getContactDetail(EMClient.getInstance().getCurrentUser(),username );
                } else {
                    // 保存好友申请到数据库
                    AddFriendApply apply = MessageManager.saveReceiveAddFriendApplyLocalData(ci, reason);
                    Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                    intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                    getContext().sendBroadcast(intent);
                }
            }

            //好友请求被同意
            @Override
            public void onFriendRequestAccepted(String username) {
                L.i("好友请求被同意：" + username);

                ContactInfo ci = DataSupport.where("mRegisterId=?",
                        username).findFirst(ContactInfo.class);
                if (ci == null) {
                    GetContactInfoUtil util = new GetContactInfoUtil();
                    util.setOnLoadSuccess(new GetContactInfoUtil.OnLoadSuccess() {
                        @Override
                        public void onLoadSuccess(final ContactInfo info) {
                            // 保存好友申请到数据库
                            info.setFriend(true);
                            info.save();
                            AddFriendApply apply = MessageManager.updateReceiveAddFriendApplyLocalData(info, true);
                            Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                            intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                            getContext().sendBroadcast(intent);
                        }
                    });
                    util.getContactDetail(EMClient.getInstance().getCurrentUser(),username );
                } else {
                    // 保存好友申请到数据库
                    ci.setFriend(true);
                    ci.save();
                    AddFriendApply apply = MessageManager.updateReceiveAddFriendApplyLocalData(ci, true);
                    Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                    intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                    getContext().sendBroadcast(intent);
                }
            }

            //好友请求被拒绝
            @Override
            public void onFriendRequestDeclined(String username) {
                L.i("好友请求被拒绝：" + username);

                ContactInfo ci = DataSupport.where("mRegisterId=?",
                        username).findFirst(ContactInfo.class);
                if (ci == null) {
                    GetContactInfoUtil util = new GetContactInfoUtil();
                    util.setOnLoadSuccess(new GetContactInfoUtil.OnLoadSuccess() {
                        @Override
                        public void onLoadSuccess(final ContactInfo info) {
                            // 保存好友申请到数据库
                            AddFriendApply apply = MessageManager.updateReceiveAddFriendApplyLocalData(info, false);
                            Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                            intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                            getContext().sendBroadcast(intent);
                        }
                    });
                    util.getContactDetail(EMClient.getInstance().getCurrentUser(),username );
                } else {
                    // 保存好友申请到数据库
                    AddFriendApply apply = MessageManager.updateReceiveAddFriendApplyLocalData(ci, false);
                    Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                    intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                    getContext().sendBroadcast(intent);
                }

            }
        };
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
    }

       /**
     * 获取app名称
     *
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * 查询用户资料
     *
     * @param registerId
     * @param userRegisterId
     * @param apply
     */
    private void getContactDetail(String registerId, String userRegisterId, final AddFriendApply apply) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_DETAIL_INFO)
                .addParams("MyId", registerId)
                .addParams("FriendId", userRegisterId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        ContactDetailResult contactDetailResult = new Gson().fromJson(response, ContactDetailResult.class);
                        if (contactDetailResult.getCode() == RESPONSE_SUCCESS) {
                            if (contactDetailResult.getBody() != null) {
                                Contact contact = contactDetailResult.getBody();
                                ContactInfo info = new ContactInfo();
                                info.setAvatar(contact.getAvatar());
                                info.setName(contact.getName());
                                info.setNickName(contact.getNickName());
                                info.setCellphone(contact.getPhone());
                                info.setRemark(contact.getRemark());
                                info.setRegisterId(contact.getFriendId());
                                info.setFriend(contact.isFriend());
                                info.save();
                                // 联系人信息存入数据库并发送广播
                                Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                                intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                                getContext().sendBroadcast(intent);
                            }
                        }
                    }
                });
    }

    /**
     * 初始化OkHttp封装类
     */
    private void initOkHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(L.TAG))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

}
