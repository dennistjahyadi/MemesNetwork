package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Comment(
    var id: Int,
    var meme_id: Int,
    var user_id: Int,
    var messages: String?,
    var comment_id: Int?,
    var created_at: String?,
    var updated_at: String?,
    var current_datetime: String?
): Parcelable {
}