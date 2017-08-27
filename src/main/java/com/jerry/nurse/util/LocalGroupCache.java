package com.jerry.nurse.util;

import com.google.gson.Gson;
import com.jerry.nurse.activity.MainActivity;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.GroupInfoResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

/**
 * Created by Jerry on 2017/8/15.
 */

public abstract class LocalGroupCache {
    /**
     * 获取数据库中群信息
     *
     * @param registerId
     * @param groupId
     */
    public void getGroupInfo(String registerId, String groupId) {
        GroupInfo info = DataSupport.where("HXGroupId=?", groupId)
                .findFirst(GroupInfo.class);
        if (info == null) {
            OkHttpUtils.get().url(ServiceConstant.GET_GROUP_INFO)
                    .addParams("RegisterId", registerId)
                    .addParams("GroupId", groupId)
                    .build()
                    .execute(new FilterStringCallback() {

                        @Override
                        public void onFilterResponse(String response, int id) {
                            GroupInfoResult result = new Gson().fromJson(response, GroupInfoResult.class);
                            if (result.getCode() == RESPONSE_SUCCESS) {
                                if (result.getBody() != null) {
                                    GroupInfo group = result.getBody();
                                    if (group != null) {
                                        MainActivity.updateGroupInfoData(group);
                                        onLoadGroupInfoSuccess(group);
                                    }
                                }
                            } else {
                                L.i(result.getMsg());
                            }
                        }
                    });
        } else {
            onLoadGroupInfoSuccess(info);
        }
    }

    /**
     * 查查询到群组信息的时候调用
     *
     * @param info
     */
    protected abstract void onLoadGroupInfoSuccess(GroupInfo info);
}
