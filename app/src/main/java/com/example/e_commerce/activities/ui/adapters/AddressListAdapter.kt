package com.example.e_commerce.activities.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.activities.models.Address
import com.example.e_commerce.activities.ui.activities.AddEditAddressActivity
import com.example.e_commerce.activities.ui.activities.AddressListActivity
import com.example.e_commerce.activities.ui.activities.CheckoutActivity
import com.example.e_commerce.activities.utils.Constants
import kotlinx.android.synthetic.main.item_address_list_layout.view.*

class AddressListAdapter(val context: Context , val addressList: ArrayList<Address>
                         , private val selectedAddress: Boolean):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address_list_layout
            ,parent,false) )
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = addressList[position]
        if(holder is MyViewHolder){
            holder.itemView.item_address_name.text = model.name
            holder.itemView.item_address_phone.text = model.mobileNumber
            holder.itemView.item_address_details.text = model.address
            holder.itemView.item_address_type.text = model.type
            if (selectedAddress){
                holder.itemView.setOnClickListener {
                    val intent = Intent(context , CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS , model)
                    context.startActivity(intent)
                }
            }
        }
    }

    fun notifyItemChange( activity: Activity, position: Int){
        val intent = Intent(context,AddEditAddressActivity::class.java )
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS , addressList[position])
        activity.startActivityForResult(intent , Constants.ADD_ADDRESS_REQ_CODE)
        notifyItemChanged(position)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}