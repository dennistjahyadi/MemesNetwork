package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.adapter.FilterRecyclerViewAdapter
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentFilterBinding
import com.dovoo.memesnetwork.model.Section
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.AdUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel

class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: FilterRecyclerViewAdapter
    val listSections: ArrayList<Section> = ArrayList()
    val generalViewModel: GeneralViewModel by viewModels()

    val sectionClickListener = View.OnClickListener {
        var data = (it.tag as FilterRecyclerViewAdapter.MyViewHolder).data
        findNavController().previousBackStackEntry?.savedStateHandle?.set("selectedSection", data.name)
        findNavController().popBackStack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = FilterRecyclerViewAdapter(requireContext(), listSections, sectionClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        AdUtils.loadAds(requireContext(), binding.adView)
        binding.rvSection.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvSection.layoutManager = layoutManager

        val listener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchSection(totalItemsCount)
            }
        }
        binding.rvSection.removeOnScrollListener(listener)
        binding.rvSection.addOnScrollListener(listener)
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        generalViewModel.sections.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.sections?.forEach { section ->
                        listSections.add(section)
                    }
                    adapter.notifyDataSetChanged()
                    if(binding.etSearch.text.isNullOrEmpty()) binding.lblTopSections.visibility = View.VISIBLE
                    else binding.lblTopSections.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
        binding.ivSearch.setOnClickListener {
            fetchSection(0)
        }

        if(listSections.isEmpty()) fetchSection(0)

        return binding.root
    }

    private fun fetchSection(offset: Int) {
        if (offset == 0) listSections.clear()
        generalViewModel.fetchSections(offset, null, binding.etSearch.text.toString())
    }


}