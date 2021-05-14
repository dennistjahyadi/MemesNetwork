package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class InsertMemesRequest(
    val user_id: Int,
    val desc: String?,
    val is_photo: Boolean,
    val data: String,
    val post_section: String
): Parcelable {
}