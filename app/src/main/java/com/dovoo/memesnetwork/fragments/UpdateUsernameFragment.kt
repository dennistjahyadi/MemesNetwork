package com.dovoo.memesnetwork.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentInsertUsernameBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.google.firebase.messaging.FirebaseMessaging

class UpdateUsernameFragment : Fragment() {

    private var _binding: FragmentInsertUsernameBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsertUsernameBinding.inflate(inflater, container, false)
        updateUsernameListener()
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnOk.setOnClickListener {
            updateUsername()
        }
        return binding.root
    }

    private fun updateUsernameListener() {
        generalViewModel.updateUsernameListener.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    SharedPreferenceUtils.insertUsernamePrefs(
                        requireContext(),
                        it.data?.user?.username
                    )
                    Toast.makeText(
                        requireContext(),
                        "Update complete ${it.data?.user?.username} :)",
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().popBackStack()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun updateUsername() {
        val userId = SharedPreferenceUtils.getPrefs(requireContext()).getInt(
            SharedPreferenceUtils.PREFERENCES_USER_ID,
            -1
        )
        generalViewModel.updateUsername(userId, binding.etUsername.text.toString())
    }
}