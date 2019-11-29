package com.pusher.demo.features.marketplace.chat

import android.os.Handler
import android.util.Log
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
        fun onConnected(person: CurrentUser)

        fun onOtherMember(person: User)
        fun onOtherMemberPresenceChanged(person: User)
        fun onOtherMemberReadCursorChanged(messageId: Int)

        fun onMessageReceived(message: Message)

        fun onError(exception: String)
    }

    private val handler = Handler()

    private lateinit var room: Room

    private var lastReadByCurrentUserMessageId = -1

    fun connect() {
        subscribeToRoom()
    }

    private fun subscribeToRoom() {
        if (ChatkitManager.currentUser == null) {
            handleError("Current user was not found - have you signed in?")
            return
        }

        room = currentUser().rooms.find { room -> room.name == "buyer:seller" }!!

        //subscribe to the room
        currentUser().subscribeToRoomMultipart(
            roomId = room.id ,
            listeners = RoomListeners(
                onMultipartMessage = { message ->
                    runOnInitThread {
                        view?.onMessageReceived(message)
                    }
                },
                onPresenceChange = { person ->
                    runOnInitThread {
                        if (person.id != currentUser().id) {
                            view?.onOtherMemberPresenceChanged(person)
                        }
                    }
                },
                onNewReadCursor = { cursor ->
                    runOnInitThread {
                        if (cursor.userId != currentUser().id) {
                            view?.onOtherMemberReadCursorChanged(cursor.position)
                        }
                    }
                }
            ),
            messageLimit = 20,
            callback = { subscription ->
                //success
                runOnInitThread {
                    getCurrentUserReadCursor(room)
                    getOtherMemberInfo(room)
                }
            }
        )
    }

    private fun getOtherMemberInfo(room: Room){
        //get members for room
        currentUser().usersForRoom( room.id, callback = { result ->
            runOnInitThread {
                when (result) {
                    is Result.Success -> {
                        result.value.let { members ->
                            //check we actually have another user to talk to
                            val otherMember = members.find { user -> user.id != currentUser().id }
                            if (otherMember == null) {
                                handleError(
                                    "could not find the other user to talk to - " +
                                            "have you created the sample data?"
                                )
                            } else {
                                getOtherMemberReadCursor(room, otherMember)
                                view?.onOtherMember(otherMember)
                            }
                        }
                    }

                    is Result.Failure -> {
                        handleError(result.error.reason)
                    }

                }
            }
        })
    }

    private fun getOtherMemberReadCursor(room: Room, otherMember : User) {
        val readCursor = currentUser().getReadCursor(room, otherMember).successOrThrow()
        view?.onOtherMemberReadCursorChanged(readCursor.position)
    }

    private fun getCurrentUserReadCursor(room: Room) {
        val readCursor = currentUser().getReadCursor(room).successOrThrow()
        lastReadByCurrentUserMessageId = readCursor.position
    }

    fun onMessageDisplayed(message: Message) {
        if (message.sender.id != currentUser().id) {
            if (lastReadByCurrentUserMessageId < message.id) {
                lastReadByCurrentUserMessageId = message.id
                updateReadCursor(room.id, lastReadByCurrentUserMessageId)
            }
        }
    }

    fun sendMessageToRoom(message: String) {
        currentUser().sendSimpleMessage(room, message,
            callback = { result ->
                runOnInitThread {
                    when (result) {

                        //we handle the success automatically by display the message

                        is Result.Failure -> {
                            handleError(result.error.reason)
                        }

                    }
                }
        })
    }

    private fun updateReadCursor(roomId: String, messageId: Int) {
        currentUser().setReadCursor(roomId, messageId)
    }

    private fun handleError(error: String) {
        Log.e(ChatkitManager.LOG_TAG, error)
        view?.onError(error)
    }

    private fun runOnInitThread(runnable: () -> Unit) = handler.post(runnable)

}

private fun currentUser() = ChatkitManager.currentUser!!