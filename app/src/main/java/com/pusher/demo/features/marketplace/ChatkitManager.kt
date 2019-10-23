package com.pusher.demo.features.marketplace

import android.content.Context
import com.pusher.chatkit.*
import com.pusher.chatkit.ChatManager
import com.pusher.util.Result
import elements.Error

object ChatkitManager {

    private val INSTANCE_LOCATOR = "FILL_ME_IN"
    private val TOKEN_PROVIDER_URL = "FILL_ME_IN"
    val LOG_TAG = "DEMO_APP"

    private lateinit var chatManager: ChatManager
    var currentUser: CurrentUser? = null

    interface ChatManagerConnectedListener{
        fun onConnected(user: CurrentUser)
        fun onError(error: String)
    }

    fun connect(context: Context, userId: String, listener: ChatManagerConnectedListener) {

        //check if we're already connected
        if (currentUser != null){
            //if we already have a current user let's sign them out first
            chatManager.close {
                when (it) {
                    is Result.Success -> {
                        connectToChatkit(context, userId, listener)
                    }
                    is Result.Failure -> {
                        listener.onError(it.error.reason)
                    }
                }
            }
        } else {
            connectToChatkit(context, userId, listener)
        }

    }

    private fun connectToChatkit(context: Context, userId: String, listener: ChatManagerConnectedListener) {

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
                            listener.onConnected(user)
                        }
                    }

                    is Result.Failure -> {
                        listener.onError(result.error.reason)
                    }
                }
            }
        )
    }
}