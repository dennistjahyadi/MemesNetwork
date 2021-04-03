package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.dovoo.memesnetwork.MainActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.MemesRecyclerViewAdapter
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.components.MyLinearLayoutManager
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.squareup.picasso.Picasso
import im.ene.toro.widget.Container
import im.ene.toro.widget.PressablePlayerSelector
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainFragment : Fragment() {

    private var section: String? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var container: Container
    private lateinit var layoutManager: MyLinearLayoutManager
    private lateinit var adapter: MemesRecyclerViewAdapter
    private lateinit var selector: PressablePlayerSelector
    private val directLinkItemTestList: ArrayList<DirectLinkItemTest> = ArrayList()
    val generalViewModel: GeneralViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, viewGroup, false)

        container = view.findViewById(R.id.player_container)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        layoutManager = MyLinearLayoutManager(context)

        selector = PressablePlayerSelector(container)
        container.layoutManager = layoutManager
        container.playerSelector = selector

        adapter = MemesRecyclerViewAdapter(
            context,
            selector,
            directLinkItemTestList,
            (activity as MainActivity?)!!.loadingBar
        )
        container.adapter = adapter
        container.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchData(totalItemsCount)
            }
        })
        swipeRefreshLayout.setOnRefreshListener(
            OnRefreshListener {
                swipeRefreshLayout.setVisibility(View.GONE)
                fetchData(0)
            }
        )
        generalViewModel.memesHome.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.memes

                    // do anything with response
                    try {
//                        for (i in 0 until it.data?.memes.size()!! ) {
//                            val result: JSONObject = response.getJSONObject(i)
//                            val meme =
//                            val id = result.getInt("id")
//                            val title = result.getString("title")
//                            val type = result.getString("type")
//                            val imagesObject = JSONObject(result.getString("images"))
//                            val coverUrl = imagesObject.getJSONObject("image700").getString("url")
//                            val category = result.getString("post_section")
//                            var videoUrl: String? = null
//                            var isVideo = false
//                            var hasAudio = false
//                            if (type.equals("animated", ignoreCase = true)) {
//                                isVideo = true
//                                videoUrl = imagesObject.getJSONObject("image460sv").getString("url")
//                                hasAudio = if (imagesObject.getJSONObject("image460sv")
//                                        .getInt("hasAudio") == 1
//                                ) true else false
//                            }
//                            val width = imagesObject.getJSONObject("image700").getInt("width")
//                            val height = imagesObject.getJSONObject("image700").getInt("height")
//                            val data: MutableMap<String, Any> = HashMap()
//                            data["total_like"] = result["total_like"]
//                            data["total_dislike"] = result["total_dislike"]
//                            data["total_comment"] = result["total_comment"]
//                            data["is_liked"] = result["is_liked"]
//                            directLinkItemTestList.add(
//                                DirectLinkItemTest(
//                                    id,
//                                    category,
//                                    title,
//                                    videoUrl,
//                                    data,
//                                    Picasso.get(),
//                                    coverUrl,
//                                    width,
//                                    height,
//                                    hasAudio,
//                                    isVideo
//                                )
//                            )
//                        }
//                        adapter.notifyDataSetChanged()
//                        swipeRefreshLayout.isEnabled = true
//                        swipeRefreshLayout.isRefreshing = false
//                        swipeRefreshLayout.visibility = View.VISIBLE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                Status.ERROR -> {

                }
            }
        })


        return view
    }

    private fun fetchData(offset: Int) {
        if (offset == 0) {
            directLinkItemTestList.clear()
        }
        val userId = getPrefs(requireContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0)
        generalViewModel.fetchMemesHome(offset, userId, section)
    }

    override fun onDestroyView() {
//        layoutManager = null
//        adapter = null
//        selector = null
        super.onDestroyView()
    }

}