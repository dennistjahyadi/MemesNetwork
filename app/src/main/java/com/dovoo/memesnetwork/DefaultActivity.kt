package com.dovoo.memesnetwork

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*

class DefaultActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val ACTIVITY_RESULT_SECTION = 2
    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        FirebaseApp.initializeApp(this)
        storage = Firebase.storage("gs://memes-network-1554020980788.appspot.com")

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

        MobileAds.initialize(this, resources.getString(R.string.admob_app_id))
    }



    fun uploadProfilePicUri(uri: Uri){
        // Create a storage reference from our app
        val storageRef = storage.reference

        val profilePicRef = storageRef.child("profilepicture/"+UUID.randomUUID())

        var uploadTask = profilePicRef.putFile(uri)
        uploadTask.addOnFailureListener {

        }.addOnSuccessListener { taskSnapshot ->

        }

    }

}