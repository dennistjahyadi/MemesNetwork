package com.dovoo.memesnetwork

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.dovoo.memesnetwork.activities.ProfileActivity
import com.dovoo.memesnetwork.activities.SectionActivity
import com.dovoo.memesnetwork.fragments.NewestMemesFragment
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1
    private val ACTIVITY_RESULT_SECTION = 2
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val fragmentList: MutableList<Fragment> = ArrayList()
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var linBtnSection: LinearLayout? = null
    private var tvBtnProfile: TextView? = null
    var loadingBar: FrameLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
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

       // MobileAds.initialize(this, resources.getString(R.string.admob_app_id))
    }

    private fun checkUserLoggedIn() {
        val isUserLoggedIn = getPrefs(applicationContext).getBoolean(
            SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
            false
        )
        val headerView = navigationView!!.getHeaderView(0)
        val clSignedIn: ConstraintLayout = headerView.findViewById(R.id.clSignedIn)
        val clSignedOut: ConstraintLayout = headerView.findViewById(R.id.clSignedOut)
        val ivProfile = headerView.findViewById<ImageView>(R.id.ivProfile)
        val navUsername = headerView.findViewById<TextView>(R.id.tvName)
        if (isUserLoggedIn) {
            clSignedIn.visibility = View.VISIBLE
            clSignedOut.visibility = View.GONE
            val username = getPrefs(applicationContext).getString(
                SharedPreferenceUtils.PREFERENCES_USER_NAME,
                ""
            )
            if (username == "") {
                // run choose username activity
            }
            navUsername.text = username
        } else {
            clSignedIn.visibility = View.GONE
            clSignedOut.visibility = View.VISIBLE
        }
    }

    private fun firstLayout() {
        val fragment: Fragment = NewestMemesFragment()
        val fragmentManager = supportFragmentManager // For A
        if (fragment != null) {
            fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit()
        }
        navigationView!!.menu.getItem(0).isChecked = true
    }

    private fun addFragments() {
        fragmentList.add(NewestMemesFragment())
        //  fragmentList.add(new NewestMemesFragment());
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_RESULT_SECTION && resultCode == RESULT_OK) {
            val section = data!!.getStringExtra("name")
            val fragment: Fragment = NewestMemesFragment()
            val arguments = Bundle()
            arguments.putString("section", section)
            fragment.arguments = arguments
            val fragmentManager = supportFragmentManager // For A
            if (fragment != null) {
                fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage("exit :( ?")
            .setPositiveButton("Yes") { dialog, id -> finish() }
            .setNegativeButton("No") { dialog, id -> }
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }
}