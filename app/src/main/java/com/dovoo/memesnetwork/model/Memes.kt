package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.HashMap

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
    var is_liked: Int?,
    var user: User?
): Parcelable {
    @Parcelize
    class MemesImage(
        var image700: Image700,
        var image460sv: Image460sv?
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

    fun getData(): MutableMap<String, Any>{
        val mData: MutableMap<String, Any> = HashMap()
        mData["total_like"] = total_like
        mData["total_comment"] = total_comment
        mData["is_liked"] = is_liked?:0
        return mData
    }

    fun isVideo(): Boolean{
        if (type.equals("animated", ignoreCase = true)) {
            return true
        }
        return false
    }

    fun hasAudio(): Boolean{
        if (type.equals("animated", ignoreCase = true)) {
            return images.image460sv?.hasAudio == 1
        }
        return false
    }

}