package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UpdateProfilePicRequest(
    val user_id: Int,
    val url: String
):Parcelable {
}