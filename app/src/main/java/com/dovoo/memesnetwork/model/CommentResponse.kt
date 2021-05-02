package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class CommentResponse(
    var comments: List<Comment>,
    var current_datetime: String
): BaseResponse() {
}