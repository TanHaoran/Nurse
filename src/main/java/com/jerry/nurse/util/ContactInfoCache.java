package com.jerry.nurse.util;

import com.google.gson.Gson;
import com.jerry.nurse.activity.MainActivity;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactDetailResult;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

/**
 * Created by Jerry on 2017/8/15.
 */

public abstract class ContactInfoCache {


    public ContactInfoCache() {
    }

    /**
     * 获取数据库中联系人信息
     *
     * @param registerId
     * @param contactId
     */
    public void tryToGetContactInfo(String registerId, String contactId) {
        ContactInfo info = DataSupport.where("mRegisterId=?", contactId).findFirst(ContactInfo.class);
        if (info == null) {
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
                                        onLoadContactInfoSuccess(ci);
                                    }
                                }
                            } else {
                                L.i(contactDetailResult.getMsg());
                            }
                        }
                    });
        } else {
            onLoadContactInfoSuccess(info);
        }
    }

    /**
     * 当查询到联系人详细信息的时候调用
     *
     * @param info
     */
    protected abstract void onLoadContactInfoSuccess(ContactInfo info);
}
