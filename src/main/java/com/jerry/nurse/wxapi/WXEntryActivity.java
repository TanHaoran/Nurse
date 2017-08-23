package com.jerry.nurse.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 微信登录页面
 *
 * @author kevin_chen 2016-12-10 下午19:03:45
 * @version v1.0
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String APP_SECRET = "填写自己的AppSecret";
    private IWXAPI mWeixinAPI;
    public static final String WEIXIN_APP_ID = "填写自己的APP_id";

    private ProgressDialogManager mProgressDialogManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialogManager = new ProgressDialogManager(this);
        mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
        mWeixinAPI.handleIntent(this.getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWeixinAPI.handleIntent(intent, this);//必须调用此句话
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq req) {
        L.i("onReq");
    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        L.i("onResp");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                L.i("ERR_OK");
                //发送成功
                SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                if (sendResp != null) {
                    String code = sendResp.code;
                    getAccess_token(code);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                L.i("ERR_USER_CANCEL");
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                L.i("ERR_AUTH_DENIED");
                //发送被拒绝
                break;
            default:
                //发送返回
                break;
        }

    }

    /**
     * 获取openid accessToken值用于后期操作
     *
     * @param code 请求码
     */
    private void getAccess_token(final String code) {
        String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + WEIXIN_APP_ID
                + "&secret="
                + APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";
        L.i("getAccess_token：" + path);
        //网络请求，根据自己的请求方式
        OkHttpUtils.get().url(path).build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        L.i("getAccess_token_result:" + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String openid = jsonObject.getString("openid").toString().trim();
                            String access_token = jsonObject.getString("access_token").toString().trim();
                            getUserMesg(access_token, openid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 获取微信的个人信息
     *
     * @param access_token
     * @param openid
     */
    private void getUserMesg(final String access_token, final String openid) {
        String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid;
        L.i("getUserMesg：" + path);
        //网络请求，根据自己的请求方式

        OkHttpUtils.get().url(path).build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        L.i("getUserMesg_result:" + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String nickname = jsonObject.getString("nickname");
                            int sex = Integer.parseInt(jsonObject.get("sex").toString());
                            String headimgurl = jsonObject.getString("headimgurl");

                            L.i("用户基本信息:");
                            L.i("nickname:" + nickname);
                            L.i("sex:" + sex);
                            L.i("headimgurl:" + headimgurl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                });
    }

}
