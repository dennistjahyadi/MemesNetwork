package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.MemesRecyclerViewAdapter
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.components.MyLinearLayoutManager
import com.dovoo.memesnetwork.databinding.FragmentMainBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.squareup.picasso.Picasso
import im.ene.toro.widget.PressablePlayerSelector
import org.json.JSONException
import java.util.*

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var selectedSection: String? = null
    private lateinit var layoutManager: MyLinearLayoutManager
    private lateinit var adapter: MemesRecyclerViewAdapter
    private lateinit var selector: PressablePlayerSelector
    private val directLinkItemTestList: ArrayList<DirectLinkItemTest> = ArrayList()
    val generalViewModel: GeneralViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, viewGroup, false)

        layoutManager = MyLinearLayoutManager(context)

        selector = PressablePlayerSelector(binding.playerContainer)
        binding.playerContainer.layoutManager = layoutManager
        binding.playerContainer.playerSelector = selector

        adapter = MemesRecyclerViewAdapter(
            requireContext(),
            selector,
            directLinkItemTestList,
            FrameLayout(requireContext())
        )
        binding.playerContainer.adapter = adapter
        binding.playerContainer.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchData(totalItemsCount)
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.visibility = View.GONE
            fetchData(0)
        }
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
                        binding.swipeRefreshLayout.isEnabled = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.swipeRefreshLayout.visibility = View.VISIBLE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                Status.ERROR -> {
                    println("BBBBB: "+it.error?.message)
                }
                else -> {}
            }
        })
        binding.includeToolbar.ivBtnProfile.setOnClickListener {
//            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addMemeFragment)
        }

        binding.includeToolbar.linBtnFilter.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_filterFragment)
        }

        if(directLinkItemTestList.isEmpty()) fetchData(0)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedSection")?.observe(viewLifecycleOwner, {
            selectedSection = it
            fetchData(0)
        })

        return binding.root
    }

    private fun fetchData(offset: Int) {
        if (offset == 0) {
            directLinkItemTestList.clear()
        }
        val userId = getPrefs(requireContext()).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0)
        generalViewModel.fetchMemesHome(offset, userId, selectedSection)
    }

    override fun onDestroyView() {
//        layoutManager = null
//        adapter = null
//        selector = null
        super.onDestroyView()
    }

}