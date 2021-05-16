package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class UserOtherResponse(
    val user: UserOtherDetails
): BaseResponse() {
}