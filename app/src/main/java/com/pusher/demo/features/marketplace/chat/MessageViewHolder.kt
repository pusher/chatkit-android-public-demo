package com.pusher.demo.features.marketplace.chat

import android.view.View
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.messages.multipart.Payload
import com.pusher.demo.R
import kotlinx.android.synthetic.main.row_message_from_other.view.*

open class MessageViewHolder (itemView: View)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun bind(message: Message){
        val inlineMessage: Payload.Inline = message.parts[0].payload as Payload.Inline
        itemView.lblMessage.text = inlineMessage.content
    }

    fun markAsRead(read: Boolean) {
        val sentOrReadDrawableResId = if (read) {
            R.drawable.read_indicator
        } else {
            R.drawable.sent_indicator
        }

        itemView.lblMessage.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
            sentOrReadDrawableResId, 0)
    }

}