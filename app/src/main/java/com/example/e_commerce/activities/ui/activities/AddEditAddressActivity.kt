package com.example.e_commerce.activities.ui.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.e_commerce.R
import com.example.e_commerce.activities.firestore.FireStoreClass
import com.example.e_commerce.activities.models.Address
import com.example.e_commerce.activities.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_add_edit_address.view.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddEditAddressActivity : BaseActivity() {

    private  var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        setupActionBar()

        if(intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if(mAddressDetails!= null){
            if(mAddressDetails!!.id.isNotEmpty()){
                toolbar_title_tv.text = resources.getString(R.string.title_edit_address)
                btn_submit_address.setText(resources.getString(R.string.btn_lbl_update))
            }
            et_full_name.setText(mAddressDetails?.name)
            et_address.setText(mAddressDetails?.address)
            et_phone_number.setText(mAddressDetails?.mobileNumber)
            et_zip_code.setText(mAddressDetails?.zipCode)
            et_additional_note.setText(mAddressDetails?.additionalNote)
            when(mAddressDetails?.type){
                Constants.HOME ->{
                    rb_home.isChecked = true
                }
                Constants.OFFICE ->{
                    rb_office.isChecked = true
                }
                Constants.OTHER ->{
                    rb_other.isChecked= true
                    til_other_details.visibility = View.VISIBLE
                    et_other_details.setText(mAddressDetails?.additionalNote)
                }
            }
        }

        btn_submit_address.setOnClickListener {
            saveAddressToFireStore()
        }

        rg_type.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId == R.id.rb_other){
                til_other_details.visibility = View.VISIBLE
            }else{
                til_other_details.visibility = View.GONE
            }
        }
    }


    fun saveAddressToFireStore(){
        if (validEntries()){
        val name = et_full_name.text.toString().trim{it<=' '}
        val phone = et_phone_number.text.toString().trim { it<=' ' }
        val address = et_address.text.toString().trim { it<=' ' }
        val zipCode = et_zip_code.text.toString().trim { it<=' ' }
        val additionalNotes = et_additional_note.text.toString().trim{it<=' '}
        val otherDetails = et_other_details.text.toString().trim { it<= ' ' }

        showProgressDialog(resources.getString(R.string.please_wait))
        val addressType: String = when{
            rb_home.isChecked->{Constants.HOME }
            rb_office.isChecked-> {Constants.OFFICE}
            else-> {Constants.OTHER} }

            val addressModel = Address(
                FireStoreClass().getUserID(),
                name,
                phone,
                address,
                zipCode,
                additionalNotes,
                addressType,
                otherDetails
            )

            if(mAddressDetails!= null && mAddressDetails!!.id.isNotEmpty()){
                FireStoreClass().updateAddress(this , addressModel , mAddressDetails!!.id)
            }else {
                FireStoreClass().addAddressToFireStore(this, addressModel)
            }
        }
    }

    fun addAddressSuccess(){
        hideProgressDialog()
        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.err_your_address_added_successfully)
        }

        Toast.makeText(this , notifySuccessMessage  , Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun validEntries():Boolean{
        return when{
            TextUtils.isEmpty(et_full_name.text.toString().trim{it<=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name) , true)
                et_full_name.setError("bitch")
                false
            }
            TextUtils.isEmpty(et_phone_number.text.toString().trim { it<=' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_phone_number) , true)
                et_phone_number.setError(resources.getString(R.string.err_msg_please_enter_phone_number))
                false
            }
            TextUtils.isEmpty(et_address.text.toString().trim { it<=' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address) , true)
                et_address.setError(resources.getString(R.string.err_msg_please_enter_address))
                false
            }
            TextUtils.isEmpty(et_zip_code.text.toString().trim { it<=' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code) , true)
                et_zip_code.setError(resources.getString(R.string.err_msg_please_enter_zip_code))
                false
            }
            else -> {
                showErrorSnackBar("valid" , false)
                true
            }
        }
    }

    fun setupActionBar(){
        setSupportActionBar(toolbar_add_address)
        val actionBar = supportActionBar
        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_add_address.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}