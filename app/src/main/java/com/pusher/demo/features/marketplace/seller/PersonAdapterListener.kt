package com.pusher.demo.features.marketplace.seller

import com.pusher.chatkit.users.User

interface PersonAdapterListener {
    fun onPersonSelected(person: User)
}