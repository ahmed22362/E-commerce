package com.example.e_commerce.activities.ui.activities

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.e_commerce.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    private lateinit var mDialog : Dialog

    private var firstBackButtonStatue = false

    fun showErrorSnackBar(message : String , errorMessage: Boolean){
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message,Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if(errorMessage){
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this,R.color.ColorSnackBarError)
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this,R.color.ColorSnackBarSuccess)
            )
        }
        snackBar.show()
    }

    fun showProgressDialog(text: String){
        mDialog = Dialog(this)
        mDialog.setContentView(R.layout.dialog_progress)
        mDialog.setCancelable(false)
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.show()
    }

    fun hideProgressDialog(){
        mDialog.dismiss()
    }


    fun setupStatueBar(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    fun doubleBackToExit(){
        if (firstBackButtonStatue){
            super.onBackPressed()
        }
        firstBackButtonStatue = true

        Toast.makeText(this,"Press back again to exit" , Toast.LENGTH_SHORT).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({firstBackButtonStatue = false} , 2000)
    }

}