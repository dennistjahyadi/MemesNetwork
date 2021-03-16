package com.dovoo.memesnetwork.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.dovoo.memesnetwork.utils.FontManager;

public class TextViewFaRegular extends AppCompatTextView {
    public TextViewFaRegular(Context context) {
        super(context);
        setDefaultTypeFace(context);
    }

    public TextViewFaRegular(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDefaultTypeFace(context);
    }

    public TextViewFaRegular(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaultTypeFace(context);
    }

    private void setDefaultTypeFace(Context context) {
        this.setTypeface(FontManager.getTypeFace(context, FontManager.faRegular400));
    }
}