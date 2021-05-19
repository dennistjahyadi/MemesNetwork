package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FollowingData(
    var id: Int,
    var user_id: Int,
    var following_user_id: Int,
    var created_at: String,
    var updated_at: String
): Parcelable {
}