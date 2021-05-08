package com.example.e_commerce.activities.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.activities.models.Order
import com.example.e_commerce.activities.ui.activities.OrderDetailsActivity
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_rv_layout.view.*

class OrdersListAdapter(private val context: Context
                        , private val list: ArrayList<Order>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_list_rv_layout,parent,false))
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model = list[position]
        if (holder is MyViewHolder){
            GlideLoader(context).
            loadProductPicture(model.productImage , holder.itemView.iv_item_image)

            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text= "$${model.totalAmount}"

            holder.itemView.ib_delete_product.visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context , OrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_ORDER_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }

class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}