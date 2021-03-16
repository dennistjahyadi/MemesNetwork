package com.dovoo.memesnetwork.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.dovoo.memesnetwork.utils.FontManager;

public class TextViewFaBrands extends AppCompatTextView {
    public TextViewFaBrands(Context context) {
        super(context);
        setDefaultTypeFace(context);
    }

    public TextViewFaBrands(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultTypeFace(context);
    }

    public TextViewFaBrands(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaultTypeFace(context);
    }


    private void setDefaultTypeFace(Context context) {
        this.setTypeface(FontManager.getTypeFace(context, FontManager.faBrands400));
    }
}