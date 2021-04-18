package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Section(
    var id: Int,
    var name: String,
    var images: String?,
    var created_at: String?,
    var updated_at: String?,
):Parcelable {
}