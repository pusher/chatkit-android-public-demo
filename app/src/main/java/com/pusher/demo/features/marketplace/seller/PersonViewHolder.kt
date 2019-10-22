package com.pusher.demo.features.marketplace.seller

import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.pusher.chatkit.presence.Presence
import com.pusher.chatkit.users.User
import com.pusher.demo.R
import kotlinx.android.synthetic.main.row_person.view.*

class PersonViewHolder (itemView: View)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun bind(user: User, unreadCount: Int, context: Context){

        itemView.lblProfile.text = user.name

        if (user.presence == Presence.Online) {
            val unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.icon_profile)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.green))

            itemView.imgProfile.setImageDrawable(wrappedDrawable)
        } else {

            val unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.icon_profile_outline)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.dark_text))

            itemView.imgProfile.setImageDrawable(wrappedDrawable)
        }

        if (unreadCount == 0) {
            itemView.lblUnreadCount.visibility = View.GONE
        } else if (unreadCount > 99) {
            itemView.lblUnreadCount.text = ":D"
        } else {
            itemView.lblUnreadCount.text = unreadCount.toString()
        }

    }

}