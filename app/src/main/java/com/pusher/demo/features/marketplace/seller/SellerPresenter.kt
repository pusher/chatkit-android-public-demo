package com.pusher.demo.features.marketplace.seller

import android.content.Context
import android.util.Log
import com.pusher.chatkit.ChatListeners
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.presence.Presence
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.users.User
import com.pusher.demo.features.BasePresenter
import com.pusher.demo.features.marketplace.ChatkitManager

class SellerPresenter :  BasePresenter<SellerPresenter.View>(){

    interface View {
        fun onConnected(user: CurrentUser)
        fun onError(error: String)
        fun onMemberPresenceChanged(user: User)
        fun onPerson(user: User, room: Room)
        fun onUnreadCountChanged(room: Room)
    }

    private lateinit var chatListeners: ChatListeners

    fun connectToChatkit(context: Context) {

        ChatkitManager.connect(
            context,
            "seller",
            object : ChatkitManager.ChatManagerConnectedListener {
                override fun onConnected(user: CurrentUser) {
                    view?.onConnected(user)
                }

                override fun onError(error: String) {
                    view?.onError(error)
                }
            })
    }

    fun endSubscriptionToRoomUpdates() {
        ChatkitManager.removeChatListener(chatListeners)
    }

    fun subscribeToRoomUpdates() {

        if (ChatkitManager.currentUser == null) {
            view?.onError("Current user was not found - have you signed in?")
            Log.e(ChatkitManager.LOG_TAG, "Current user was not found - have you signed in?")
            return
        }

        chatListeners = ChatListeners(
            onRoomUpdated = {
                view?.onUnreadCountChanged(it)
            },
            onPresenceChanged = { user: User, newPresence: Presence, oldPresence: Presence ->
                view?.onMemberPresenceChanged(user)
            }
        )

        ChatkitManager.addChatListener(chatListeners)

        ChatkitManager.getUsersFromMyJoinedRooms(object: ChatkitManager.JoinedRoomsMembersListener {
            override fun onMembersFetched(members: List<User>) {
                reconcileFetchedBuyers(members)
            }

            override fun onError(error: String) {
                //todo: handle this D:
            }

        })

    }

    private fun reconcileFetchedBuyers(buyers: List<User>) {

        for (room in ChatkitManager.currentUser!!.rooms) {

            val otherMemberId = room.memberUserIds.find { userId-> userId != ChatkitManager.currentUser!!.id }!!
            val otherMember = buyers.find{ user -> user.id == otherMemberId}!!
            view?.onPerson(otherMember, room)

        }

    }

}
