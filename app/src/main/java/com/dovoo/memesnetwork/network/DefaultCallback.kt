package com.dovoo.memesnetwork.network

import androidx.lifecycle.MutableLiveData
import com.dovoo.memesnetwork.model.ErrorBean
import com.dovoo.memesnetwork.model.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DefaultCallback<T>(protected val data: MutableLiveData<Resource<T>>) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        try {
            if (call.isCanceled) {
                return
            }
            if (response.isSuccessful) {
                val resp = response.body()
                data.postValue(Resource.success(resp))

            } else {
                val error = ErrorBean(response.errorBody().toString())
                data.postValue(Resource.error(error))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val error = ErrorBean(e.message)
            data.postValue(Resource.error(error))
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        val error = ErrorBean(t.message)
        data.postValue(Resource.error(error))
    }
}