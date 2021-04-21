package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class LoginResponse(
    var user: User? = null
):BaseResponse() {
}