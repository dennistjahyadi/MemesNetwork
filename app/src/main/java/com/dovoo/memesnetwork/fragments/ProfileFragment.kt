package com.dovoo.memesnetwork.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dovoo.memesnetwork.DefaultActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentProfileBinding
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.google.android.gms.tasks.OnCompleteListener


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        checkUsername()
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvBtnLogout.setOnClickListener {
            doGoogleLogout()
        }

        return binding.root
    }

    private fun checkUsername(){
        val username = SharedPreferenceUtils.getPrefs(requireContext()).getString(SharedPreferenceUtils.PREFERENCES_USER_NAME, null)
        if(username.isNullOrEmpty()){
            findNavController().navigate(R.id.action_profileFragment_to_insertUsernameFragment)
        }
    }

    private fun doGoogleLogout() {

        (activity as DefaultActivity).mGoogleSignInClient.signOut()
            .addOnCompleteListener(activity as DefaultActivity, OnCompleteListener<Void?> {
                if (it.isComplete) {
                    showDialogLogout()
                }
            })
    }

    fun showDialogLogout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.logout_msg))
            .setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                SharedPreferenceUtils.removeUserPrefs(requireContext())
                findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }
}