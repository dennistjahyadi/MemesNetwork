package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class InsertLikeRequest(
    val meme_id: Int,
    val user_id: Int,
    val liked: Int
): Parcelable {
}