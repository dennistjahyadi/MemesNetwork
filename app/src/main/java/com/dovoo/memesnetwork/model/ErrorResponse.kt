package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
open class ErrorResponse(
    var mUnparseable: Boolean = true,
    var mThrowable: Throwable? = null,
) : BaseResponse() {
    constructor(t: Throwable?) : this() {
        mThrowable = t
        mUnparseable = false
    }

    constructor(msg: String?) : this() {
        message = msg ?: ""
    }
}
