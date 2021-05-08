package com.example.e_commerce.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.User
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupActionBar()

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this , LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        tv_edit.setOnClickListener {
            val intent = Intent(this , UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS , mUserDetails)
            startActivity(intent)
        }

        ll_address.setOnClickListener {
            val intent = Intent(this , AddressListActivity::class.java)
            startActivity(intent)
        }
    }



    fun setupActionBar(){
        setSupportActionBar(toolbar_settings_activity)
        val actionBar = supportActionBar

        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun getUserDetails(){

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User){
        hideProgressDialog()
        mUserDetails = user
        GlideLoader(this).loadUserPicture(user.image , setting_user_image_iv)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_email.text = user.email
        tv_gender.text = user.gender
        tv_mobile_number.text = user.mobile.toString()
    }


    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
}