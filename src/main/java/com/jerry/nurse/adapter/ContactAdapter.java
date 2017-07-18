package com.jerry.nurse.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jerry.nurse.R;

public class ContactAdapter extends BaseAdapter {
    private String[] strings = new String[]{"helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod", "helo", "wod",};

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int position) {
        return strings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R
                .layout.item_contact, null);
        TextView tv = (TextView) convertView.findViewById(R.id.tv_nickname);
        tv.setText(strings[position]);
        Button btn = (Button) convertView.findViewById(R.id.btn_delete);
        //删除监听  
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("hehh");
            }
        });
        return convertView;
    }

}  