package com.dovoo.memesnetwork

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.model.Notification
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*


class DefaultActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val ACTIVITY_RESULT_SECTION = 2
    lateinit var storage: FirebaseStorage
    val generalViewModel: GeneralViewModel by viewModels()
    lateinit var navHostFragment: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        navHostFragment = findViewById(R.id.nav_host_fragment)

        FirebaseApp.initializeApp(this)
        storage = Firebase.storage("gs://memes-network-1554020980788.appspot.com")
//        storage = FirebaseStorage.getInstance()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@DefaultActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1123
            )
        }
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        MobileAds.initialize(
            this
        ) {

        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            if (GlobalFunc.isLogin(applicationContext)) {
                val userId = GlobalFunc.getLoggedInUserId(applicationContext)
                try {
                    generalViewModel.setFirebaseToken(userId, token!!)
                }catch (ex: Exception){}
            }
        })
    }

    override fun onStart() {
        super.onStart()
        checkIfThroughNotification()
    }

    private fun checkIfThroughNotification(){
        val title = intent.getStringExtra("title")
        val messages = intent.getStringExtra("messages")
        val iconUrl = intent.getStringExtra("iconUrl")
        val notifType = intent.getStringExtra("notifType")
        val memeId = intent.getStringExtra("memeId")
        val userId = intent.getStringExtra("userId")
        val commentId = intent.getStringExtra("commentId")

        when {
            notifType.equals(Notification.TYPE_FOLLOWING) -> {
                try {
                    GlobalFunc.minNotifCount(this)
                    val bundle = bundleOf("user_id" to userId!!.toInt())
                    navHostFragment.findNavController().navigate(R.id.userFragment, bundle)
                } catch (ex: Exception) {
                }
            }
            notifType.equals(Notification.TYPE_MEME_COMMENT) -> {
                try {
                    GlobalFunc.minNotifCount(this)
                    getMeme(memeId!!.toInt(), true)
                } catch (ex: Exception) {
                }
            }
            notifType.equals(Notification.TYPE_SUB_COMMENT) -> {
                try {
                    GlobalFunc.minNotifCount(this)
                    getComment(commentId!!.toInt())
                } catch (ex: Exception) {
                }
            }
            notifType.equals(Notification.TYPE_MEME_LIKED) -> {
                try {
                    GlobalFunc.minNotifCount(this)
                    getMeme(memeId!!.toInt(), false)
                } catch (ex: Exception) {
                }
            }
        }
        intent.removeExtra("notifType")
        intent.removeExtra("memeId")
        intent.removeExtra("userId")
        intent.removeExtra("commentId")
        intent.removeExtra("title")
        intent.removeExtra("messages")
        intent.removeExtra("iconUrl")
    }

    private fun getComment(commentId: Int) {
        generalViewModel.getComment(commentId).observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    val comment = it.data?.comment
                    comment?.current_datetime = it.data?.current_datetime
                    val bundle = bundleOf("main_comment" to comment)
                    navHostFragment.findNavController()
                        .navigate(R.id.commentDetailsFragment, bundle)
                }
            }
        })
    }

    private fun getMeme(memeId: Int,defaultCommentPage: Boolean) {

        generalViewModel.getMeme(GlobalFunc.getLoggedInUserId(applicationContext), memeId).observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    try {
                        val bundle = bundleOf(
                            "item" to DirectLinkItemTest(it.data!!.meme),
                            "defaultCommentPage" to defaultCommentPage
                        )
                        navHostFragment.findNavController().navigate(
                            R.id.memesDetailFragment,
                            bundle
                        )
                    } catch (ex: Exception) {
                    }
                }
            }
        })
    }


}