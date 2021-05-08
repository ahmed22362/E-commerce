package com.example.e_commerce.activities.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_commerce.R
import com.example.e_commerce.activities.utils.Constants
import com.example.e_commerce.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Ecommerce)
        setContentView(binding.root)
        supportActionBar?.hide()

        val sharedPreferences = getSharedPreferences(Constants.MY_SHOP_PAL_PREFERENCE, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")
        binding.mainTv.text = "hello $username"

    }
}