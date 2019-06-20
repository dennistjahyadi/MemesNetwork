package com.dovoo.memesnetwork.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.dovoo.memesnetwork.utils.FontManager;

public class TextViewChlorinr extends AppCompatTextView {
    public TextViewChlorinr(Context context) {
        super(context);
        setDefaultTypeFace(context);
    }

    public TextViewChlorinr(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDefaultTypeFace(context);
    }

    public TextViewChlorinr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaultTypeFace(context);
    }

    private void setDefaultTypeFace(Context context) {
        this.setTypeface(FontManager.getTypeFace(context, FontManager.chlorinr));
    }
}