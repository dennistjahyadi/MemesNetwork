package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.dovoo.memesnetwork.DefaultActivity
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
            requireContext(),
            selector,
            directLinkItemTestList,
            FrameLayout(requireContext())
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

                    // do anything with response
                    try {
                        it.data?.memes?.forEach { meme ->
                            val directLinkItem = DirectLinkItemTest(meme, Picasso.get())
                            directLinkItemTestList.add(directLinkItem)
                        }
                        adapter.notifyDataSetChanged()
                        swipeRefreshLayout.isEnabled = true
                        swipeRefreshLayout.isRefreshing = false
                        swipeRefreshLayout.visibility = View.VISIBLE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                Status.ERROR -> {
                    System.out.println(it.error?.message)
                }
            }
        })
        fetchData(0)

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