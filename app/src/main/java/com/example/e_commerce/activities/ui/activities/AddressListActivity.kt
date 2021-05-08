package com.example.e_commerce.activities.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.Address
import com.example.e_commerce.activities.ui.adapters.AddressListAdapter
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.activities.utils.SwipeToDeleteCallback
import com.example.e_commerce.activities.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlin.collections.ArrayList

class AddressListActivity : BaseActivity() {

    private var mSelectedAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        setupActionBar()
        getAddressList()

        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS , false)
        }

        if(mSelectedAddress){
            toolbar_address_list_tv.text = resources.getString(R.string.title_select_address)
        }

        address_list_add_address_tv.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivityForResult(intent , Constants.ADD_ADDRESS_REQ_CODE)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            getAddressList()
        }
    }

    fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAddressFromCloud(this)
    }

    fun getAddressSuccess(addressList: ArrayList<Address>){
        hideProgressDialog()
        if(addressList.size>0){
            address_list_address_rv.visibility = View.VISIBLE
            address_list_no_address_tv.visibility = View.GONE
            address_list_address_rv.layoutManager= LinearLayoutManager(this)
            address_list_address_rv.setHasFixedSize(true)
            val adapter = AddressListAdapter(this , addressList , mSelectedAddress)

            address_list_address_rv.adapter = adapter

            if(!mSelectedAddress){
                val editSwipeHandler= object: SwipeToEditCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = address_list_address_rv.adapter as AddressListAdapter
                        adapter.notifyItemChange(this@AddressListActivity , viewHolder.adapterPosition)
                    }
                }
                val editItemTouchHelper =  ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(address_list_address_rv)

                val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FireStoreClass().deleteAddress(this@AddressListActivity , addressList[viewHolder.adapterPosition].id)
                    }
                }
                val deleteItemTouchHelper =  ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(address_list_address_rv)
            }

        }else{
            address_list_address_rv.visibility = View.GONE
            address_list_no_address_tv.visibility = View.VISIBLE
        }
    }

    fun successDeleteAddress(){
        hideProgressDialog()
        Toast.makeText(this ,
            resources.getString(R.string.err_your_address_deleted_successfully) ,
            Toast.LENGTH_SHORT).show()
        getAddressList()
    }


    fun setupActionBar(){
        setSupportActionBar(toolbar_address_list_activity)
        val actionBar = supportActionBar
        if (actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbar_address_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}