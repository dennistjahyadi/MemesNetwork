package com.example.acer.memesnetwork.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.example.acer.memesnetwork.utils.FontManager;

public class TextViewFaSolid extends AppCompatTextView {
    public TextViewFaSolid(Context context) {
        super(context);
        setDefaultTypeFace(context);
    }

    public TextViewFaSolid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDefaultTypeFace(context);
    }

    public TextViewFaSolid(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaultTypeFace(context);
    }

    private void setDefaultTypeFace(Context context) {
        this.setTypeface(FontManager.getTypeFace(context, FontManager.faSolid900));
    }
}