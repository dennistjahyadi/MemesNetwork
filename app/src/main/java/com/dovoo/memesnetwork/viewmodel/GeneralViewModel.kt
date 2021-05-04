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

    var myMemes = MutableLiveData<Resource<MemesResponse>>()

    var likedMemes = MutableLiveData<Resource<MemesResponse>>()


    var sections = MutableLiveData<Resource<SectionResponse>>()

    var updateUsernameListener = MutableLiveData<Resource<UpdateUsernameResponse>>()

    var loginListener = MutableLiveData<Resource<LoginResponse>>()

    fun fetchMemesHome(offset: Int, userId: Int, postSection: String?) {
        // Do an asynchronous operation to fetch users.
        val call = adapter.fetchMemes(offset, userId, postSection)
        call.enqueue(DefaultCallback(memesHome))
    }

    fun fetchMyMemes(offset: Int, userId: Int, postSection: String?) {
        // Do an asynchronous operation to fetch users.
        val call = adapter.fetchMyMemes(offset, userId, postSection)
        call.enqueue(DefaultCallback(myMemes))
    }

    fun fetchLikedMemes(offset: Int, userId: Int) {
        val call = adapter.fetchLikedMemes(offset, userId)
        call.enqueue(DefaultCallback(likedMemes))
    }

    fun fetchComments(
        offset: Int,
        userId: Int?,
        memeId: Int?
    ): MutableLiveData<Resource<CommentResponse>> {
        val call = adapter.fetchComments(offset, userId, memeId)
        var commentResponse = MutableLiveData<Resource<CommentResponse>>()
        call.enqueue(DefaultCallback(commentResponse))
        return commentResponse
    }

    fun fetchSections(offset: Int, limit: Int?, filter: String?) {
        val call = adapter.fetchSections(offset, limit, filter)
        call.enqueue(DefaultCallback(sections))
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

}