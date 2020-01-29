package com.pusher.demo.features.marketplace.chat

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.presence.Presence
import com.pusher.chatkit.users.User
import com.pusher.demo.R
import com.pusher.demo.features.marketplace.ChatkitManager
import kotlinx.android.synthetic.main.activity_marketplace_chat.*

class MarketplaceChatActivity : AppCompatActivity(), MarketplaceChatPresenter.View {

    private lateinit var adapter: MessageAdapter

    private val presenter = MarketplaceChatPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace_chat)

        presenter.onViewAttached(this)

        if (ChatkitManager.currentUser != null) {
            //set up our recyclerview adapter
            adapter = MessageAdapter(ChatkitManager.currentUser!!.id) {
                presenter.onMessageDisplayed(it)
            }

            val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            layoutManager.stackFromEnd = true
            recyclerViewMessages.layoutManager =  layoutManager
            recyclerViewMessages.adapter = adapter

            //handle sending messages
            txtMessage.setOnEditorActionListener { _, actionId, keyEvent ->

                val sendRequested = actionId == EditorInfo.IME_ACTION_SEND ||
                        keyEvent.keyCode == KeyEvent.KEYCODE_ENTER

                if (sendRequested && txtMessage.text.isNotEmpty()){
                    presenter.sendMessageToRoom(txtMessage.text.toString())
                    txtMessage.setText("")
                    true
                } else {
                    false
                }
            }

            presenter.connect()
        } else {
            onError("Current user was not found - have you signed in?")
        }

        if (ChatkitManager.currentUser!!.name == "buyer") {
            addQuickReplies(listOf(
                "Is this item still available?",
                "Is the price negotiable?",
                "Are you willing to lower the price?"
            ))
        } else if (ChatkitManager.currentUser!!.name == "seller") {
            addQuickReplies(listOf(
                "This item is still available",
                "Sorry this item is no longer available"))
        }
    }

    override fun onError(exception: String) {
        txtMessage.isEnabled = false
        lblError.text = exception
        recyclerViewMessages.visibility = View.GONE
    }

    override fun onConnected(person: CurrentUser) {
        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show()
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
        lblName.text = person.name
        displayPresence(person.presence)
    }

    override fun onOtherMemberPresenceChanged(person: User) {
        displayPresence(person.presence)
    }

    override fun onMessageReceived(message: Message) {
        adapter.addMessage(message)
        recyclerViewMessages.layoutManager?.scrollToPosition(adapter.itemCount - 1)
    }

    override fun onOtherMemberReadCursorChanged(messageId: Int) {
        adapter.markAsReadUpTo(messageId)
    }

    private fun addQuickReplies(quickReplies: List<String>) {

        for (quickReply in quickReplies) {

            val item = layoutInflater.inflate(R.layout.item_quick_reply, containerQuickReplies, false)
            item.findViewById<TextView>(R.id.lblQuickReply).text = quickReply

            containerQuickReplies.addView(item)

            item.setOnClickListener {
                presenter.sendMessageToRoom(quickReply)
            }

        }
    }
}
