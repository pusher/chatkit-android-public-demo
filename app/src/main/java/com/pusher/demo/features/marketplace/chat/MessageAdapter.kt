package com.pusher.demo.features.marketplace.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.demo.R

class MessageAdapter(private val currentUserId: String,
                     private val messageDisplayedListener : MessageDisplayedListener)
    : androidx.recyclerview.widget.RecyclerView.Adapter<MessageViewHolder>() {

    interface MessageDisplayedListener {
        fun onMessageDisplayed(message: Message)
    }

    private var messages = mutableListOf<Message>()

    private var lastReadByOtherMemberMessageId = -1

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_message, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message, currentUserId)

        val isCurrentUserMessage = message.sender.id == currentUserId
        val read = message.id <= lastReadByOtherMemberMessageId
        holder.markAsRead(isCurrentUserMessage && read)

        messageDisplayedListener.onMessageDisplayed(message)
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size)
    }

    fun markAsReadUpTo(messageId: Int) {
        lastReadByOtherMemberMessageId = messageId
        notifyDataSetChanged()
    }

}