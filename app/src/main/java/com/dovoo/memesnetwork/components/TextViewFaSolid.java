package com.dovoo.memesnetwork.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.dovoo.memesnetwork.utils.FontManager;

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