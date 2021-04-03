package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class Status: Parcelable {
    SUCCESS,
    ERROR,
    LOADING,
    CACHE,
    INIT
}