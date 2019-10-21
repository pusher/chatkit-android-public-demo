package com.pusher.demo.features.marketplace.seller

import android.content.Context
import com.pusher.chatkit.CurrentUser
import com.pusher.demo.features.BasePresenter
import com.pusher.demo.features.marketplace.ChatkitManager

class SellerPresenter :  BasePresenter<SellerPresenter.View>(){

    interface View {
        fun onConnected(user: CurrentUser)
        fun onError(error: String)
    }

    fun connectToChatkit(context: Context) {

        ChatkitManager.connect(
            context,
            "buyer",
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