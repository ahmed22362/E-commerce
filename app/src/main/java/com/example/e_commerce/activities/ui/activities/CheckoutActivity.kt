package com.example.e_commerce.activities.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.Address
import com.example.e_commerce.activities.models.CartItem
import com.example.e_commerce.activities.models.Order
import com.example.e_commerce.activities.models.Product
import com.example.e_commerce.activities.ui.adapters.CartListAdapter
import com.example.e_commerce.activities.utils.Constants
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartList:ArrayList<CartItem>
    private var mSubTotal: Double =0.0
    private var mTotal: Double =0.0
    private lateinit var mOrderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionBar()
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }
        if(mAddressDetails!= null){
            checkout_address_type.text = mAddressDetails?.type
            checkout_full_name_tv.text = mAddressDetails?.name
            checkout_mobile_number_tv.text = mAddressDetails?.mobileNumber
            checkout_additional_note_tv.text = mAddressDetails?.additionalNote

            if(mAddressDetails?.otherDetails!!.isNotEmpty()){
                checkout_other_details_tv.text = mAddressDetails?.otherDetails
            }
        }
        getAllProducts()

        checkout_place_order_btn.setOnClickListener {
            placeOrder()
        }
    }

    private fun getAllProducts(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAllProductsList(this)
    }

    fun getAllProductSuccess(list: ArrayList<Product>){
        mProductList = list
        getCartItemList()
    }
    private fun getCartItemList(){
        FireStoreClass().getCartItems(this)
    }
    fun getCartItemSuccess(cartList: ArrayList<CartItem>){

        mCartList = cartList

        for (product in mProductList){
            for(item in cartList){
                if (product.product_id == item.product_id){
                    item.stock_quantity = product.quantity
                }
            }
        }
        hideProgressDialog()
        checkout_cart_list_items_rv.layoutManager = LinearLayoutManager(this)
        checkout_cart_list_items_rv.setHasFixedSize(true)
        val adapter = CartListAdapter(this, mCartList, false)
        checkout_cart_list_items_rv.adapter = adapter

        for (item in mCartList){
            val availableQuantity =item.stock_quantity.toInt()
            if (availableQuantity>0){
                val price = item.price.toInt()
                val quantity = item.cart_quantity.toInt()
                mSubTotal +=(price*quantity)
            }
            checkout_sub_total_tv.text = mSubTotal.toString()
            if (mSubTotal>0){
                ll_checkout_place_order.visibility = View.VISIBLE
                mTotal = mSubTotal+10
                tv_checkout_total_charge_tv.text = mTotal.toString()

            }else{
                ll_checkout_place_order.visibility = View.GONE
            }

        }

    }

    fun placeOrder(){
        mOrderDetails = Order(FireStoreClass().getUserID(),
        mCartList,
        mAddressDetails!!,
        "My Order ${System.currentTimeMillis()}",
            mCartList[0].image,
            mSubTotal.toString(),
            "10.0$",
            mTotal.toString(),
            System.currentTimeMillis()
            )
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().placeOrder(this , mOrderDetails)
    }


    fun cartItemsUpdateSuccess(){
        hideProgressDialog()
        val intent = Intent(this , DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        Toast.makeText(this , "your Oder Placed Successfully", Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()
    }

    fun placeOrderSuccess(){
        FireStoreClass().updateCheckoutItems(this , mCartList, mOrderDetails)
    }
    fun setupActionBar(){
        setSupportActionBar(checkout_toolbar)
        val actionBar = supportActionBar
        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        checkout_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}