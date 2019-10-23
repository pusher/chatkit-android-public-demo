package com.pusher.demo.features.marketplace.buyer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pusher.chatkit.CurrentUser
import com.pusher.demo.R
import com.pusher.demo.features.marketplace.chat.MarketplaceChatActivity
import kotlinx.android.synthetic.main.activity_buyer.*

class BuyerActivity : AppCompatActivity(), BuyerPresenter.View {

    private val presenter = BuyerPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer)

        presenter.onViewAttached(this)

        presenter.connectToChatkit(this)

        lblError.text = "connecting"
        containerContent.visibility = View.GONE
    }

    override fun onConnected(user: CurrentUser) {
        runOnUiThread {
            containerContent.visibility = View.VISIBLE
            lblError.visibility = View.GONE

            btnMessageSeller.setOnClickListener {
                startActivity(
                    Intent(this, MarketplaceChatActivity::class.java))
            }
        }
    }

    override fun onError(error: String) {
        runOnUiThread {
            lblError.text = error
        }
    }
}
