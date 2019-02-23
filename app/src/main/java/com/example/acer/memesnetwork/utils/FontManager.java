package com.example.acer.memesnetwork.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;

public class FontManager {

    public static final String faBrands400 = "fonts/fa_brands_400.otf";
    public static final String faRegular400 = "fonts/fa_regular_400.otf";
    public static final String faSolid900 = "fonts/fa_solid_900.otf";

    public static Typeface getTypeFace(Context context, String font){
        return Typeface.createFromAsset(context.getAssets(),font);
    }

    public static int getPxFromDp(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}