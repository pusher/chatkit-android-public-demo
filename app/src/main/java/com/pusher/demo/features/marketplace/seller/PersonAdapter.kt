package com.pusher.demo.features.marketplace.seller

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.users.User
import com.pusher.demo.R

data class Person(val roomId: String, var person: User, var unreadCount: Int)

class PersonAdapter(private val context: Context, val listener: PersonAdapterListener)
    : androidx.recyclerview.widget.RecyclerView.Adapter<PersonViewHolder>() {

    private var people = mutableListOf<Person>()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PersonViewHolder {
        return PersonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_person, parent, false), listener
        )
    }

    override fun getItemCount(): Int {
        return people.size
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = people[position]
        holder.bind(person.person, person.unreadCount, context)
    }

    fun addPerson(person: User, room: Room) {
        //todo: how do we fix this ->
        people.add(Person(room.id, person, room.unreadCount!!))
        notifyItemInserted(people.size)
    }

    fun updatePresence(person: User) {
        for (p in people) {
            if (p.person.id == person.id) {
               p.person = person
                notifyItemChanged(people.indexOf(p))
            }
        }
    }



}