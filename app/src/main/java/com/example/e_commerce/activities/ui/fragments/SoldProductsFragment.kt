package com.example.e_commerce.activities.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.SoldProduct
import com.example.e_commerce.activities.ui.adapters.SoldProductAdapter
import kotlinx.android.synthetic.main.fragment_sold_products.*


class SoldProductsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold_products, container, false)
    }

    override fun onResume() {
        super.onResume()
        getSoldProductsDetails()
    }

    private fun getSoldProductsDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getSoldProductList(this)
    }

    fun getSoldProductSuccess(productList: ArrayList<SoldProduct>){
        hideProgressDialog()
        if (productList.size>0){
            fragment_sold_product_rv.visibility = View.VISIBLE
            fragment_sold_product_tv.visibility = View.GONE

            fragment_sold_product_rv.layoutManager = LinearLayoutManager(activity)
            fragment_sold_product_rv.setHasFixedSize(true)

            val adapter = SoldProductAdapter(requireActivity() , productList)
            fragment_sold_product_rv.adapter = adapter

        }else{
            fragment_sold_product_rv.visibility = View.GONE
            fragment_sold_product_tv.visibility = View.VISIBLE
        }
    }
}