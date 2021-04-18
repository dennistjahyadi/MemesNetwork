package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dovoo.memesnetwork.adapter.FilterRecyclerViewAdapter
import com.dovoo.memesnetwork.databinding.FragmentFilterBinding

class FilterFragment: Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: FilterRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)


        return binding.root
    }

}