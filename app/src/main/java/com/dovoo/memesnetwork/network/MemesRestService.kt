package com.dovoo.memesnetwork.network

import com.dovoo.memesnetwork.model.*
import retrofit2.Call
import retrofit2.http.*

interface MemesRestService {

    @GET("v1/fetch-memes")
    fun fetchMemes(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int,
        @Query("post_section") postSection: String?
    ): Call<MemesResponse>

    @GET("v1/fetch-mymemes")
    fun fetchMyMemes(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int,
        @Query("post_section") postSection: String?
    ): Call<MemesResponse>

    @GET("v1/fetch-liked-memes")
    fun fetchLikedMemes(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int): Call<MemesResponse>

    @GET("v1/fetch-comments")
    fun fetchComments(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int?,
        @Query("meme_id") memeId: Int?,
        @Query("sort") sort: String?
    ): Call<CommentResponse>

    @GET("v1/fetch-sections")
    fun fetchSections(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int?,
        @Query("filter") filter: String?
    ): Call<SectionResponse>

    @POST("v1/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("v1/update-username")
    fun updateUsername(@Body request: UpdateUsernameRequest): Call<UpdateUsernameResponse>


    @POST("v1/update-profile-pic")
    fun updateProfilePic(@Body request: UpdateProfilePicRequest): Call<UpdateProfilePicResponse>

    @POST("v1/insert-likes")
    fun insertLike(@Body request: InsertLikeRequest): Call<InsertLikeResponse>

    @POST("v1/insert-comments")
    fun sendComments(@Body request: SendCommentRequest): Call<SendCommentResponse>
}