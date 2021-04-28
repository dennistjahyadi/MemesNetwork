package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dovoo.memesnetwork.databinding.FragmentMyMemesBinding
import com.dovoo.memesnetwork.databinding.FragmentProfileBinding

class MyMemesFragment: Fragment() {
    private var _binding: FragmentMyMemesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyMemesBinding.inflate(inflater, container, false)

        return binding.root
    }
}