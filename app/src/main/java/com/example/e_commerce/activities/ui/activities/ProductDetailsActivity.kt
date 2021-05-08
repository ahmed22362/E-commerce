package com.example.e_commerce.activities.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.solver.widgets.analyzer.VerticalWidgetRun
import androidx.core.content.ContextCompat
import androidx.lifecycle.whenResumed
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.CartItem
import com.example.e_commerce.activities.models.Product
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_product_details.view.*

class ProductDetailsActivity : BaseActivity() {
    private var mProductId: String =""
    private var mOwnerProduct: String=""
    private lateinit var mProductDetails: Product


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
           mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_USER_NAME)){
            mOwnerProduct = intent.getStringExtra(Constants.EXTRA_PRODUCT_USER_NAME)!!
        }
        if (mOwnerProduct ==FireStoreClass().getUserID()){
            product_details_add_to_cart_btn.visibility = View.GONE
            product_details_go_to_cart_btn.visibility = View.GONE
        }else{
            product_details_add_to_cart_btn.visibility = View.VISIBLE
        }


        product_details_save_btn.setOnClickListener {
            updateProductDetails()
        }

        product_details_add_to_cart_btn.setOnClickListener {
            addToCart()
        }

        product_details_go_to_cart_btn.setOnClickListener {
            startActivity(Intent(this , CartListActivity::class.java))
        }


        go_to_login_btn.setOnClickListener {
            startActivity(Intent(this , LoginActivity::class.java))
        }

        getProductDetails()

    }


    fun addToCart(){
        val addedItem =  CartItem(FireStoreClass().getUserID(),
                mProductId,
                mOwnerProduct,
                mProductDetails.title,
                mProductDetails.description,
                mProductDetails.price,
                mProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY,
                mProductDetails.quantity
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addItemToCart(this , addedItem)
    }
    fun getProductDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getProductDetails(this , mProductId)
    }

    fun productExistInCart(){
        hideProgressDialog()
        product_details_add_to_cart_btn.visibility= View.GONE
        product_details_go_to_cart_btn.visibility = View.VISIBLE
        Log.e("TAG", "productExistInCart: i am gere" )

    }

    fun productDetailsSuccess(product:Product){
        mProductDetails = product

        GlideLoader(this).loadProductPicture(product.image , iv_product_detail_image)
        tv_product_details_title.setText(product.title)
        tv_product_details_price.setText("$${product.price}")
        tv_product_details_description.setText(product.description)
        tv_product_details_stock_quantity.setText(product.quantity)

        if(product.quantity.toInt() ==0){
            hideProgressDialog()
            product_details_add_to_cart_btn.visibility = View.GONE
            tv_product_details_stock_quantity.setText(R.string.lbl_out_of_stock)
            tv_product_details_stock_quantity.setTextColor(ContextCompat.getColor(this , R.color.ColorSnackBarError))
        }else{
            if(FireStoreClass().getUserID()== product.user_id ){
                hideProgressDialog()
            }else{
                FireStoreClass().checkIfItemExistInCart(this , mProductId)
            }
        }
    }

    private fun updateProductDetails(){
        val productHashMap= HashMap<String,Any>()
        if(valid()){
            showProgressDialog(resources.getString(R.string.please_wait))
            val title = tv_product_details_title.text.toString().trim{it <=' '}
            val price = tv_product_details_price.text.toString().trim{it <=' '}
            val description = tv_product_details_description.text.toString().trim{it <= ' '}
            val quantity = tv_product_details_stock_quantity.text.toString().trim{it <=' '}

            productHashMap[Constants.TITLE] = title
            productHashMap[Constants.PRICE] = price
            productHashMap[Constants.DESCRIPTION] = description
            productHashMap[Constants.PRODUCT_QUANTITY]= quantity

            Log.e("TAG", "updateProductDetails: here" )
            FireStoreClass().updateProduct(this , productHashMap , mProductId)

        }
    }





    private fun valid() :Boolean{
    return  when {
            TextUtils.isEmpty(tv_product_details_title.toString().trim{it <=' '})->{
                showErrorSnackBar("please enter title" , true)
                false
            }
            TextUtils.isEmpty(tv_product_details_price.toString().trim{it <=' '})->{
                showErrorSnackBar("please enter Price" , true)
                false
            }
            TextUtils.isEmpty(tv_product_details_description.toString().trim{it <= ' '})->{
                showErrorSnackBar("please enter description" , true)
                false
            }
            TextUtils.isEmpty(tv_product_details_stock_quantity.toString().trim{it <=' '})->{
                showErrorSnackBar("please enter quantity number" , true)
                false
            }
        else -> {
            true
        }

        }
    }

    fun successProductDetails(){
        hideProgressDialog()
        Toast.makeText(this , "product uploaded successfully" , Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    fun itemAddedSuccessful(){
        hideProgressDialog()
        Toast.makeText(this, "your item added successfully" , Toast.LENGTH_SHORT).show()
        product_details_add_to_cart_btn.visibility = View.GONE
        product_details_go_to_cart_btn.visibility = View.VISIBLE
    }

    private fun setupActionBar(){
        setSupportActionBar(product_details_toolbar)
        val actionbar = supportActionBar

        if(actionbar!= null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        product_details_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (intent.hasExtra(Constants.SHOWEDITMENU)){
            return false
        }else
            menuInflater.inflate(R.menu.edit_product_details_menu , menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.action_edit_details ->{
                product_details_save_btn.visibility = View.VISIBLE

                tv_product_details_title.isEnabled = true
                tv_product_details_price.isEnabled = true
                tv_product_details_description.isEnabled = true
                tv_product_details_stock_quantity.isEnabled = true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}