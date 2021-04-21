package com.dovoo.memesnetwork.network

import com.dovoo.memesnetwork.model.MemesResponse
import com.dovoo.memesnetwork.model.SectionResponse
import retrofit2.Call
import retrofit2.http.*

interface MemesRestService {

    @GET("v1/fetch-memes")
    fun fetchMemes(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int,
        @Query("post_section") postSection: String?
    ): Call<MemesResponse>

    @GET("v1/fetch-sections")
    fun fetchSections(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int?,
        @Query("filter") filter: String?
    ): Call<SectionResponse>

    @POST("v1/login")
    fun login()

}