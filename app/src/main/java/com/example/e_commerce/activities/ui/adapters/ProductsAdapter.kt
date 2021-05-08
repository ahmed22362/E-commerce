package com.example.e_commerce.activities.ui.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.activities.models.Product
import com.example.e_commerce.activities.ui.activities.ProductDetailsActivity
import com.example.e_commerce.activities.ui.fragments.ProductsFragment
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_rv_layout.view.*

open class ProductsAdapter (private  val context: Context
                            ,private val productList:ArrayList<Product>,
                            private  val fragment: ProductsFragment):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_rv_layout
                ,parent
                ,false)
        )
    }
    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = productList[position]
        if(holder is MyViewHolder){
            Log.e("TAG", "onBindViewHolder: "+model )
            GlideLoader(context).loadProductPicture(model.image,holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "$${model.price}"

            holder.itemView.ib_delete_product.setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context , ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID , model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_USER_NAME ,model.user_id )
                context.startActivity(intent)
            }
        }
    }


    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}