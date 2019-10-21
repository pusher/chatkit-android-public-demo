package com.pusher.demo.features

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pusher.demo.R
import com.pusher.demo.features.marketplace.buyer.BuyerActivity
import com.pusher.demo.features.marketplace.seller.SellerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //open the seller style chat
        btnMarketplaceSeller.setOnClickListener {
            startActivity(Intent(this, SellerActivity::class.java))
        }

        //open the buyer style chat
        btnMarketplaceBuyer.setOnClickListener {
            startActivity(Intent(this, BuyerActivity::class.java))
        }
    }
}
