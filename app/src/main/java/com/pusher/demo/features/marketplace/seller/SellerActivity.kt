package com.pusher.demo.features.marketplace.seller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.users.User
import com.pusher.demo.R
import com.pusher.demo.features.marketplace.chat.MarketplaceChatActivity
import kotlinx.android.synthetic.main.activity_seller.*

class SellerActivity : AppCompatActivity(),
    SellerPresenter.View {

    private val presenter = SellerPresenter()
    private lateinit var adapter: PersonAdapter

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
                Toast.makeText(this, "you would go to the product description",
                    Toast.LENGTH_SHORT).show()
            }

            displayConversations(user.rooms)
        }
    }

    fun displayConversations(rooms: List<Room>) {

        // you'd probably want to save on your backend which conversations relate to which product
        // because we only have 1 product we can assume all conversations are to dow ith it!

        lblConversationsTitle.text =
            resources.getQuantityText(R.plurals.numberOfPeopleInterested, rooms.size)

        val context = this
        //set up our recyclerview
        adapter = PersonAdapter(this, object:PersonAdapterListener{
            override fun onPersonSelected(person: User) {
                context.startActivity(Intent(context, MarketplaceChatActivity::class.java))
            }
        })
        recyclerViewPeople.layoutManager =  LinearLayoutManager(this)
        recyclerViewPeople.adapter = adapter

        //currently we have to subscribe to the room to get the people but we can change this soon!
        for (room in rooms) {
            presenter.subscribeToRoom(room)
        }

    }

    override fun onError(error: String) {
        runOnUiThread {
            lblError.text = error
            lblError.visibility = View.VISIBLE
            containerContent.visibility = View.GONE
        }
    }

    override fun onMemberPresenceChanged(user: User) {
        runOnUiThread {
            adapter.updatePresence(user)
        }
    }

    override fun onPerson(user: User, room: Room) {
        runOnUiThread {
            adapter.addPerson(user, room)
        }
    }

}
