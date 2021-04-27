package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class UpdateProfilePicResponse (
    val user: User
):BaseResponse(){
}