package com.example.e_commerce.activities.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.SearchRecentSuggestions
import android.webkit.MimeTypeMap

object Constants {

    const val READ_STORAGE_PERMISSION_REQUEST_CODE  = 0
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val MY_SHOP_PAL_PREFERENCE: String = "my shop pal preference"
    //user constance
    const val USERS: String = "users"
    const val GENDER: String = "gender"
    const val MALE: String = "male"
    const val FEMALE:String = "female"
    const val MOBILE:String = "mobile"
    const val IMAGE: String = "image"
    const val COMPLETED_PROFILE: String = "profileComplete"
    const val FIRST_NAME:String = "firstName"
    const val LAST_NAME:String = "lastName"
    const val USER_PROFILE_IMAGE: String = "user_profile_image"
    const val LOGGED_IN_USERNAME: String = "logged in username"
    const val EXTRA_USER_DETAILS: String = "extra user details"
    //product constance

    const val PRODUCTS: String = "product"
    const val TITLE: String = "title"
    const val PRODUCT_ID:String = "id"
    const val PRODUCTS_IMAGE:String = "product image"
    const val PRICE: String ="price"
    const val DESCRIPTION: String = "description"
    const val PRODUCT_IMAGE: String = "product_image"

    const val USER_ID: String = "user_id"

    const val PRODUCT_QUANTITY:String = "quantity"

    const val EXTRA_PRODUCT_ID: String = "extra_product_id"

    const val EXTRA_PRODUCT_USER_NAME: String = "product_user_id"

    const val DEFAULT_CART_QUANTITY: String = "1"

    const val CART_ITEMS: String = "cart_items"

    const val CART_PRODUCT_ID: String = "product_Id"

    const val CART_ITEM_IMAGE: String = "image"

    const val SHOWEDITMENU: String ="menu"

    const val CART_ITEM_QUANTITY: String = "cart_quantity"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    const val ADDRESS: String ="address"

    const val EXTRA_ADDRESS_DETAILS: String ="address_details"

    const val EXTRA_SELECT_ADDRESS: String="select_address"

    const val ADD_ADDRESS_REQ_CODE:Int = 122

    const val EXTRA_SELECTED_ADDRESS: String="selected_address"

    const val ORDERS: String = "orders"

    const val STOCK_QUANTITY: String = "stock_quantity"

    const val EXTRA_ORDER_DETAILS: String = "extra_order_details"

    const val SOLD_PRODUCT: String = "sold_product"

    const val EXTRA_SOLD_PRODUCTS_DETAILS: String="extra_sold_product_details"


    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }


    fun getFileExtension(activity: Activity , uri: Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}