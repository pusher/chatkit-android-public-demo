package com.pusher.demo.features.marketplace

import android.content.Context
import android.util.Log
import com.pusher.chatkit.*
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.chatkit.users.User
import com.pusher.demo.features.BasePresenter
import com.pusher.util.Result

class MarketplacePresenter :  BasePresenter<MarketplacePresenter.View>(){

    interface View {
        fun onError(exception: String)
        fun onConnected(person: CurrentUser)
        fun onOtherMember(person: User)
        fun onMemberPresenceChanged(person: User)
        fun onMessageReceived(message: Message)
    }

    private val LOG_TAG = "DEMO_APP"
    private val INSTANCE_LOCATOR = "FILL_ME_IN"
    private val TOKEN_PROVIDER_URL = "FILL_ME_IN"

    private lateinit var chatManager: ChatManager
    private lateinit var currentUser: CurrentUser

    private lateinit var room: Room

    fun connect(context: Context, userId: String) {

        //set up your chat manager with your instance locator and token provider
        chatManager = ChatManager(
            instanceLocator = INSTANCE_LOCATOR,
            userId = userId,
            dependencies = AndroidChatkitDependencies(
                tokenProvider = ChatkitTokenProvider(
                    endpoint = TOKEN_PROVIDER_URL,
                    userId = userId
                ),
                context = context
            )
        )

        //connect to chatkit
        chatManager.connect(
            listeners = ChatListeners(),
            callback = { result ->
                when (result) {

                    is Result.Success -> {
                        result.value.let { user ->
                            currentUser = user
                            view?.onConnected(user)
                            subscribeToRoom()
                        }
                    }

                    is Result.Failure -> {
                        handleError(result.error.reason)
                    }
                }

            }
        )

    }

    private fun subscribeToRoom() {

        val usersRoom =  currentUser.rooms.find { room -> room.name == "buyer:seller" }

        if (usersRoom == null) {
            handleError("Could not subscribe to buyer:seller room - have you created the sample data?")
            return
        }

        room = usersRoom

        //subscribe to the room
        currentUser.subscribeToRoomMultipart(
            roomId = room.id ,
            listeners = RoomListeners(
                onMultipartMessage = { message ->
                    view?.onMessageReceived(message)
                },
                onPresenceChange = { person ->
                    if (person.id != currentUser.id) {
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
        currentUser.usersForRoom( room.id, callback = { result ->
            when (result) {

                is Result.Success -> {
                    result.value.let { members ->
                        //check we actually have another user to talk to
                        val otherMember = members.find { user-> user.id != currentUser.id }
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

        currentUser.sendSimpleMessage(room, message,
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