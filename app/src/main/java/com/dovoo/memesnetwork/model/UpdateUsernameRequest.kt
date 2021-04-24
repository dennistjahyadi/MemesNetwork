package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UpdateUsernameRequest(
    var user_id: Int,
    var username: String
):Parcelable {
}