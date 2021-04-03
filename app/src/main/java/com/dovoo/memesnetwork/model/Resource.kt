package com.dovoo.memesnetwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Resource<out T>(val status: Status, val data: T?, val error: ErrorBean?) :
    Parcelable {
    companion object {
        fun <T : Parcelable> init(): Resource<T> {
            return Resource(Status.INIT, null, null)
        }

        fun <T : Parcelable> cache(data: T?): Resource<T> {
            return Resource(Status.CACHE, data, null)
        }

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: ErrorBean): Resource<T> {
            return Resource(Status.ERROR, null, error)
        }

        fun <T : Parcelable> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

}