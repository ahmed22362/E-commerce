package com.example.e_commerce.activities.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.Product
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.IOException

class AddProductActivity : BaseActivity() , View.OnClickListener {

    private var mSelectedImageUri: Uri? = null
    private  lateinit var mImageUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setUpActionbar()

        add_product_update_iv.setOnClickListener(this)
        add_product_submit_btn.setOnClickListener(this)
    }




    override fun onClick(v: View?) {
        if (v!= null){
            when(v.id){
                R.id.add_product_update_iv ->{
                    if(ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE)==
                        PackageManager.PERMISSION_GRANTED){

                    Constants.showImageChooser(this)
                    }else{
                        ActivityCompat.requestPermissions(this , arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE) ,
                            Constants.READ_STORAGE_PERMISSION_REQUEST_CODE)
                    }
                }

                R.id.add_product_submit_btn ->{


                    if(validateProductDetails()){
                        UploadImageToStorage()
                    }
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

        if(requestCode==Constants.READ_STORAGE_PERMISSION_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this ,resources.getString(R.string.read_storage_permission_denide),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data != null){
                    add_product_update_iv.setImageDrawable(ContextCompat.getDrawable(this , R.drawable.ic_vector_edit))

                    mSelectedImageUri = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(mSelectedImageUri!!, add_product_image_iv)
                    }catch (e: IOException){
                        Toast.makeText(this ,"Image Selection Failed",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("TAG", "onActivityResult: request canceld")
        }
    }

    fun setUpActionbar(){

        setSupportActionBar(add_product_toolbar)
        val actionBar = supportActionBar
        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        add_product_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun UploadImageToStorage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().uploadImageToCloudStorage(this , mSelectedImageUri ,Constants.PRODUCT_IMAGE)

    }


    fun imageUploadedSuccessfully(imageURL: String){
        mImageUrl = imageURL
        addProduct()
//        showErrorSnackBar("Product image is uploaded Successfully $imageURL" , false)
    }

    fun addProduct(){
        val userId = getSharedPreferences(Constants.MY_SHOP_PAL_PREFERENCE ,Context.MODE_PRIVATE).
        getString(Constants.LOGGED_IN_USERNAME , "")!!

        val title = add_product_title_et.text.toString().trim{it <= ' '}
        val description = add_product_description_et.text.toString().trim{it <=' '}
        val price = add_product_price_et.text.toString().trim{it<=' '}
        val quantity = add_product_quantity_et.text.toString().trim { it<=' ' }
        val product = Product(FireStoreClass().getUserID() , userId, title , price,quantity,description,mImageUrl)

        FireStoreClass().addProduct(this ,product)

    }

    private fun validateProductDetails(): Boolean{

        return when {
            mSelectedImageUri == null->{
                showErrorSnackBar(resources.getString(R.string.image_error) , true)
                false
            }
            TextUtils.isEmpty(add_product_title_et.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.title_error) , true)
                false
            }
            TextUtils.isEmpty(add_product_price_et.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.price_error) , true)
                false
            }
            TextUtils.isEmpty(add_product_description_et.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.desciprion_error) , true)
                false
            }
            TextUtils.isEmpty(add_product_quantity_et.text.toString().trim{it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.quantity_error) , true)
                false
            }
            else -> true
        }
    }

    fun productAddedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this,"your product added successfully" , Toast.LENGTH_SHORT).show()
        finish()
    }

}