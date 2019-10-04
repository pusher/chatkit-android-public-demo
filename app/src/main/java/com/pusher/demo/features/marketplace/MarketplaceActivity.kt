package com.pusher.demo.features.marketplace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.presence.Presence
import com.pusher.chatkit.users.User
import com.pusher.demo.R
import kotlinx.android.synthetic.main.activity_seller.*

class MarketplaceActivity : AppCompatActivity(), MarketplacePresenter.View {

    companion object {
        val EXTRA_USER_ID = "com.pusher.demo.features.marketplace.EXTRA_USER_ID"
    }

    private lateinit var adapter: MessageAdapter

    private val presenter = MarketplacePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)

        val userId = intent.getStringExtra(EXTRA_USER_ID)!!

        presenter.onViewAttached(this)

        //tell our presenter to connect as the seller user
        presenter.connect(this, userId)


        //set up our recyclerview adapter
        adapter = MessageAdapter(applicationContext, userId)
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerViewMessages.layoutManager =  layoutManager
        recyclerViewMessages.adapter = adapter

        //handle sending messages
        txtMessage.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEND){
                presenter.sendMessageToRoom(txtMessage.text.toString())
                txtMessage.setText("")
                true
            } else {
                false
            }
        }
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

    private fun displayPresence(presence: Presence) {
        if (presence == Presence.Online) {
            val unwrappedDrawable = AppCompatResources.getDrawable(applicationContext, R.drawable.icon_profile)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(applicationContext, R.color.light_text))

            imgStatus.setImageDrawable(wrappedDrawable)
        } else {

            val unwrappedDrawable = AppCompatResources.getDrawable(applicationContext, R.drawable.icon_profile_outline)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(applicationContext, R.color.light_purple))

            imgStatus.setImageDrawable(wrappedDrawable)
        }
    }

    override fun onOtherMember(person: User) {
        runOnUiThread {
            lblName.text = person.name
            displayPresence(person.presence)
        }

    }

    override fun onMemberPresenceChanged(person: User) {
        runOnUiThread {
            displayPresence(person.presence)
        }
    }

    override fun onMessageReceived(message: Message) {
        runOnUiThread {
            adapter.addMessage(message)
            recyclerViewMessages.layoutManager?.scrollToPosition(adapter.messages.size -1)
        }
    }

}
