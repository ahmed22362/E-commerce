package com.example.e_commerce.activities.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.Product
import com.example.e_commerce.activities.ui.activities.CartListActivity
import com.example.e_commerce.activities.ui.activities.LoginActivity
import com.example.e_commerce.activities.ui.activities.SettingsActivity
import com.example.e_commerce.activities.ui.adapters.DashboardAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // to create just menu in this fragment
        setHasOptionsMenu(true)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
            textView.text = "this is the dashbord fragment"
            textView.setTextColor(Color.parseColor("#FFFFFF"))

        return root

    }


    override fun onResume() {
        super.onResume()
        getDashboardProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashbord_menu , menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){
            R.id.action_setting->{
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_shop_cart->{
                startActivity(Intent(activity , CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun successfulDashboardItem(productList: ArrayList<Product>){
        hideProgressDialog()
        if(productList.size > 0){
            fragment_dashboard_rv.visibility = View.VISIBLE
            text_dashboard.visibility = View.GONE
            fragment_dashboard_rv.layoutManager=GridLayoutManager(context,2)
            fragment_dashboard_rv.setHasFixedSize(true)
            val dashboardAdapter = DashboardAdapter(requireActivity() , productList)
            fragment_dashboard_rv.adapter = dashboardAdapter
        }else{
            fragment_dashboard_rv.visibility = View.GONE
            text_dashboard.visibility = View.VISIBLE
        }
    }
    fun getDashboardProducts(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getDashboardProduct(this)
    }
}