package com.example.e_commerce.activities.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.Product
import com.example.e_commerce.activities.ui.activities.AddProductActivity
import com.example.e_commerce.activities.ui.adapters.ProductsAdapter
import kotlinx.android.synthetic.main.fragment_products.*

class ProductsFragment : BaseFragment() {

    private  val TAG = "ProductsFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onResume() {
        super.onResume()
        getProductListFromCloud()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_products, container, false)
        return root
    }


    fun deleteProduct(productId:String){
        showAlertDialogDeleteMessage(productId)
        Toast.makeText(requireActivity() , "you are deleting $productId" , Toast.LENGTH_SHORT ).show()
    }

    fun deletedProductSuccessfully(){
        hideProgressDialog()
        Toast.makeText(requireActivity(),"your product deleted",Toast.LENGTH_SHORT).show()
        getProductListFromCloud()
    }

    fun successProductListFromCloud(productList: ArrayList<Product>){
        hideProgressDialog()
        Log.e(TAG, "successProductListFromCloud: howo" )
        if(productList.size>0){
            Log.e(TAG, "successProductListFromCloud: hereee" )
            fragment_product_rv.visibility = View.VISIBLE
            fragment_product_tv.visibility = View.GONE

            fragment_product_rv.layoutManager = LinearLayoutManager(activity)
            fragment_product_rv.setHasFixedSize(true)

            val productAdapter = ProductsAdapter(requireActivity() , productList , this)

            fragment_product_rv.adapter = productAdapter

        }else{
            fragment_product_rv.visibility = View.GONE
            fragment_product_tv.visibility = View.VISIBLE
            Log.e(TAG, "successProductListFromCloud: else" )

        }

    }

    fun getProductListFromCloud(){
        showProgressDialog(resources.getString(R.string.please_wait))
        Log.e(TAG, "getProductListFromCloud: hi" )
        FireStoreClass().getProducts(this)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id){
            R.id.add_product_action ->{
                startActivity(Intent(activity , AddProductActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialogDeleteMessage(productId: String){
        val builder =AlertDialog.Builder(requireActivity())

        builder.setTitle(resources.getString(R.string.delete_alert_title))
        builder.setMessage(resources.getString(R.string.delete_alert_message))
        builder.setIcon(android.R.drawable.ic_delete)
        builder.setPositiveButton(resources.getString(R.string.yes)){dialoginterface, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().deleteProduct(this, productId)
            dialoginterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.no)){dialoginterface, _ ->
            dialoginterface.dismiss()
        }

        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}