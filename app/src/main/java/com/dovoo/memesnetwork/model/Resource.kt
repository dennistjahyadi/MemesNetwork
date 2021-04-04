package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Resource<out T: Parcelable>(val status: Status, val data: T?, val error: ErrorResponse?) :
    Parcelable {
    companion object {
        fun <T : Parcelable> init(): Resource<T> {
            return Resource(Status.INIT, null, null)
        }

        fun <T : Parcelable> cache(data: T?): Resource<T> {
            return Resource(Status.CACHE, data, null)
        }

        fun <T: Parcelable> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T: Parcelable> error(error: ErrorResponse): Resource<T> {
            return Resource(Status.ERROR, null, error)
        }

        fun <T : Parcelable> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

}