package com.jerry.nurse.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/8/29.
 */

public class PasswordEditText extends AppCompatEditText {

    private boolean mIsHide = true;

    public PasswordEditText(Context context) {
        super(context);
        init();
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        final Drawable[] drawables = getCompoundDrawables();
        final int eyeWidth = drawables[2].getBounds().width();// 眼睛图标的宽度
        final Drawable drawableEyeOpen = getResources().getDrawable(R.drawable.eye_open);
        drawableEyeOpen.setBounds(drawables[2].getBounds());//这一步不能省略
        setOnTouchListener(new View.OnTouchListener() {
                               @Override
                               public boolean onTouch(View v, MotionEvent event) {
                                   if (event.getAction() == MotionEvent.ACTION_UP) {
                                       float et_pwdMinX = v.getWidth() - eyeWidth - getPaddingRight();
                                       float et_pwdMaxX = v.getWidth();
                                       float et_pwdMinY = 0;
                                       float et_pwdMaxY = v.getHeight();
                                       float x = event.getX();
                                       float y = event.getY();

                                       // 点击了眼睛图标的位置
                                       if (x < et_pwdMaxX && x > et_pwdMinX && y > et_pwdMinY && y < et_pwdMaxY) {
                                           mIsHide = !mIsHide;
                                           if (mIsHide) {
                                               setCompoundDrawables(null, null, drawables[2], null);
                                               setTransformationMethod(PasswordTransformationMethod.getInstance());
                                           } else {
                                               setCompoundDrawables(null, null, drawableEyeOpen, null);
                                               setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                           }
                                       }
                                   }
                                   return false;
                               }
                           }
        );
    }
}
