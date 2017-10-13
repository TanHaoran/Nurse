package com.jerry.nurse.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDex;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.AlarmManager;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LocalContactCache;
import com.jerry.nurse.util.LocalGroupCache;
import com.jerry.nurse.util.MessageManager;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

import static com.jerry.nurse.activity.MainActivity.ACTION_CHAT_MESSAGE_READ;
import static com.jerry.nurse.activity.MainActivity.ACTION_CHAT_MESSAGE_RECEIVE;
import static com.jerry.nurse.activity.MainActivity.ACTION_FRIEND_APPLY_RECEIVE;
import static com.jerry.nurse.activity.MainActivity.EXTRA_CHAT_MESSAGE;
import static com.jerry.nurse.activity.MainActivity.EXTRA_FRIEND_APPLY_CONTACT;


/**
 * Created by Jerry on 2017/7/17.
 */

public class MyApplication extends LitePalApplication {

    public static IWXAPI sWxApi;

    @Override
    public void onCreate() {
        super.onCreate();

        // 设置字体大小不随系统而变化
        initFontSize();
        // 初始化极光推送
        initJPush();
        // 初始化LitePal        
        initLitePal();
        // 初始化友盟
        initMobclickAgent();
        // 初始化环信
        initEaseMob();
        // 初始化OkHttp封装类
        initOkHttp();
        // 初始化注册微信
        initWeChat();
        // 初始化微博
        initWebSDK();
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
     * 初始化极光推送
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
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
        // 已读回执
        options.setRequireAck(true);
        // 送达回执
        options.setRequireDeliveryAck(true);

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

        // 开启消息接收监听
        startMessageListener();
        // 开启好友状态监听
        startContactListener();
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
     * 开启消息监听
     */
    private void startMessageListener() {
        EMMessageListener emMessageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                if (checkIfChatting()) {
                    return;
                }

                // 播放系统提示音
                AlarmManager.playAlarm(getApplicationContext());

                // 对传过来的消息进行逐条处理
                for (final EMMessage emMessage : messages) {
                    L.i("收到一条消息:" + emMessage.getMsgId());
                    // 首先去寻找本地数据库是否有这个人
                    new LocalContactCache() {
                        @Override
                        protected void onLoadContactInfoSuccess(ContactInfo ci) {
                            // 保存记录并发送广播
                            saveChatMessageAndSendBroadcast(emMessage);
                        }
                    }.getContactInfo(EMClient.getInstance().getCurrentUser(), emMessage.getFrom());

                    if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                        new LocalGroupCache() {
                            @Override
                            protected void onLoadGroupInfoSuccess(GroupInfo info) {
                                L.i("更新后的群昵称是:" + info.getHXNickName());
                            }
                        }.getGroupInfo(EMClient.getInstance().getCurrentUser(),
                                emMessage.getTo());
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                if (checkIfChatting()) {
                    return;
                }
                L.i("收到透传消息");
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                if (checkIfChatting()) {
                    return;
                }
                L.i("收到已读回执");
                updateChatMessageAndSendBroadcast(messages);
            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {
                if (checkIfChatting()) {
                    return;
                }
                L.i("收到已送达回执");
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                if (checkIfChatting()) {
                    return;
                }
                L.i("消息状态变动");
            }
        };
        // 开始监听
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }

    /**
     * 检测是否在聊天窗口中
     *
     * @return
     */
    private boolean checkIfChatting() {
        String name = ActivityCollector.getTopActivity().getLocalClassName();
        if (name.equals("activity.ChatActivity")) {
            return true;
        }
        return false;
    }

    /**
     * 保存聊天信息并发送广播
     *
     * @param emMessage
     */
    private void saveChatMessageAndSendBroadcast(EMMessage emMessage) {
        // 保存到数据库
        ChatMessage chatMessage = MessageManager.saveChatMessageData(emMessage, false);
        // 发送广播
        Intent intent = new Intent(ACTION_CHAT_MESSAGE_RECEIVE);
        intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
        getContext().sendBroadcast(intent);
    }

    /**
     * 更新聊天信息状态并发送广播
     *
     * @param emMessages
     */
    private void updateChatMessageAndSendBroadcast(List<EMMessage> emMessages) {

        for (EMMessage emMessage : emMessages) {
            // 单聊
            if (emMessage.getChatType() == EMMessage.ChatType.Chat) {

                // 从数据库中找到这条数据设置消息状态已读
                List<ChatMessage> cms = DataSupport.where("mFrom=? and mTo=? and mRead=?",
                        emMessage.getFrom(), emMessage.getTo(), "0").find(ChatMessage.class);
                for (ChatMessage cm : cms) {
                    if (!cm.isRead()) {
                        cm.setRead(true);
                        cm.save();
                        L.i("设置一条已读");
                    }
                }
            }
            // TODO 群聊
            else {

            }
        }

        // 发送广播
        Intent intent = new Intent(ACTION_CHAT_MESSAGE_READ);
        getContext().sendBroadcast(intent);
    }

    /**
     * 开启好友状态监听
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

                // 播放系统提示音
                AlarmManager.playAlarm(getApplicationContext());

                new LocalContactCache() {
                    @Override
                    protected void onLoadContactInfoSuccess(ContactInfo ci) {

                        // 保存好友申请到数据库
                        AddFriendApply apply = MessageManager
                                .saveApplyLocalData(ci, reason, false);
                        Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                        intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                        getContext().sendBroadcast(intent);
                    }
                }.getContactInfo(EMClient.getInstance().getCurrentUser(), username);
            }

            //好友请求被同意
            @Override
            public void onFriendRequestAccepted(String username) {
                L.i("好友请求被同意：" + username);

                new LocalContactCache() {
                    @Override
                    protected void onLoadContactInfoSuccess(ContactInfo ci) {
                        // 保存好友申请到数据库
                        ci.setFriend(true);
                        ci.save();
                        AddFriendApply apply = MessageManager.updateApplyData(ci, true);
                        Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                        intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                        getContext().sendBroadcast(intent);
                    }
                }.getContactInfo(EMClient.getInstance().getCurrentUser(), username);
            }

            //好友请求被拒绝
            @Override
            public void onFriendRequestDeclined(String username) {
                L.i("好友请求被拒绝：" + username);

                new LocalContactCache() {
                    @Override
                    protected void onLoadContactInfoSuccess(ContactInfo info) {
                        // 保存好友申请到数据库
                        AddFriendApply apply = MessageManager.updateApplyData(info, false);
                        Intent intent = new Intent(ACTION_FRIEND_APPLY_RECEIVE);
                        intent.putExtra(EXTRA_FRIEND_APPLY_CONTACT, apply);
                        getContext().sendBroadcast(intent);
                    }
                }.getContactInfo(EMClient.getInstance().getCurrentUser(), username);
            }
        };
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
    }

    /**
     * 初始化OkHttp封装类
     */
    private void initOkHttp() {
        // 设置连接超时和读取超时时间
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(L.TAG))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }


    /**
     * 初始化注册微信
     */
    private void initWeChat() {
        sWxApi = WXAPIFactory.createWXAPI(this, ServiceConstant.WX_APP_ID, false);
        sWxApi.registerApp(ServiceConstant.WX_APP_ID);
    }

    //新浪微博初始化，对应的参数分别是app_key,回调地址，和权限
    private void initWebSDK() {
        WbSdk.install(this, new AuthInfo(this, ServiceConstant.SINA_APP_KEY, ServiceConstant.SINA_REDIRECT_URL,
                ServiceConstant.SINA_SCOPE));
    }


    /**
     * 超过最大方法数量后，设置允许的方法
     *
     * @param base
     */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
