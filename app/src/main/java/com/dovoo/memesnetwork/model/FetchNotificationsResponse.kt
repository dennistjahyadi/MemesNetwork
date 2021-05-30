package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class FetchNotificationsResponse(
    val notifications: ArrayList<Notification>,
    val current_datetime: String
):BaseResponse() {
}