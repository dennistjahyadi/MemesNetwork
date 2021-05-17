package com.dovoo.memesnetwork.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dovoo.memesnetwork.model.*
import com.dovoo.memesnetwork.network.DefaultCallback
import com.dovoo.memesnetwork.network.MemesRestAdapter


class GeneralViewModel : ViewModel() {

    var currentUser: User? = null

    private val adapter = MemesRestAdapter.apiRestService

    var memesHome = MutableLiveData<Resource<MemesResponse>>()

    var userMemes = MutableLiveData<Resource<MemesResponse>>()

    var likedMemes = MutableLiveData<Resource<MemesResponse>>()

    var sections = MutableLiveData<Resource<SectionResponse>>()

    var updateUsernameListener = MutableLiveData<Resource<UpdateUsernameResponse>>()

    var loginListener = MutableLiveData<Resource<LoginResponse>>()

    fun fetchMemesHome(offset: Int, userId: Int, postSection: String?) {
        // Do an asynchronous operation to fetch users.
        val call = adapter.fetchMemes(offset, userId, postSection)
        call.enqueue(DefaultCallback(memesHome))
    }

    fun fetchUserMemes(offset: Int, userId: Int, postSection: String?) {
        // Do an asynchronous operation to fetch users.
        val call = adapter.fetchMyMemes(offset, userId, postSection)
        call.enqueue(DefaultCallback(userMemes))
    }

    fun fetchLikedMemes(offset: Int, userId: Int) {
        val call = adapter.fetchLikedMemes(offset, userId)
        call.enqueue(DefaultCallback(likedMemes))
    }

    fun fetchComments(
        offset: Int,
        userId: Int?,
        memeId: Int?,
        commentId: Int?,
        sort: String?
    ): MutableLiveData<Resource<CommentResponse>> {
        val call = adapter.fetchComments(offset, userId, memeId, commentId, sort)
        var commentResponse = MutableLiveData<Resource<CommentResponse>>()
        call.enqueue(DefaultCallback(commentResponse))
        return commentResponse
    }

    fun fetchMainComments(
        offset: Int,
        userId: Int?,
        memeId: Int?,
        commentId: Int?,
        sort: String?
    ): MutableLiveData<Resource<CommentResponse>> {
        val call = adapter.fetchMainComments(offset, userId, memeId, commentId, sort)
        var commentResponse = MutableLiveData<Resource<CommentResponse>>()
        call.enqueue(DefaultCallback(commentResponse))
        return commentResponse
    }

    fun fetchSections(offset: Int, limit: Int?, filter: String?) {
        val call = adapter.fetchSections(offset, limit, filter)
        call.enqueue(DefaultCallback(sections))
    }

    fun fetchTopSections(filter: String?):MutableLiveData<Resource<SectionResponse>> {
        val topSections = MutableLiveData<Resource<SectionResponse>>()
        val call = adapter.fetchTopSections(filter)
        call.enqueue(DefaultCallback(topSections))
        return topSections
    }

    fun login(email: String) {
        val loginRequest = LoginRequest(email)
        val call = adapter.login(loginRequest)
        call.enqueue(DefaultCallback(loginListener))
    }

    fun updateUsername(userId: Int, username: String) {
        val updateUsernameRequest = UpdateUsernameRequest(userId, username)
        val call = adapter.updateUsername(updateUsernameRequest)
        call.enqueue(DefaultCallback(updateUsernameListener))
    }

    fun insertLike(
        memeId: Int,
        userId: Int,
        liked: Int
    ): MutableLiveData<Resource<InsertLikeResponse>> {
        val insertLikeRequest = InsertLikeRequest(memeId, userId, liked)
        val call = adapter.insertLike(insertLikeRequest)
        val insertLikeListener = MutableLiveData<Resource<InsertLikeResponse>>()
        call.enqueue(DefaultCallback(insertLikeListener))
        return insertLikeListener
    }

    fun updateProfilePic(
        userId: Int,
        url: String
    ): MutableLiveData<Resource<UpdateProfilePicResponse>> {
        val updateProfilePicRequest = UpdateProfilePicRequest(userId, url)
        val call = adapter.updateProfilePic(updateProfilePicRequest)
        val updateProfilePicListener = MutableLiveData<Resource<UpdateProfilePicResponse>>()
        call.enqueue(DefaultCallback(updateProfilePicListener))
        return updateProfilePicListener
    }

    fun sendComment(
        memeId: Int,
        userId: Int,
        messages: String,
        commentId: Int?
    ): MutableLiveData<Resource<SendCommentResponse>> {
        val request = SendCommentRequest(memeId, userId, messages, commentId)
        val call = adapter.sendComments(request)
        val listener = MutableLiveData<Resource<SendCommentResponse>>()
        call.enqueue(DefaultCallback(listener))
        return listener
    }

    fun insertMemes(
        user_id: Int,
        desc: String?,
        is_photo: Boolean,
        post_section: String,
        dataJson: String
    ): MutableLiveData<Resource<InsertMemesResponse>> {
        val request = InsertMemesRequest(user_id, desc, is_photo, dataJson, post_section)
        val call = adapter.insertMemes(request)
        val listener = MutableLiveData<Resource<InsertMemesResponse>>()
        call.enqueue(DefaultCallback(listener))
        return listener
    }

    fun getUser(userId: Int): MutableLiveData<Resource<UserOtherResponse>>{
        val call = adapter.getUser(userId)
        val listener = MutableLiveData<Resource<UserOtherResponse>>()
        call.enqueue(DefaultCallback(listener))
        return listener
    }

}