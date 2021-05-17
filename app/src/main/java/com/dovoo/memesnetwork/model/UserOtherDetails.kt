package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserOtherDetails(
    val id: Int,
    var username: String?,
    val email: String,
    var email_verified_at: String?,
    var password: String?,
    var photo_url: String?,
    var remember_token: String?,
    var created_at: String?,
    var updated_at: String?,
    var memes: ArrayList<Memes>,
    var following_user: ArrayList<User>,
    var follower_user: ArrayList<User>
): Parcelable {

}