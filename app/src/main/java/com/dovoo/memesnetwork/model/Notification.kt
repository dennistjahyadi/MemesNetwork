package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Notification(
    var id: Int,
    var user_id_dest: Int,
    var user_id_from: Int,
    var meme_id: Int?,
    var current_comment_id: Int?,
    var main_comment_id: Int?,
    var messages: String,
    var type: String,
    var created_at: String,
    var updated_at: String,
    var user_from_obj: User?,
    var meme_obj: Memes?,
    var is_following: Int?,
    var main_comment_obj: Comment?,
    var current_comment_obj: Comment?
): Parcelable {

    companion object{
        val TYPE_FOLLOWING = "following"
        val TYPE_SUB_COMMENT = "sub_comment"
        val TYPE_MEME_COMMENT = "meme_comment"
    }
}