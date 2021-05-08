package com.example.e_commerce.activities.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.CartItem
import com.example.e_commerce.activities.ui.activities.CartListActivity
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.GlideLoader
import kotlinx.android.synthetic.main.item_cart_list_layout.view.*
import kotlinx.android.synthetic.main.item_list_rv_layout.view.*

class CartListAdapter   (private val context: Context
                      , private val list: ArrayList<CartItem>
                        ,private val updateCartItem: Boolean
                         ):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_cart_list_layout,parent,false
        ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image , holder.itemView.cart_item_image)
            holder.itemView.tv_cart_item_title.text = model.title
            holder.itemView.tv_cart_item_price.text = "$${model.price}"
            holder.itemView.tv_cart_item_quantity.text = model.cart_quantity

            if(model.cart_quantity.toInt() == 0){
                holder.itemView.ib_add_cart_item.visibility = View.GONE
                holder.itemView.ib_delete_cart_item.visibility= View.GONE

                if(updateCartItem){
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                }else{
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                }

                holder.itemView.tv_cart_item_quantity.text= context.resources.getString(R.string.lbl_out_of_stock)
                holder.itemView.tv_cart_item_quantity.setTextColor(ContextCompat.getColor(context,R.color.ColorSnackBarError))
            }else{
                if(updateCartItem){
                    holder.itemView.ib_add_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_delete_cart_item.visibility= View.VISIBLE
                    holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                }else{
                    holder.itemView.ib_add_cart_item.visibility = View.GONE
                    holder.itemView.ib_delete_cart_item.visibility= View.GONE
                    holder.itemView.ib_remove_cart_item.visibility = View.GONE
                }

            }

            holder.itemView.ib_delete_cart_item.setOnClickListener {
                when(context){
                    is CartListActivity->{
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }
                FireStoreClass().deleteItemFromCart(context , model.id)

            }

            holder.itemView.ib_add_cart_item.setOnClickListener {
                val quantity = model.cart_quantity.toInt()
                if (quantity < model.stock_quantity.toInt()){
                    val itemHashMap = HashMap<String , Any>()
                    itemHashMap[Constants.CART_ITEM_QUANTITY] = (quantity + 1).toString()
                    if (context is CartListActivity){
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                    FireStoreClass().updateItemInCart(context , model.id , itemHashMap)
                }else{
                    if (context is CartListActivity) {
                            context.showErrorSnackBar("you can not add more stock quatity" , true)
                        }
                    }
            }

            holder.itemView.ib_remove_cart_item.setOnClickListener {
                if(model.cart_quantity.toInt() ==1){
                    FireStoreClass().deleteItemFromCart(context , model.id)
                }else{
                    val quantity = model.cart_quantity.toInt()
                    val itemHashMap = HashMap<String , Any>()
                    itemHashMap[Constants.CART_ITEM_QUANTITY] = (quantity - 1).toString()
                    if (context is CartListActivity){
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                    FireStoreClass().updateItemInCart(context , model.id , itemHashMap)
                }
            }

        }
    }

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}