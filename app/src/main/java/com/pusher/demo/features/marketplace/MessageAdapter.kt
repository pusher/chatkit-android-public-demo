package com.pusher.demo.features.marketplace

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.demo.R

class MessageAdapter(val context: Context, val currentUserId: String)
    : androidx.recyclerview.widget.RecyclerView.Adapter<MessageViewHolder>() {

    var messages = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_message, parent, false), context
        )
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position], currentUserId)
    }

    fun addMessage(message: Message) {
        this.messages.add(message)
        notifyItemInserted(this.messages.size)
    }


}