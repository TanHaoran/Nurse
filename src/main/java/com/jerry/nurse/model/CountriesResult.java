package com.jerry.nurse.model;

import java.util.List;

/**
 * Created by Jerry on 2017/8/6.
 */

public class CountriesResult {

    /**
     * body : [{"CountryCode":"+86","CountryId":"0000000001","CountryName":"中国","DisplayOrder":1,"IsFlag":"1"},{"CountryCode":"+866","CountryId":"0000000002","CountryName":"中国台湾","DisplayOrder":2,"IsFlag":"1"},{"CountryCode":"+852","CountryId":"0000000003","CountryName":"中国香港","DisplayOrder":3,"IsFlag":"1"},{"CountryCode":"81","CountryId":"0000000004","CountryName":"日本","DisplayOrder":5,"IsFlag":"1"},{"CountryCode":"001","CountryId":"0000000005","CountryName":"美国","DisplayOrder":6,"IsFlag":"1"},{"CountryCode":"+853","CountryId":"0000000006","CountryName":"中国香港","DisplayOrder":4,"IsFlag":"1"},{"CountryCode":"82","CountryId":"0000000007","CountryName":"韩国","DisplayOrder":7,"IsFlag":"1"},{"CountryCode":"+44","CountryId":"0000000008","CountryName":"英国","DisplayOrder":8,"IsFlag":"1"},{"CountryCode":"+1","CountryId":"0000000009","CountryName":"加拿大","DisplayOrder":9,"IsFlag":"1"},{"CountryCode":"+49","CountryId":"0000000010","CountryName":"德国","DisplayOrder":10,"IsFlag":"1"},{"CountryCode":"+33","CountryId":"0000000011","CountryName":"法国","DisplayOrder":11,"IsFlag":"1"}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<Country> body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Country> getBody() {
        return body;
    }

    public void setBody(List<Country> body) {
        this.body = body;
    }
}
