package com.dovoo.memesnetwork.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class SetFollowingResponse(
    var data: FollowingData
): BaseResponse() {
}