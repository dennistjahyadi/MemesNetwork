package com.dovoo.memesnetwork.model

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
    var memes: ArrayList<Memes>
) {

}