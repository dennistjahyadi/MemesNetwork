package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class GetCommentResponse(
    var comment: Comment,
    var current_datetime: String?,
) : BaseResponse() {
}