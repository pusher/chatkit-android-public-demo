package com.pusher.demo.features.marketplace.chat

import android.view.View
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.messages.multipart.Payload
import com.pusher.demo.R
import kotlinx.android.synthetic.main.row_message.view.*

class MessageViewHolder (itemView: View)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun bind(message: Message, currentUserId: String){
        val inlineMessage: Payload.Inline = message.parts[0].payload as Payload.Inline
        if (message.sender.id == currentUserId) {
            itemView.lblMessageFromYou.visibility = View.VISIBLE
            itemView.lblMessageFromYou.text = inlineMessage.content
            itemView.lblMessageFromOther.visibility = View.GONE
        } else {
            itemView.lblMessageFromOther.visibility = View.VISIBLE
            itemView.lblMessageFromOther.text = inlineMessage.content
            itemView.lblMessageFromYou.visibility = View.GONE
        }
    }

    fun markAsRead(read: Boolean) {
        val sentOrReadDrawableResId = if (read) {
            R.drawable.read_indicator
        } else {
            R.drawable.sent_indicator
        }

        itemView.lblMessageFromYou.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
            sentOrReadDrawableResId, 0)
    }

}