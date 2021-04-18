package com.dovoo.memesnetwork.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dovoo.memesnetwork.model.MemesResponse
import com.dovoo.memesnetwork.model.Resource
import com.dovoo.memesnetwork.model.SectionResponse
import com.dovoo.memesnetwork.network.DefaultCallback
import com.dovoo.memesnetwork.network.MemesRestAdapter


class GeneralViewModel : ViewModel() {

    private val adapter = MemesRestAdapter.apiRestService

    var memesHome = MutableLiveData<Resource<MemesResponse>>()

    var sections = MutableLiveData<Resource<SectionResponse>>()

    fun fetchMemesHome(offset: Int, userId: Int, postSection: String?) {
        // Do an asynchronous operation to fetch users.
        val call = adapter.fetchMemes(offset, userId, postSection)
        call.enqueue(DefaultCallback(memesHome))
    }

    fun fetchSections(offset: Int, limit: Int?, filter: String?){
        val call = adapter.fetchSections(offset, limit, filter)
        call.enqueue(DefaultCallback(sections))
    }

}