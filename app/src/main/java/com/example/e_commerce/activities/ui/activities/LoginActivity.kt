package com.example.e_commerce.activities.ui.activities

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.User
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {

        private lateinit var binding :ActivityLoginBinding

        override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setTheme(R.style.Theme_AppCompat_NoActionBar)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

            // This is used to hide the status bar and make the login screen as a full screen activity.
            // It is deprecated in the API level 30. I will update you with the alternate solution soon.
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        binding.loginRegisterTv.setOnClickListener{
            val intent = Intent(this , RegisterActivity::class.java)
            startActivity(intent)
        }


        binding.signinBtn.setOnClickListener{
            logInRegisteredUser()
        }

        binding.tvForgetPassword.setOnClickListener {
            val intent = Intent(this , ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space
            val email = binding.loginMailEt.text.toString().trim { it <= ' ' }
            val password = binding.loginPasswordEt.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            FireStoreClass().getUserDetails(this)
                            showErrorSnackBar("You are logged in successfully.", false)
                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
        }
    }
    /**
     * A function to validate the login entries of a user.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.loginMailEt.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.loginPasswordEt.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun userLoggedInSuccessfully(user: User){
        hideProgressDialog()
        if(user.profileComplete==0){
            val intent = Intent(this ,
                UserProfileActivity::class.java )
                    .putExtra(Constants.EXTRA_USER_DETAILS , user)
            startActivity(intent)
        }else{
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        finish()
    }
}