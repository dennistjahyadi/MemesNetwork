package com.dovoo.memesnetwork.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.dovoo.memesnetwork.DefaultActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentLoginBinding
import com.dovoo.memesnetwork.model.LoginResponse
import com.dovoo.memesnetwork.model.Resource
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.removeAllPrefs
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class LoginFragment : Fragment(), View.OnClickListener {
    private val RC_SIGN_IN = 1

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    lateinit var mGoogleSignInClient: GoogleSignInClient

    val generalViewModel: GeneralViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        mGoogleSignInClient = (activity as DefaultActivity).mGoogleSignInClient
        mGoogleSignInClient.revokeAccess()
        binding.signInButton.setOnClickListener(this)
        loginListener()
        onResult()
        return binding.root
    }

    fun onResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("insertUsernameSuccess")
            ?.observe(viewLifecycleOwner, {
                if (it) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "loginSuccess",
                        true
                    )
                    findNavController().popBackStack()
                }
            })
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            // user is log in
            syncDatabase(account)
        } else {
            // user not log in
            GlobalFunc.setLogout(requireContext())
            removeAllPrefs(requireContext())
        }
    }

    private fun loginListener() {
        generalViewModel.loginListener = MutableLiveData<Resource<LoginResponse>>()
        generalViewModel.loginListener.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.loading.loadingBar.visibility = View.GONE

                    it.data?.user?.let { user ->
                        generalViewModel.currentUser = user
                        SharedPreferenceUtils.saveUserPrefs(
                            requireContext(),
                            user.username,
                            user.id,
                            user.email,
                            user.photo_url
                        )
                        GlobalFunc.successLogin(requireContext())
                        if (user.username.isNullOrEmpty()) {
                            findNavController().navigate(R.id.action_loginFragment_to_insertUsernameFragment)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Welcome ${user.username} :)",
                                Toast.LENGTH_LONG
                            ).show()

                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                "loginSuccess",
                                true
                            )
                            findNavController().popBackStack()
                        }

                    }
                }
                Status.ERROR -> {
                    binding.loading.loadingBar.visibility = View.GONE
                    println("bbbbb: login: " + it.error?.message)
                }
            }
        })
    }

    private fun syncDatabase(account: GoogleSignInAccount) {
        val email = account.email
        email?.let {
            binding.loading.loadingBar.visibility = View.VISIBLE
            generalViewModel.login(email)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sign_in_button -> signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null)
        }
    }
}