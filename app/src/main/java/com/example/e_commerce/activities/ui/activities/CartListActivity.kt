package com.example.e_commerce.activities.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.CartItem
import com.example.e_commerce.activities.models.Product
import com.example.e_commerce.activities.ui.adapters.CartListAdapter
import com.example.e_commerce.activities.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {

    private lateinit var mProductList:ArrayList<Product>
    private lateinit var mCartListItems:ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        setupActionBar()

        btn_checkout.setOnClickListener {
            val intent = Intent(this , AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS , true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getAllProducts()
    }


    fun successGettingCartList(list: ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductList){
            for(cartItem in list){
                if (product.product_id==cartItem.product_id){
                    cartItem.stock_quantity == product.quantity

                    if(product.quantity.toInt() == 0 ){
                        cartItem.cart_quantity = product.quantity
                    }
                }
            }
        }

        mCartListItems = list
        if(mCartListItems.size>0){
            cart_list_rv.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE
            ll_checkout.visibility = View.VISIBLE

            cart_list_rv.layoutManager = LinearLayoutManager(this)
            val adapter = CartListAdapter(this , mCartListItems, true)
            cart_list_rv.adapter = adapter
            var total: Double = 0.0

            for(i in mCartListItems){
                val availableQuantity = i.stock_quantity.toInt()

                if (availableQuantity> 0){
                val price = i.price.toDouble()
                val quantity: Int = i.cart_quantity.toInt()
                total +=(price*quantity)
                }
            }
            tv_sub_total.text = "$$total"
            tv_shipping_charge.text = "$10.0"

            if(total>0){
                ll_checkout.visibility = View.VISIBLE
                val total = total + 10
                tv_total_amount.text = "$$total"
            }else{
                ll_checkout.visibility = View.GONE
            }

        }else{
            cart_list_rv.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
            ll_checkout.visibility = View.GONE
        }

    }

    fun successfullyDeleteItemFrmCart(){
        hideProgressDialog()
        Toast.makeText(this , "your item deleted successfully", Toast.LENGTH_SHORT).show()
        getCartListItems()
    }

    fun successGettingAllProducts(productList: ArrayList<Product>){
        hideProgressDialog()
        mProductList = productList
        getCartListItems()
    }

    fun successfullyUpdateItem(){
        hideProgressDialog()
        getCartListItems()
    }
    private fun getCartListItems(){
        FireStoreClass().getCartItems(this)
    }

    private fun getAllProducts(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAllProductsList(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(cart_list_toolbar)
        val actionBar= supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        cart_list_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}