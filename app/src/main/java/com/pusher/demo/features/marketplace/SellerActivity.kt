package com.pusher.demo.features.marketplace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.users.User
import com.pusher.demo.R

class SellerActivity : AppCompatActivity(), MarketplacePresenter.View {

    private val presenter = MarketplacePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
        presenter.onViewAttached(this)

        //tell our presenter to connect as the seller user
        presenter.connect(this, "seller")

    }

    override fun onError(exception: String) {
        runOnUiThread {
            Toast.makeText(this, exception, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onConnected(person: CurrentUser) {
        runOnUiThread {
            Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOtherMember(person: User) {
       
    }

    override fun onMemberPresenceChanged(person: User) {

    }

    override fun onMessageReceived(message: Message) {

    }

}
