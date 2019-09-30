package com.pusher.demo.features.marketplace

import android.content.Context
import com.pusher.chatkit.*
import com.pusher.chatkit.messages.multipart.Message
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

    private val INSTANCE_LOCATOR = "v1:us1:68fbe60c-9d50-4842-beb8-1a06f136eeba"
    private val TOKEN_PROVIDER_URL = "https://us1.pusherplatform.io/services/chatkit_token_provider/v1/68fbe60c-9d50-4842-beb8-1a06f136eeba/token"

    private lateinit var chatManager: ChatManager
    private lateinit var currentUser: CurrentUser

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

        val roomId = "buyer:seller"
        //subscribe to room
        currentUser.subscribeToRoomMultipart(
            roomId = roomId ,
            listeners = RoomListeners(
                onMultipartMessage = { message ->
                    if (isViewAttached()) {
                        view?.onMessageReceived(message)
                    }
                },
                onPresenceChange = { person ->
                    if (isViewAttached()) {
                        view?.onMemberPresenceChanged(person)
                    }
                }
            ),
            messageLimit = 20,
            callback = { subscription ->
                //success
            }
        )

        //get members for room
        currentUser.usersForRoom(roomId, callback = { result ->
            when (result) {
                is Result.Success -> {
                    if (isViewAttached()) {
                        //we can assume first because this is only a 1:1 group
                        view?.onOtherMember(result.value.first())
                    }
                }

                is Result.Failure -> {
                    //you'd want to handle this error differently
                    if (isViewAttached()) {
                        view?.onError(result.error.reason)
                    }
                }
            }
        })


    }
}