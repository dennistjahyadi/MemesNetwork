package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class UpdateUsernameResponse(
    val user: User
):BaseResponse(){
}