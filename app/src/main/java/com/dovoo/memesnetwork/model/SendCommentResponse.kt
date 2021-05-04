package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class SendCommentResponse(
    val data: Comment,
): BaseResponse() {
}