package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class FetchUserResponse(
    val users: ArrayList<User>
): BaseResponse() {
}