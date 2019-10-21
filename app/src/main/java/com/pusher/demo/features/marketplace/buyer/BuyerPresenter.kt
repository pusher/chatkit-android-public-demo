package com.pusher.demo.features.marketplace.buyer

import android.content.Context
import com.pusher.chatkit.CurrentUser
import com.pusher.demo.features.BasePresenter
import com.pusher.demo.features.marketplace.ChatkitManager

class BuyerPresenter : BasePresenter<BuyerPresenter.View>(){

    interface View {
        fun onConnected(user: CurrentUser)
        fun onError(error: String)
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
}