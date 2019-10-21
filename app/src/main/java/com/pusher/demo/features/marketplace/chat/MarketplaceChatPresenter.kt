package com.pusher.demo.features.marketplace.chat

import android.content.Context
import android.util.Log
import com.pusher.chatkit.*
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.chatkit.users.User
import com.pusher.demo.features.BasePresenter
import com.pusher.demo.features.marketplace.ChatkitManager
import com.pusher.util.Result

class MarketplaceChatPresenter :  BasePresenter<MarketplaceChatPresenter.View>(){

    interface View {
        fun onError(exception: String)
        fun onConnected(person: CurrentUser)
        fun onOtherMember(person: User)
        fun onMemberPresenceChanged(person: User)
        fun onMessageReceived(message: Message)
    }

    private val LOG_TAG = "DEMO_APP"
    private lateinit var room: Room

    fun connect() {
        subscribeToRoom()
    }

    private fun subscribeToRoom() {
        room = ChatkitManager.currentUser.rooms.find { room -> room.name == "buyer:seller" }!!

        //subscribe to the room
        ChatkitManager.currentUser.subscribeToRoomMultipart(
            roomId = room.id ,
            listeners = RoomListeners(
                onMultipartMessage = { message ->
                    view?.onMessageReceived(message)
                },
                onPresenceChange = { person ->
                    if (isViewAttached() &&
                            person.id != ChatkitManager.currentUser.id) {
                        view?.onMemberPresenceChanged(person)
                    }
                }
            ),
            messageLimit = 20,
            callback = { subscription ->
                //success
                getMembersForRoom(room)
            }
        )
    }

    private fun getMembersForRoom(room: Room){
        //get members for room
        ChatkitManager.currentUser.usersForRoom( room.id, callback = { result ->
            when (result) {
                is Result.Success -> {
                    result.value.let { members ->
                        //check we actually have another user to talk to
                        val otherMember = members.find { user-> user.id != ChatkitManager.currentUser.id }
                        if (otherMember == null) {
                            handleError("could not find the other user to talk to - " +
                                    "have you created the sample data?")
                        } else {
                            view?.onOtherMember(otherMember)
                        }
                    }
                }

                is Result.Failure -> {
                    handleError(result.error.reason)
                }

            }
        })
    }

    fun sendMessageToRoom(message: String) {

        ChatkitManager.currentUser.sendSimpleMessage(room, message,
            callback = { result ->
                when (result) {

                    //we handle the success automatically by display the message

                    is Result.Failure -> {
                        handleError(result.error.reason)
                    }

                }
        })
    }

    private fun handleError(error: String) {
        Log.e(LOG_TAG, error)
        view?.onError(error)
    }

}