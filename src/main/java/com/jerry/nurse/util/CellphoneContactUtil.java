package com.jerry.nurse.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class CellphoneContactUtil {

    /**
     * 获取手机通讯录所有联系人
     *
     * @param context
     * @return
     */
    public static List<CellphoneContact> getPhoneNumberFromMobile(Context context) {
        List<CellphoneContact> list = new ArrayList<CellphoneContact>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        //moveToNext方法返回的是一个boolean类型的数据  
        while (cursor.moveToNext()) {
            //读取通讯录的姓名  
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //读取通讯录的号码  
            String number = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number = number.replace(" ", "");
            number = number.replace("-", "");
            if (number.length() >= 11) {
                number = number.substring(number.length() - 11);
            }
            CellphoneContact phoneInfo = new CellphoneContact();
            phoneInfo.setName(name);
            phoneInfo.setNickName(name);
            phoneInfo.setPhone(number);
            list.add(phoneInfo);
        }
        return list;
    }
}  