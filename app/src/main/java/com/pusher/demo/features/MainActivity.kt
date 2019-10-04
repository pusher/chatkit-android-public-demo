package com.pusher.demo.features

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pusher.demo.R
import com.pusher.demo.features.marketplace.MarketplaceActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //open the seller style chat
        btnMarketplaceSeller.setOnClickListener {
            val intent = Intent(this, MarketplaceActivity::class.java)
            intent.putExtra(MarketplaceActivity.EXTRA_USER_ID, "seller")
            startActivity(intent)
        }

        btnMarketplaceBuyer.setOnClickListener {
            val intent = Intent(this, MarketplaceActivity::class.java)
            intent.putExtra(MarketplaceActivity.EXTRA_USER_ID, "buyer")
            startActivity(intent)
        }
    }
}
