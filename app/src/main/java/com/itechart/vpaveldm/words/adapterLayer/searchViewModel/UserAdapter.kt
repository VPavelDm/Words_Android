package com.itechart.vpaveldm.words.adapterLayer.searchViewModel

import android.graphics.drawable.Icon
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.dataLayer.user.User
import kotlinx.android.synthetic.main.recycler_user.view.*

class UserAdapter(
        private val users: List<User>,
        private val listener: ISubscribeUser,
        private val greenIcon: Icon,
        private val blackIcon: Icon
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(user = users[position])
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var user: User? = null

        init {
            itemView.subscribeButton.setOnClickListener {
                user?.let { user -> listener.subscribe(user) }
            }
        }

        fun bind(user: User) {
            this.user = user
            itemView.nicknameTV.text = user.name
            itemView.subscribeButton.setImageIcon(if (user.isSubscriber) greenIcon else blackIcon)
        }
    }

}

interface ISubscribeUser {
    fun subscribe(user: User)
}