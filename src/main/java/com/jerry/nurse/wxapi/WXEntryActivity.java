package com.jerry.nurse.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jerry.nurse.app.MyApplication;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.WeChatLoginManager;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //如果没回调onResp，八成是这句没有写
        MyApplication.sWxApi.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        L.i("错误码 : " + resp.errCode);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()) {
                    T.showShort(WXEntryActivity.this, "分享失败");
                } else {
                    T.showShort(WXEntryActivity.this, "登录失败");
                }
                break;
            // 请求成功
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        // 拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) resp).code;
                        L.i("code = " + code);
                        // 调用微信登录管理器登录
                        WeChatLoginManager weChatLoginManager = new WeChatLoginManager(WXEntryActivity.this);
                        weChatLoginManager.login(code);
                        finish();
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        T.showShort(WXEntryActivity.this, "微信分享成功");
                        finish();
                        break;
                }
                break;
        }
    }


}