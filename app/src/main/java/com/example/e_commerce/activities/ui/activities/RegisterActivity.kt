package com.example.e_commerce.activities.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.User
import com.example.e_commerce.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //hide notePar
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        binding.registerLoginTv.setOnClickListener{
            onBackPressed()
        }

        binding.redgisterRegisterBtn.setOnClickListener {
            registerUser()
        }

    }



    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun setupActionBar() {

        setSupportActionBar(binding.registerToolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.registerToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        val firstName = findViewById<EditText>(R.id.register_first_name_et)
        return when {
            TextUtils.isEmpty(firstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                firstName.setError(resources.getString(R.string.err_msg_enter_first_name))
                false
            }

            TextUtils.isEmpty(binding.registerSecondNameEt.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)

                false
            }

            TextUtils.isEmpty(binding.registerEmailEt.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.registerPasswordEt.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(binding.registerConfirmPasswordEt.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            binding.registerPasswordEt.text.toString().trim { it <= ' ' } != binding.registerConfirmPasswordEt.text.toString()
                    .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !binding.registerTermCheckbox.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }

    private fun registerUser(){
        if(validateRegisterDetails()){

            showProgressDialog("Please Wait")

            val email    :String = binding.registerEmailEt.text.toString().trim{it<=' '}
            val password :String = binding.registerPasswordEt.text.toString().trim{it <=' '}


            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(OnCompleteListener <AuthResult>{task ->

                    if(task.isSuccessful){
                        val firebaseUser :FirebaseUser = task.result!!.user!!
                        showErrorSnackBar("you register successfully with id ${firebaseUser.uid}",false)
                        val user = User(firebaseUser.uid,binding.registerFirstNameEt.text.toString(),
                                            binding.registerSecondNameEt.text.toString(),
                                            binding.registerEmailEt.text.toString())

                        FireStoreClass().registerUser(this , user)
                    }
                    else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                })

                }
        }


    fun userRegistrationSuccess(){
        hideProgressDialog()
        Toast.makeText(this ,resources.getString(R.string.register_success), Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

}