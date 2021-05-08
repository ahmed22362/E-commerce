package com.example.e_commerce.activities.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.activities.models.Order
import com.example.e_commerce.activities.models.SoldProduct
import com.example.e_commerce.activities.ui.adapters.CartListAdapter
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_sold_product_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SoldProductDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_product_details)

        setupActionBar()

        var productDetails :SoldProduct= SoldProduct()

        if(intent.hasExtra(Constants.EXTRA_SOLD_PRODUCTS_DETAILS)){
            productDetails = intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCTS_DETAILS)!!
        }

        showSoldProductDetails(productDetails)
    }

    private fun showSoldProductDetails(productDetails : SoldProduct){
            tv_sold_product_details_id.text = productDetails.id

            // Date Format in which the date will be displayed in the UI.
            val dateFormat = "dd MMM yyyy HH:mm"
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = productDetails.order_date
            tv_sold_product_details_date.text = formatter.format(calendar.time)

            GlideLoader(this@SoldProductDetailsActivity).loadProductPicture(
                productDetails.image,
                iv_product_item_image
            )
            tv_product_item_name.text = productDetails.title
            tv_product_item_price.text ="$${productDetails.price}"
            tv_sold_product_quantity.text = productDetails.sold_quantity

            tv_sold_details_address_type.text = productDetails.address.type
            tv_sold_details_full_name.text = productDetails.address.name
            tv_sold_details_address.text =
                "${productDetails.address.address}, ${productDetails.address.zipCode}"
            tv_sold_details_additional_note.text = productDetails.address.additionalNote

            if (productDetails.address.otherDetails.isNotEmpty()) {
                tv_sold_details_other_details.visibility = View.VISIBLE
                tv_sold_details_other_details.text = productDetails.address.otherDetails
            } else {
                tv_sold_details_other_details.visibility = View.GONE
            }
            tv_sold_details_mobile_number.text = productDetails.address.mobileNumber

            tv_sold_product_sub_total.text = productDetails.sub_total_amount
            tv_sold_product_shipping_charge.text = productDetails.shipping_charge
            tv_sold_product_total_amount.text = productDetails.total_amount
        }

    fun setupActionBar(){
        setSupportActionBar(toolbar_sold_product_details_activity)
        val actionBar = supportActionBar
        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_sold_product_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}