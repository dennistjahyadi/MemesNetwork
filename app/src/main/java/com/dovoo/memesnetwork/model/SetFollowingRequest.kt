package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SetFollowingRequest(
    val user_id: Int,
    val following_user_id: Int
): Parcelable {
}