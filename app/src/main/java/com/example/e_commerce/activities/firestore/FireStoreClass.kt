package com.example.e_commerce.activities.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.e_commerce.activities.models.*
import com.example.e_commerce.activities.ui.activities.*
import com.example.e_commerce.activities.ui.fragments.DashboardFragment
import com.example.e_commerce.activities.ui.fragments.OrdersFragment
import com.example.e_commerce.activities.ui.fragments.ProductsFragment
import com.example.e_commerce.activities.ui.fragments.SoldProductsFragment
import com.example.e_commerce.activities.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withTimeout

class FireStoreClass {
    private val db = Firebase.firestore
    private val TAG = "FireStoreClass"

    fun registerUser (activity: RegisterActivity, userInfo: User){
        db.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(TAG, "registerUser: ",e )
            }
    }

    fun getUserID():String{
        val currentUser = Firebase.auth.currentUser
        var currentUserID: String = ""

        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity){
        db.collection(Constants.USERS)
                .document(getUserID())
                .get()
                .addOnSuccessListener {document ->
                    val user  = document.toObject(User::class.java)!!

                    //put username in pone storage
                    val sharedPreferences = activity.getSharedPreferences(Constants.MY_SHOP_PAL_PREFERENCE
                            , Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString(Constants.LOGGED_IN_USERNAME , "${user.firstName} ${user.lastName}")
                            editor.apply()

                    when(activity){
                        is LoginActivity -> {
                            activity.userLoggedInSuccessfully(user)
                        }
                        is SettingsActivity  -> {
                            activity.userDetailsSuccess(user)
                        }
                    }

                }.addOnFailureListener {e->
                    when(activity){
                        is LoginActivity ->{
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity->{
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(TAG, "error while getting user details: ", e )
                }
    }

    fun updateUserDetails(activity: Activity , hashMap: HashMap<String,Any>){
        db.collection(Constants.USERS)
                .document(getUserID())
                .update(hashMap)
                .addOnSuccessListener {
                    when(activity){
                        is UserProfileActivity ->{
                            activity.userUpdateProfileSuccessfully()
                        }
                    }
                }
                .addOnFailureListener {e ->

                    when (activity) {
                        is UserProfileActivity -> {
                            // Hide the progress dialog if there is any error. And print the error in log.
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the user details.",
                            e
                    )
                }

    }

    fun uploadImageToCloudStorage(activity: Activity , imageURI: Uri? , imageType: String){

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType+ System.currentTimeMillis()+"."
                        +Constants.getFileExtension(activity , imageURI))

        sRef.putFile(imageURI!!).addOnSuccessListener {taskSnapshot ->
            Log.e(TAG, taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {uri ->
                when(activity){
                    is UserProfileActivity -> {
                        activity.imageUploadedSuccess(uri.toString())
                    }
                    is AddProductActivity ->{
                        activity.imageUploadedSuccessfully(uri.toString())
                    }
                }

                Log.e(TAG, uri.toString() )
            }.addOnFailureListener{exception->
                when(activity){
                    is UserProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity ->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(TAG, exception.message  , exception)
            }
        }

    }

    fun addProduct(activity: AddProductActivity , productInfo: Product){
        db.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.productAddedSuccessfully()
            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(TAG, "addProduct: ",e )
            }
    }

    fun getProducts(fragment: Fragment){
        db.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID,getUserID() )
            .get()
            .addOnSuccessListener {document->

                val productList: ArrayList<Product> =ArrayList()

                for(i in document.documents){
                    val product = i.toObject(Product::class.java)
                    Log.e(TAG, "getProducts: "+product )
                    product?.product_id = i.id
                    productList.add(product!!)
                }

                when(fragment){
                    is ProductsFragment ->{
                        fragment.successProductListFromCloud(productList)
                    }
                }
            }.addOnFailureListener {
                when(fragment){
                    is ProductsFragment ->{
                        Log.e(TAG, "getProducts: hi" )
                        fragment.hideProgressDialog()
                    }
                }
            }
    }

    fun getDashboardProduct(fragment: DashboardFragment){
        db.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {documnet->
                val productList: ArrayList<Product> = ArrayList()
                for(i in documnet.documents){
                    val product = i.toObject(Product::class.java)
                    product?.product_id = i.id

                    productList.add(product!!)
                }
                fragment.successfulDashboardItem(productList)
            }
            .addOnFailureListener {e->
                Log.e(TAG, "failed in getting data for dashboard",e )
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity , productId: String){
        db.collection(Constants.PRODUCTS)
                .document(productId)
                .get()
                .addOnSuccessListener {documentSnapshot ->
                    val product = documentSnapshot.toObject(Product::class.java)!!
                    activity.productDetailsSuccess(product)
                }.addOnFailureListener { e->
                    activity.hideProgressDialog()
                    Log.e(TAG, "getProductDetails: ",e )
                }
    }

    fun updateProduct(activity: ProductDetailsActivity , hashMap: HashMap<String, Any> , productId: String){
        db.collection(Constants.PRODUCTS)
                .document(productId)
                .update(hashMap)
                .addOnSuccessListener {
                    Log.e(TAG, "updateProduct: here" )
                    activity.successProductDetails()
                }.addOnFailureListener {e->
                    activity.hideProgressDialog()
                    Log.e(TAG, "updateProduct: ",e )
                }
    }

    fun deleteProduct(fragment: ProductsFragment ,productId: String){
        db.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.deletedProductSuccessfully()
            }.addOnFailureListener {e->
                Log.e(TAG, "deleteProduct: product didn't deleted succussfully",e )

            }
    }


    fun getAllProductsList(activity: Activity){
        db.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener {document->
                    val productList: ArrayList<Product> = ArrayList()
                    for(i in document.documents){
                        val product = i.toObject(Product::class.java)
                        product?.product_id = i.id
                        if (product != null) {
                            productList.add(product)
                        }
                        when(activity){
                            is CartListActivity->{
                                activity.successGettingAllProducts(productList)
                            }
                            is CheckoutActivity->{
                                activity.getAllProductSuccess(productList)
                            }
                        }

                    }
                }.addOnFailureListener {e->
                when(activity){
                    is CartListActivity->{
                        activity.hideProgressDialog()
                        Log.e(TAG, "getAllProducts: cart activity ", e)
                    }
                    is CheckoutActivity->{
                        activity.hideProgressDialog()
                        Log.e(TAG, "getAllProducts: checkout ativity", e)
                    }
                }

                }
    }

    fun addItemToCart(activity: ProductDetailsActivity , cartItem: CartItem){
        db.collection(Constants.CART_ITEMS)
                .document()
                .set(cartItem , SetOptions.merge())
                .addOnSuccessListener {
                    activity.itemAddedSuccessful()
                }.addOnFailureListener {e->
                    activity.hideProgressDialog()
                    Log.e(TAG, "addItemToCart: ",e )
                }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity , productId: String){
        Log.e(TAG, "checkIfItemExistInCart: bb" )
        db.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID ,getUserID())
                .whereEqualTo(Constants.CART_PRODUCT_ID , productId)
                .get()
                .addOnSuccessListener {document->
                    Log.e(activity.javaClass.simpleName, document.documents.toString()+"hereee"+productId)

                    if(document.documents.size >0){
                        activity.productExistInCart()
                    }else{
                        Log.e(TAG, "checkIfItemExistInCart: not here" )
                        activity.hideProgressDialog()

                    }
                }.addOnFailureListener { e->
                    activity.hideProgressDialog()
                    Log.e(TAG, "checkIfItemExistInCart: ",e )
                }
    }

    fun deleteItemFromCart(context: Context , itemId:String){
        db.collection(Constants.CART_ITEMS)
                .document(itemId)
                .delete().addOnSuccessListener {
                    when(context){
                        is CartListActivity ->{
                            context.successfullyDeleteItemFrmCart()
                        }
                    }
                }.addOnFailureListener {e->
                    Log.e(TAG, "deleteItemFromCart: ", e)
                }
    }

    fun updateItemInCart(context: Context , itemId: String , hashMap: HashMap<String, Any>){
        db.collection(Constants.CART_ITEMS)
                .document(itemId)
                .update(hashMap)
                .addOnSuccessListener {
                    when(context){
                        is CartListActivity->{
                            context.successfullyUpdateItem()
                        }
                    }

                }.addOnFailureListener { e->
                    when(context){
                        is CartListActivity->{
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(TAG, "updateItemInCart: ",e )
                }
    }
    fun getCartItems(activity: Activity){
        db.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID , getUserID())
                .get()
                .addOnSuccessListener { document->
                    val cartList :ArrayList<CartItem> = ArrayList()
                    for( i in document.documents){
                        val cartItem = i.toObject(CartItem::class.java)!!
                        cartItem.id = i.id
                        cartList.add(cartItem)
                    }
                    when(activity){
                        is CartListActivity->{
                            activity.successGettingCartList(cartList)
                        }
                        is CheckoutActivity ->{
                            activity.getCartItemSuccess(cartList)
                        }
                    }
                }.addOnFailureListener { e->
                    when(activity){
                        is CartListActivity->{
                            activity.hideProgressDialog()
                            Log.e(TAG, "getCartItems: ",e )
                        }
                        is CheckoutActivity->{
                            activity.hideProgressDialog()
                            Log.e(TAG, "getCartItems: ",e )
                        }
                    }
                }
    }

    fun addAddressToFireStore(activity: AddEditAddressActivity , addressInfo: Address){
        db.collection(Constants.ADDRESS)
            .document()
            .set(addressInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.addAddressSuccess()
            }.addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(TAG, "addAddressToFireStore: ",e )
            }
    }

    fun getAddressFromCloud(activity: AddressListActivity){
        db.collection(Constants.ADDRESS)
            .whereEqualTo(Constants.USER_ID , getUserID())
            .get()
            .addOnSuccessListener {documment->
                Log.e(TAG, "getAddressFromCloud: "+documment.documents.toString() )
                val addressList: ArrayList<Address> = ArrayList()
                for (i in documment.documents){
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }
                activity.getAddressSuccess(addressList)
            }.addOnFailureListener {e->
                Log.e(TAG, "getAddressFromCloud: ",e )
                activity.hideProgressDialog()
            }
    }


    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String){
        db.collection(Constants.ADDRESS)
            .document(addressId)
            .set(addressInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.addAddressSuccess()
            }.addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(TAG, "updateAddress: ",e )
            }
    }

    fun deleteAddress(activity: AddressListActivity , addressId: String){
        db.collection(Constants.ADDRESS)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.successDeleteAddress()
            }.addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(TAG, "deleteAddress: ",e )
            }
    }

    fun placeOrder(activity: CheckoutActivity , orderInfo: Order){
        db.collection(Constants.ORDERS)
            .document()
            .set(orderInfo , SetOptions.merge())
            .addOnSuccessListener {
                activity.placeOrderSuccess()
            }.addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(TAG, "placeOrder: ", e)
            }
    }

    fun updateCheckoutItems(activity: CheckoutActivity , cartItem: ArrayList<CartItem>, order: Order){
        val writeBatch = db.batch()

        for (item in cartItem){
//            val hashMap =  HashMap<String, Any>()
//            hashMap[Constants.STOCK_QUANTITY]=
//                (item.stock_quantity.toInt() - item.cart_quantity.toInt())
            val soldProduct=SoldProduct(
                item.product_owner_id,
                item.title,
                item.price,
                item.cart_quantity,
                item.image,
                order.user_id,
                order.order_datetime,
                order.subTotalAmount,
                order.shippingCharge,
                order.totalAmount,
                order.address
            )
            val ref = db.collection(Constants.SOLD_PRODUCT)
                .document(item.product_id)
            writeBatch.set(ref, soldProduct)
        }

        for (item in cartItem){
            val ref = db.collection(Constants.CART_ITEMS)
                .document(item.id)
            writeBatch.delete(ref)
        }

        writeBatch.commit().addOnSuccessListener {
            activity.cartItemsUpdateSuccess()
        }.addOnFailureListener {e->
            activity.hideProgressDialog()
            Log.e(TAG, "updateCheckoutItems: ",e )
        }
    }

    fun getOrdersList(fragment: OrdersFragment){
        db.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID , getUserID())
            .get()
            .addOnSuccessListener {document->
                val list:ArrayList<Order> = ArrayList()

                for (i in document.documents){
                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id
                    list.add(orderItem)
                }

                fragment.getOrdersSuccess(list)

            }.addOnFailureListener {e->
                fragment.hideProgressDialog()
                Log.e(TAG, "getOrdersList: ",e )
            }
    }

    fun getSoldProductList(fragment: SoldProductsFragment){
        db.collection(Constants.SOLD_PRODUCT)
            .whereEqualTo(Constants.USER_ID , getUserID())
            .get()
            .addOnSuccessListener {document ->
                val list: ArrayList<SoldProduct> = ArrayList()

                for(i in document.documents){
                    val model = i.toObject(SoldProduct::class.java)!!
                    model.id = i.id
                    list.add(model)
                }

                fragment.getSoldProductSuccess(list)
            }.addOnFailureListener {e->
                fragment.hideProgressDialog()
                Log.e(TAG, "getSoldProductList: ",e )
            }
    }
}