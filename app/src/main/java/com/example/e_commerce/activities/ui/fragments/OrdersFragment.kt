package com.example.e_commerce.activities.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.Order
import com.example.e_commerce.activities.ui.adapters.OrdersListAdapter
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_orders, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        getOrdersList()
    }

    private fun getOrdersList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getOrdersList(this)
    }

    fun getOrdersSuccess(ordersList: ArrayList<Order>){
        if(ordersList.size>0){
            fragment_order_rv.visibility = View.VISIBLE
            fragment_order_tv.visibility = View.GONE
            fragment_order_rv.layoutManager = LinearLayoutManager(activity)
            fragment_order_rv.setHasFixedSize(true)

            val adapter = OrdersListAdapter(requireActivity() ,ordersList)

            fragment_order_rv.adapter = adapter

        }else{
            fragment_order_rv.visibility = View.GONE
            fragment_order_tv.visibility = View.VISIBLE
        }
        hideProgressDialog()

    }
}