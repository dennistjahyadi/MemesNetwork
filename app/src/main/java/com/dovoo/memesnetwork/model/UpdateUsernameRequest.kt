package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UpdateUsernameRequest(
    var userId: Int,
    var username: String
):Parcelable {
}