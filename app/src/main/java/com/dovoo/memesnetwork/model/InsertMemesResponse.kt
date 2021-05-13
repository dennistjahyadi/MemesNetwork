package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class InsertMemesResponse(
    val data: Memes
) : BaseResponse() {
}