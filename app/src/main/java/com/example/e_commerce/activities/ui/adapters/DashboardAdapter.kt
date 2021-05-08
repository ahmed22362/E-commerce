package com.example.e_commerce.activities.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.activities.models.Product
import com.example.e_commerce.activities.ui.activities.ProductDetailsActivity
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout_dashboard.view.*

class DashboardAdapter(private val context: Context , private val productList: ArrayList<Product>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_list_layout_dashboard,parent ,false ))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = productList[position]
        if (holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image , holder.itemView.item_image)
            holder.itemView.item_name.text = model.title
            holder.itemView.item_price.text = "$${model.price}"
            holder.itemView.setOnClickListener {
                val intent = Intent(context , ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID , model.product_id)
                intent.putExtra(Constants.SHOWEDITMENU , 0)
                intent.putExtra(Constants.EXTRA_PRODUCT_USER_NAME ,model.user_id )
                context.startActivity(intent)
            }
        }
    }



    class MyViewHolder(view:View) :RecyclerView.ViewHolder(view)
}