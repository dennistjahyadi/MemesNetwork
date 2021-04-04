package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MemesResponse(
    var memes: ArrayList<Memes>
): BaseResponse() {
}