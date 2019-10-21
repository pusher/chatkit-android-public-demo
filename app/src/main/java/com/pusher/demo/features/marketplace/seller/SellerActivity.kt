package com.pusher.demo.features.marketplace.seller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.pusher.chatkit.CurrentUser
import com.pusher.demo.R
import com.pusher.demo.features.marketplace.chat.MarketplaceChatActivity
import kotlinx.android.synthetic.main.activity_seller.*

class SellerActivity : AppCompatActivity(),
    SellerPresenter.View {

    private val presenter = SellerPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)

        presenter.onViewAttached(this)

        presenter.connectToChatkit(this )
        lblError.text = "connecting"
        containerContent.visibility = View.GONE

    }

    override fun onConnected(user: CurrentUser) {
        //display all the conversations
        runOnUiThread {
            containerContent.visibility = View.VISIBLE
            lblError.visibility = View.GONE

            containerProduct.setOnClickListener {
                Toast.makeText(this, "go to your product description",
                    Toast.LENGTH_SHORT).show()
            }

            containerConversation.setOnClickListener {
                startActivity(Intent(this, MarketplaceChatActivity::class.java))
            }
        }
    }

    override fun onError(error: String) {
        runOnUiThread {
            lblError.text = error
        }
    }
}
