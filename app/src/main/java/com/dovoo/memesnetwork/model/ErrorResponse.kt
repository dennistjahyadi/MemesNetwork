package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class ErrorResponse(
    var mUnparseable: Boolean = true,
    var mThrowable: Throwable? = null,
    var message: String? = null
) : BaseResponse() {
    constructor(t: Throwable) : this() {
        mThrowable = t
        mUnparseable = false
    }
    constructor(msg: String?) : this(){
        message = msg
    }
}
