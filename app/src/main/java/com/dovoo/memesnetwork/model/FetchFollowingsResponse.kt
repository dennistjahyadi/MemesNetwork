package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class FetchFollowingsResponse(
    val followings: List<FollowingData>
):BaseResponse() {
}