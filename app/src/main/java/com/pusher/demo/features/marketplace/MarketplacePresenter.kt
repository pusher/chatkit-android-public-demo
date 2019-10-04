package com.pusher.demo.features.marketplace

import android.content.Context
import com.pusher.chatkit.*
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.chatkit.users.User
import com.pusher.demo.features.BasePresenter
import com.pusher.util.Result
import elements.Error

class MarketplacePresenter :  BasePresenter<MarketplacePresenter.View>(){

    interface View {
        fun onError(exception: String)
        fun onConnected(person: CurrentUser)
        fun onOtherMember(person: User)
        fun onMemberPresenceChanged(person: User)
        fun onMessageReceived(message: Message)
    }

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
                            if (isViewAttached()) {
                                view?.onConnected(user)
                            }

                            subscribeToRoom()
                        }
                    }

                    is Error -> {
                        if (isViewAttached()) {
                            view?.onError(result.reason)
                        }
                    }
                }
            }
        )

    }

    private fun subscribeToRoom() {

        room = currentUser.rooms.find { room -> room.name == "buyer:seller" }!!

        //subscribe to the room
        currentUser.subscribeToRoomMultipart(
            roomId = room.id ,
            listeners = RoomListeners(
                onMultipartMessage = { message ->
                    if (isViewAttached()) {
                        view?.onMessageReceived(message)
                    }
                },
                onPresenceChange = { person ->
                    if (isViewAttached() &&
                            person.id != currentUser.id) {
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
                    if (isViewAttached()) {
                        view?.onOtherMember(result.value.find { user-> user.id != currentUser.id }!!)
                    }
                }

                is Result.Failure -> {
                    //you'd probably want to handle this error differently
                    if (isViewAttached()) {
                        view?.onError(result.error.reason)
                    }
                }
            }
        })
    }

    fun sendMessageToRoom(message: String) {

        currentUser.sendSimpleMessage(room, message,
            callback = { result ->
                when (result) {
                    is Result.Success -> {
                        // message is already displayed
                    }
                    is Result.Failure -> {
                        if (isViewAttached()){
                            view?.onError(result.error.reason)
                        }
                    }
                }
        })
    }

}