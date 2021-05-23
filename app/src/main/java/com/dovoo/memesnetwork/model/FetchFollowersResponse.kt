package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class FetchFollowersResponse(
    val followers: List<FollowingData>
):BaseResponse() {
}