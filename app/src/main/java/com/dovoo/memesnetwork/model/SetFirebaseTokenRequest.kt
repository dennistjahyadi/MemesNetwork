package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SetFirebaseTokenRequest(
    val user_id: Int?,
    val token: String
): Parcelable {
}