package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class BaseResponse(
    var status: String = "",
    var message: String = ""
): Parcelable {
}