package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class UserOtherResponse(
    val user: UserOtherDetails,
    val total_memes: Int = 0,
    val total_following: Int = 0,
    val total_follower: Int = 0,
    val is_following: Boolean = false
): BaseResponse() {
}