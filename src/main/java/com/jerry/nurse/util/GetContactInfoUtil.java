package com.jerry.nurse.util;

import com.google.gson.Gson;
import com.jerry.nurse.activity.MainActivity;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactDetailResult;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

/**
 * Created by Jerry on 2017/8/15.
 */

public class GetContactInfoUtil {


    private OnLoadSuccess mOnLoadSuccess;

    public void setOnLoadSuccess(OnLoadSuccess onLoadSuccess) {
        mOnLoadSuccess = onLoadSuccess;
    }

    public interface OnLoadSuccess {
        void onLoadSuccess(ContactInfo ci);
    }

    public GetContactInfoUtil() {
    }

    /**
     * 获取用户资料
     *
     * @param registerId
     * @param contactId
     */
    public void getContactDetail(String registerId, String contactId) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_DETAIL_INFO)
                .addParams("MyId", registerId)
                .addParams("FriendId", contactId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        ContactDetailResult contactDetailResult = new Gson().fromJson(response, ContactDetailResult.class);
                        if (contactDetailResult.getCode() == RESPONSE_SUCCESS) {
                            if (contactDetailResult.getBody() != null) {
                                Contact contact = contactDetailResult.getBody();
                                if (contact != null) {
                                    ContactInfo ci = MainActivity.updateContactInfoData(contact);
                                    if (mOnLoadSuccess != null) {
                                        mOnLoadSuccess.onLoadSuccess(ci);
                                    }
                                }
                            }
                        } else {
                            L.i(contactDetailResult.getMsg());
                        }
                    }
                });
    }
}
