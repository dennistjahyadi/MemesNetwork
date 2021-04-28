package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dovoo.memesnetwork.databinding.FragmentMyCommentsBinding

class MyCommentsFragment: Fragment() {
    private var _binding: FragmentMyCommentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCommentsBinding.inflate(inflater, container, false)

        return binding.root
    }
}