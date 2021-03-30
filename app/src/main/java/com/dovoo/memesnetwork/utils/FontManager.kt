package com.dovoo.memesnetwork.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface

object FontManager {
    const val faBrands400 = "fonts/fa_brands_400.otf"
    const val faRegular400 = "fonts/fa_regular_400.otf"
    const val faSolid900 = "fonts/fa_solid_900.otf"
    const val chlorinr = "fonts/chlorinr.ttf"
    @JvmStatic
    fun getTypeFace(context: Context, font: String?): Typeface {
        return Typeface.createFromAsset(context.assets, font)
    }

    fun getPxFromDp(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}