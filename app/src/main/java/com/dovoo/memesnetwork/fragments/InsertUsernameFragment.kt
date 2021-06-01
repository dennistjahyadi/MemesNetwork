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
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

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

        binding.btnOk.setOnClickListener {
            updateUsername()
        }
        return binding.root
    }

    private fun updateUsernameListener() {
        generalViewModel.updateUsernameListener.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(
                                    "TAG",
                                    "Fetching FCM registration token failed",
                                    task.exception
                                )
                                return@OnCompleteListener
                            }

                            // Get new FCM registration token
                            val token = task.result
                            if (GlobalFunc.isLogin(requireContext())) {
                                val userId = GlobalFunc.getLoggedInUserId(requireContext())
                                try {
                                    generalViewModel.setFirebaseToken(userId, token!!)
                                }catch (ex: Exception){}
                            }
                        })
                    SharedPreferenceUtils.insertUsernamePrefs(
                        requireContext(),
                        it.data?.user?.username
                    )
                    Toast.makeText(
                        requireContext(),
                        "Welcome ${it.data?.user?.username} :)",
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().getBackStackEntry(R.id.mainFragment).savedStateHandle.set(
                        "loginSuccess",
                        true
                    )
                    findNavController().popBackStack(R.id.mainFragment, false)
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun updateUsername() {
        val userId = getPrefs(requireContext()).getInt(
            SharedPreferenceUtils.PREFERENCES_USER_ID,
            -1
        )
        generalViewModel.updateUsername(userId, binding.etUsername.text.toString())
    }
}