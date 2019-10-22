package com.pusher.demo.features.marketplace.seller

import android.content.Context
import android.util.Log
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.chatkit.users.User
import com.pusher.demo.features.BasePresenter
import com.pusher.demo.features.marketplace.ChatkitManager
import com.pusher.util.Result

class SellerPresenter :  BasePresenter<SellerPresenter.View>(){

    interface View {
        fun onConnected(user: CurrentUser)
        fun onError(error: String)
        fun onMemberPresenceChanged(user: User)
        fun onPerson(user: User, room: Room)
    }

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

    fun subscribeToRoom(room: Room) {
        //subscribe to the room
        ChatkitManager.currentUser.subscribeToRoomMultipart(
            roomId = room.id ,
            listeners = RoomListeners(
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
                            val error = "Couldn't find any other people in room " + room.name
                            Log.e(ChatkitManager.LOG_TAG, error)
                            view?.onError(error)
                        } else {
                            view?.onPerson(otherMember, room)
                        }
                    }
                }

                is Result.Failure -> {
                    Log.e(ChatkitManager.LOG_TAG, result.error.reason)
                    view?.onError(result.error.reason)
                }

            }
        })
    }
}