package com.example.e_commerce.activities.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.User
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import com.example.e_commerce.databinding.ActivityUserProfileBinding
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException


class UserProfileActivity : BaseActivity() {

    private  lateinit var mUserDetails: User

    private var mSelectedImageUri: Uri? = null

    private var mUserProfileUrl: String = ""

    private lateinit var binding : ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityUserProfileBinding.inflate(layoutInflater)

        setTheme(R.style.Theme_AppCompat_NoActionBar)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mUserDetails = User()

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        profile_et_email.isEnabled = false
        profile_et_email.setText(mUserDetails.email)

        if(mUserDetails.profileComplete == 0 ){
            profile_tb_tv.text = resources.getString(R.string.title_complete_profile)
            // Here, the some of the edit text components are disabled because it is added at a time of Registration.
            profile_et_first_name.isEnabled = false
            profile_et_first_name.setText(mUserDetails.firstName)
            profile_et_second_name.isEnabled = false
            profile_et_second_name.setText(mUserDetails.lastName)
        }else{
            setupActionBar()

            profile_tb_tv.text = resources.getString(R.string.edit_profile)
            GlideLoader(this).loadUserPicture(mUserDetails.image ,profile_iv_user_profile )

            // Set the existing values to the UI and allow user to edit except the Email ID.
            profile_et_first_name.setText(mUserDetails.firstName)
            profile_et_second_name.setText(mUserDetails.lastName)

            if (mUserDetails.mobile != 0L) {
                profile_et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            } else {
                rb_female.isChecked = true
            }
        }


        binding.profileIvUserProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){

//                val galleryIntent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                startActivity(galleryIntent)

                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ,Constants.READ_STORAGE_PERMISSION_REQUEST_CODE)
            }

        }

        profile_btn_submit.setOnClickListener {


            if(validateUserProfileDetails()){
                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))

                if (mSelectedImageUri != null) {
                    FireStoreClass().uploadImageToCloudStorage(
                        this@UserProfileActivity,
                        mSelectedImageUri,
                        Constants.USER_PROFILE_IMAGE
                    )
                }
                else{
                    updateUserProfileDetails()
                }


            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                Toast.makeText(baseContext, "you granted permission ", Toast.LENGTH_SHORT).show()

                Constants.showImageChooser(this)
                }else{
                Toast.makeText(baseContext, "you denied permission ", Toast.LENGTH_SHORT).show()

            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {if (resultCode == Activity.RESULT_OK) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("hi", "onActivityResult: first "+ data.toString())
        Toast.makeText(this,"hi" , Toast.LENGTH_SHORT).show()
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            Log.e("i am here", "onActivityResult: "+data.toString() )
            if (data != null) {
                try {
                    // The uri of selected image from phone storage.
                    mSelectedImageUri = data.data!!

                    GlideLoader(this).loadUserPicture(mSelectedImageUri!!, profile_iv_user_profile)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@UserProfileActivity,
                        resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }else{
                Toast.makeText(this , "the data is null" , Toast.LENGTH_SHORT).show()
            }
        }
    } else if (resultCode == Activity.RESULT_CANCELED) {
        // A log is printed when user close or cancel the image selection.
        Log.e("Request Cancelled", "Image selection cancelled")
    }
  }


    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(profile_et_mobile_number.text.toString().trim{it <= ' '})->{
                showErrorSnackBar("Enter mobile number" , true)
                false
            }
            else->{
                true
            }
        }
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String , Any>()

        val firstName = profile_et_first_name.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        // Get the LastName from editText and trim the space
        val lastName = profile_et_second_name.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = profile_et_mobile_number.text.toString().trim{it <=' '}

        val gender = if (rb_male.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }
        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        if(mobileNumber.isNotEmpty()&& mobileNumber!= Constants.MOBILE){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if(mUserProfileUrl.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileUrl
        }
        if (mUserDetails.profileComplete == 0) {
            userHashMap[Constants.COMPLETED_PROFILE] = 1
        }

        FireStoreClass().updateUserDetails(this , userHashMap)

    }

    fun  userUpdateProfileSuccessfully(){
        hideProgressDialog()
        Toast.makeText(
                this@UserProfileActivity,
                resources.getString(R.string.msg_profile_update_success),
                Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }


    fun imageUploadedSuccess(imageURL: String){
        mUserProfileUrl = imageURL
        updateUserProfileDetails()
    }

    private fun setupActionBar() {

        setSupportActionBar(profile_toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        profile_toolbar.setNavigationOnClickListener { onBackPressed() }
    }
    // END
}