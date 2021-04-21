package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dovoo.memesnetwork.databinding.FragmentInsertUsernameBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel

class InsertUsernameFragment : Fragment() {

    private var _binding: FragmentInsertUsernameBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInsertUsernameBinding.inflate(inflater, container, false)
        updateUsernameListener()
        return binding.root
    }

    private fun updateUsernameListener(){
        generalViewModel.updateUsernameListener.observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {

                }
                Status.ERROR -> {

                }
            }
        })
    }

    private fun updateUsername(){
        val userId = getPrefs(requireContext()).getInt(
            SharedPreferenceUtils.PREFERENCES_USER_ID,
            -1
        )
        generalViewModel.updateUsername(userId, binding.etUsername.text.toString())

    }
}