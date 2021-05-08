package com.example.e_commerce.activities.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order (
    val user_id: String ="",
    val items: ArrayList<CartItem> = ArrayList(),
    val address: Address= Address(),
    val title: String = "",
    val productImage: String = "",
    val subTotalAmount: String ="",
    val shippingCharge: String ="",
    val totalAmount: String = "",
    val order_datetime: Long = 0L,
    var id: String = ""
    ):Parcelable