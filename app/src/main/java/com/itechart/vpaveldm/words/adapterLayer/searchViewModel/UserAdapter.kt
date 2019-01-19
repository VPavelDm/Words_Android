package com.itechart.vpaveldm.words.adapterLayer.searchViewModel

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.dataLayer.user.User
import kotlinx.android.synthetic.main.recycler_user.view.*

class UserAdapter(private val users: List<User>, private val listener: ISubscribeUser) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user = user)
        holder.itemView.subscribeButton.setOnClickListener {
            user.isSubscriber = !user.isSubscriber
            listener.subscribe(user, position)
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(user: User) {
            itemView.nicknameTV.text = user.name
            itemView.subscribeButton.setImageResource(
                if (user.isSubscriber) R.drawable.ic_track_green else R.drawable.ic_track
            )
        }
    }

}

interface ISubscribeUser {
    fun subscribe(user: User, position: Int)
}