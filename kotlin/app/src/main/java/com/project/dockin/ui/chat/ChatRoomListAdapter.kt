package com.project.dockin.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.dockin.R
import com.project.dockin.data.chat.ChatRoom

class ChatRoomListAdapter(
    private val onClick: (ChatRoom) -> Unit
) : RecyclerView.Adapter<ChatRoomListAdapter.VH>() {

    private var items: List<ChatRoom> = emptyList()

    fun submitList(list: List<ChatRoom>) {
        items = list
        notifyDataSetChanged()
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomName: TextView = itemView.findViewById(R.id.tvRoomName)
        val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_room, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvRoomName.text = item.name
        holder.tvLastMessage.text = item.lastMessage
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}