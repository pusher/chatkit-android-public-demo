package com.pusher.demo.features.marketplace.chat

import android.view.View
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.messages.multipart.Payload
import kotlinx.android.synthetic.main.row_message.view.*


class MessageViewHolder (itemView: View)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun bind(message: Message, currentUserId: String){

        val inlineMessage: Payload.Inline = message.parts[0].payload as Payload.Inline
        if (message.sender.id == currentUserId) {
            itemView.lblMessageFromYou.visibility = View.VISIBLE
            itemView.lblMessageFromYou.text = inlineMessage.content
        } else {
            itemView.lblMessageFromOther.visibility = View.VISIBLE
            itemView.lblMessageFromOther.text = inlineMessage.content
        }

    }

}