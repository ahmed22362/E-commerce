package com.example.e_commerce.activities.ui.activities

import android.os.Bundle
import android.widget.Toast
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : BaseActivity() {
    private lateinit var binding :ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()

        binding.forgetPasswordSubmitBtn.setOnClickListener {
        val mail :String =binding.forgetPasswordMailEt.text.toString().trim(){ it <=' '}
         if(mail.isEmpty()){
            showErrorSnackBar("please enter vali mail" , true)

         }else{
             showProgressDialog(resources.getString(R.string.please_wait))

             FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener {task ->
                 hideProgressDialog()
                 if (task.isSuccessful){
                     Toast.makeText(this , "check your mail" , Toast.LENGTH_SHORT).show()
                     finish()
                 }else{
                     showErrorSnackBar(task.exception?.message.toString() , true)
                 }
         }

          }


        }

    }

    private fun setupActionBar(){
        setSupportActionBar(binding.forgetPasswordToolbar)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        binding.forgetPasswordToolbar.setNavigationOnClickListener{
            onBackPressed()
        }

    }
}