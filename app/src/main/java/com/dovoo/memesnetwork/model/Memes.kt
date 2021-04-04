package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Memes(
    var id: Int,
    var code: String,
    var title: String,
    var type: String,
    var images: MemesImage,
    var post_section: String,
    var tags: ArrayList<MemesTag>,
    var created_at: String,
    var updated_at: String,
    var total_like: Int,
    var total_comment: Int,
    var is_liked: Int?
): Parcelable {
    @Parcelize
    class MemesImage(
        var image700: Image700,
        var image460sv: Image460sv
    ):Parcelable

    @Parcelize
    class Image700(
        var width: Int,
        var height: Int,
        var url: String,
        var webpUrl: String?
    ):Parcelable

    @Parcelize
    class Image460sv(
        var width: Int,
        var height: Int,
        var url: String,
        var webUrl: String,
        var hasAudio: Int,
        var duration: Int,
    ):Parcelable

    @Parcelize
    class MemesTag(
        var key: String,
        var url: String
    ):Parcelable


}