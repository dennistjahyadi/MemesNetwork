package com.dovoo.memesnetwork.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentLoginBinding
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.removeAllPrefs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONException
import org.json.JSONObject

class LoginFragment: Fragment(), View.OnClickListener {
    private val RC_SIGN_IN = 1

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GlobalFunc.mGoogleSignInClient
        binding.signInButton.setOnClickListener(this)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        updateUI(account)
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            // user is log in
            syncDatabase(account)
        } else {
            // user not log in
            removeAllPrefs(requireContext())
        }
    }

    private fun syncDatabase(account: GoogleSignInAccount) {
        val email = account.email
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
//        AndroidNetworking.post(BuildConfig.API_URL + "syncusers")
//                .addJSONObjectBody(jsonObject)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                        try {
//                            JSONObject data = response.getJSONObject("data");
//
//
//                            if (data.isNull("username") || data.getString("username").equals("")) {
//                                Intent i = new Intent(getApplicationContext(), ChooseUsernameActivity.class);
//                                i.putExtra("email", email);
//                                startActivity(i);
//                            } else {
//                                SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_NAME, data.getString("username"));
//                                SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_ID, data.getInt("id"));
//                                SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_EMAIL, email);
//                                SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, true);
//                                Toast.makeText(getApplicationContext(), "Welcome " + data.getString("username") + " :)", Toast.LENGTH_LONG).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        finish();
//
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        // handle error
//                        System.out.print("error");
//                    }
//                });
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