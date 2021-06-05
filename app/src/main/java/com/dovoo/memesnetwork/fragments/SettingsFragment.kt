package com.dovoo.memesnetwork.fragments

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dovoo.memesnetwork.DefaultActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentSettingsBinding
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.swNotifFollowing.isChecked = SharedPreferenceUtils.isEnableNotifFollowing(
            requireContext()
        )
        binding.swNotifMemesComment.isChecked = SharedPreferenceUtils.isEnableNotifMemesComment(
            requireContext()
        )
        binding.swNotifCommentReply.isChecked = SharedPreferenceUtils.isEnableNotifCommentReply(
            requireContext()
        )
        binding.swNotifMemeLiked.isChecked = SharedPreferenceUtils.isEnableNotifMemesLiked(
            requireContext()
        )
        binding.swNotifFollowing.setOnCheckedChangeListener { _, b ->
            SharedPreferenceUtils.setNotifFollowing(requireContext(), b)
        }
        binding.swNotifMemesComment.setOnCheckedChangeListener { _, b ->
            SharedPreferenceUtils.setNotifMemesComment(requireContext(), b)
        }
        binding.swNotifCommentReply.setOnCheckedChangeListener { _, b ->
            SharedPreferenceUtils.setNotifCommentReply(requireContext(), b)
        }
        binding.swNotifMemeLiked.setOnCheckedChangeListener { _, b ->
            SharedPreferenceUtils.setNotifMemeLiked(requireContext(), b)
        }

        binding.tvBtnPrivacyPolicy.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(browserIntent)
        }
        binding.tvBtnLogout.setOnClickListener {
            showDialogLogout()
        }
        return binding.root
    }

    private fun doGoogleLogout() {
        (activity as DefaultActivity).mGoogleSignInClient.signOut()
            .addOnCompleteListener(activity as DefaultActivity) {
                if (it.isComplete) {
                    SharedPreferenceUtils.removeUserPrefs(requireContext())
                    findNavController().popBackStack()
                }
            }
    }

    fun showDialogLogout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.logout_msg))
            .setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                doGoogleLogout()
            }
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }
}