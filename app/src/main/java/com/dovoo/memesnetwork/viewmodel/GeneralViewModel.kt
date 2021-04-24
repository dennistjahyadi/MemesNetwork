package com.dovoo.memesnetwork.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dovoo.memesnetwork.model.*
import com.dovoo.memesnetwork.network.DefaultCallback
import com.dovoo.memesnetwork.network.MemesRestAdapter


class GeneralViewModel : ViewModel() {

    private val adapter = MemesRestAdapter.apiRestService

    var memesHome = MutableLiveData<Resource<MemesResponse>>()

    var sections = MutableLiveData<Resource<SectionResponse>>()

    var updateUsernameListener = MutableLiveData<Resource<UpdateUsernameResponse>>()

    var loginListener = MutableLiveData<Resource<LoginResponse>>()

    fun fetchMemesHome(offset: Int, userId: Int, postSection: String?) {
        // Do an asynchronous operation to fetch users.
        val call = adapter.fetchMemes(offset, userId, postSection)
        call.enqueue(DefaultCallback(memesHome))
    }

    fun fetchSections(offset: Int, limit: Int?, filter: String?){
        val call = adapter.fetchSections(offset, limit, filter)
        call.enqueue(DefaultCallback(sections))
    }

    fun login(email: String){
        val loginRequest = LoginRequest(email)
        val call = adapter.login(loginRequest)
        call.enqueue(DefaultCallback(loginListener))
    }

    fun updateUsername(userId: Int, username: String){
        val updateUsernameRequest = UpdateUsernameRequest(userId, username)
        val call = adapter.updateUsername(updateUsernameRequest)
        call.enqueue(DefaultCallback(updateUsernameListener))
    }

    fun insertLike(memeId: Int, userId: Int, liked: Int): MutableLiveData<Resource<InsertLikeResponse>>{
        val insertLikeRequest = InsertLikeRequest(memeId, userId, liked)
        val call = adapter.insertLike(insertLikeRequest)
        var insertLikeListener = MutableLiveData<Resource<InsertLikeResponse>>()
        call.enqueue(DefaultCallback(insertLikeListener))
        return insertLikeListener
    }

}