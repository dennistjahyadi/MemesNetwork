package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.databinding.FragmentUserBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.model.UserOtherDetails
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val userId by lazy {
        arguments?.getInt("user_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        getUser()
        return binding.root
    }

    private fun getUser() {

        generalViewModel.getUser(userId).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    updateUI(it.data!!.user)
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun updateUI(user: UserOtherDetails){
        binding.tvUsername.text = user.username
        Glide.with(requireContext())
            .load(user.photo_url)
            .into(binding.ivProfile)
    }
}