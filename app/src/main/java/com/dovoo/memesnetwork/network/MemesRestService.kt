package com.dovoo.memesnetwork.network

import com.dovoo.memesnetwork.model.MemesResponse
import retrofit2.Call
import retrofit2.http.*

interface MemesRestService {

    @GET("v1/fetch-memes")
    open fun fetchMemes(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int,
        @Query("post_section") postSection: String?
    ): Call<MemesResponse>

}