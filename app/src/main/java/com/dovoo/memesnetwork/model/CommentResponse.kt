package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class CommentResponse(
    var comments: List<Comment>
    ): BaseResponse() {
}