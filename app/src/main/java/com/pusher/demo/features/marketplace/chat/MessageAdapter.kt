package com.pusher.demo.features.marketplace.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.demo.R

class MessageAdapter(private val currentUserId: String,
                     private val messageDisplayedListener : (Message) -> Unit)
    : androidx.recyclerview.widget.RecyclerView.Adapter<MessageViewHolder>() {

    private var messages = mutableListOf<Message>()

    private var lastReadByOtherMemberMessageId = -1

    private val TYPE_MESSAGE_FROM_ME = 1
    private val TYPE_MESSAGE_FROM_OTHER = 2

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MessageViewHolder{
        return if (p1 == TYPE_MESSAGE_FROM_ME) {
            MessageFromMeViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_message_from_me, parent, false)
            )
        } else {
            MessageFromOtherViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_message_from_other, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentUserId == messages[position].sender.id) {
            TYPE_MESSAGE_FROM_ME
        } else {
            TYPE_MESSAGE_FROM_OTHER
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        if (getItemViewType(position) == TYPE_MESSAGE_FROM_ME) {

            val messageHolder = holder as MessageFromMeViewHolder

            val message = messages[position]
            messageHolder.bind(message)

            val currentUserMessage = message.sender.id == currentUserId
            val read = message.id <= lastReadByOtherMemberMessageId
            messageHolder.markAsRead(currentUserMessage && read)

            messageDisplayedListener(message)
        } else {
            val messageHolder = holder as MessageFromOtherViewHolder

            val message = messages[position]
            messageHolder.bind(message)

            val currentUserMessage = message.sender.id == currentUserId
            val read = message.id <= lastReadByOtherMemberMessageId
            messageHolder.markAsRead(currentUserMessage && read)

            messageDisplayedListener(message)
        }

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