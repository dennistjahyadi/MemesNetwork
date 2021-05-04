package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SendCommentRequest(
    val meme_id: Int,
    val user_id: Int,
    val messages: String,
    val comment_id: Int?
): Parcelable {
}