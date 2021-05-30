package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class GetMemeResponse(
    var meme: Memes
) : BaseResponse() {
}