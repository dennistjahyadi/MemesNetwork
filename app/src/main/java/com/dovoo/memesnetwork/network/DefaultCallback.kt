package com.dovoo.memesnetwork.network

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.dovoo.memesnetwork.model.BaseResponse
import com.dovoo.memesnetwork.model.ErrorResponse
import com.dovoo.memesnetwork.model.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DefaultCallback<T: BaseResponse>(protected val data: MutableLiveData<Resource<T>>) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        try {
            if (call.isCanceled) {
                return
            }
            if (response.isSuccessful) {
                val resp = response.body()
                data.postValue(Resource.success(resp))

            } else {
                val error = ErrorResponse(response.errorBody().toString())
                data.postValue(Resource.error(error))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val error = ErrorResponse(e.cause)
            data.postValue(Resource.error(error))
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        val error = ErrorResponse(t)
        data.postValue(Resource.error(error))
    }
}