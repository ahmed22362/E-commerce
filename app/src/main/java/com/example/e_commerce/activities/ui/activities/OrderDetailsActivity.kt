package com.example.e_commerce.activities.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.activities.models.Order
import com.example.e_commerce.activities.ui.adapters.CartListAdapter
import com.example.e_commerce.activities.utils.Constants
import kotlinx.android.synthetic.main.activity_order_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        setupActionbar()
        var orderDetails: Order = Order()
        if(intent.hasExtra(Constants.EXTRA_ORDER_DETAILS)){
            orderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_ORDER_DETAILS)!!
        }
        showOrderDetails(orderDetails)
    }



    private fun showOrderDetails(orderDetails:Order){

        order_details_orderId_tv.text = orderDetails.title

        val dateFormat= "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat , Locale.getDefault())
        val calender: Calendar = Calendar.getInstance()
        calender.timeInMillis = orderDetails.order_datetime
        val orderDate = formatter.format(calender.time)

        order_details_order_date_tv.text = orderDate
        val diffInMillSecond = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMillSecond)
        Log.e("TAG", "showOrderDetails: $diffInHours" )
        when{
            diffInHours < 1 -> {
                order_details_order_statue_tv.text = resources.getString(R.string.order_status_pending)
                order_details_order_statue_tv.setTextColor(ContextCompat.getColor(this , R.color.ColorSnackBarError))
            }
            diffInHours < 2 -> {
                order_details_order_statue_tv.text = resources.getString(R.string.order_status_in_process)
                order_details_order_statue_tv.setTextColor(ContextCompat.getColor(this , R.color.colorOrderStatusInProcess))
            }
            diffInHours < 3 -> {
                order_details_order_statue_tv.text = resources.getString(R.string.order_status_delivered)
                order_details_order_statue_tv.setTextColor(ContextCompat.getColor(this , R.color.colorOrderStatusDelivered))
            }
            else -> {
            order_details_order_statue_tv.text = resources.getString(R.string.order_status_delivered)
            order_details_order_statue_tv.setTextColor(
                ContextCompat.getColor(this@OrderDetailsActivity, R.color.colorOrderStatusDelivered)
            )
        }
        }

        order_details_items_tv.layoutManager = LinearLayoutManager(this)
        order_details_items_tv.setHasFixedSize(true)
        val adapter = CartListAdapter(this ,orderDetails.items , false )
        order_details_items_tv.adapter = adapter

        tv_my_order_details_address_type.text = orderDetails.address.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text = orderDetails.address.address
        tv_my_order_details_additional_note.text = orderDetails.address.additionalNote

        if(orderDetails.address.otherDetails.isNotEmpty()){
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = orderDetails.address.otherDetails
        }else{
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber

        tv_order_details_sub_total.text = orderDetails.subTotalAmount
        tv_order_details_total_amount.text = orderDetails.totalAmount
    }

    private fun setupActionbar(){
        setSupportActionBar(order_details_toolbar)
        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        order_details_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}