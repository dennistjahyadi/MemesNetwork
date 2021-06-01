package com.dovoo.memesnetwork.network

import com.dovoo.memesnetwork.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MemesRestService {

    @GET("v1/fetch-memes")
    fun fetchMemes(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int,
        @Query("post_section") postSection: String?
    ): Call<MemesResponse>

    @GET("v1/fetch-memes-just-following")
    fun fetchMemesFollowing(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int
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
        @Query("user_id") userId: Int
    ): Call<MemesResponse>

    @GET("v1/fetch-main-comments")
    fun fetchMainComments(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int?,
        @Query("meme_id") memeId: Int?,
        @Query("comment_id") commentId: Int?,
        @Query("sort") sort: String?
    ): Call<CommentResponse>

    @GET("v1/fetch-comments")
    fun fetchComments(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int?,
        @Query("meme_id") memeId: Int?,
        @Query("comment_id") commentId: Int?,
        @Query("sort") sort: String?
    ): Call<CommentResponse>

    @GET("v1/fetch-sections")
    fun fetchSections(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int?,
        @Query("filter") filter: String?
    ): Call<SectionResponse>


    @GET("v1/fetch-top-sections")
    fun fetchTopSections(
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

    @POST("v1/insert-memes")
    fun insertMemes(@Body request: InsertMemesRequest): Call<InsertMemesResponse>

    @GET("v1/get-user")
    fun getUser(
        @Query("user_id") userId: Int
    ): Call<UserOtherResponse>

    @POST("v1/set-following")
    fun setFollowing(@Body request: SetFollowingRequest): Call<SetFollowingResponse>

    @GET("v1/fetch-followings")
    fun fetchFollowings(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int
    ): Call<FetchFollowingsResponse>

    @GET("v1/fetch-followers")
    fun fetchFollowers(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int
    ): Call<FetchFollowersResponse>

    @GET("v1/fetch-notifications")
    fun fetchNotifications(
        @Query("offset") offset: Int,
        @Query("user_id") userId: Int
    ): Call<FetchNotificationsResponse>

    @POST("v1/set-firebase-token")
    fun setFirebaseToken(@Body request: SetFirebaseTokenRequest): Call<BaseResponse>

    @GET("v1/get-meme")
    fun getMeme(
        @Query("meme_id") memeId: Int
    ): Call<GetMemeResponse>

    @GET("v1/get-comment")
    fun getComment(
        @Query("comment_id") commentId: Int
    ): Call<GetCommentResponse>
}