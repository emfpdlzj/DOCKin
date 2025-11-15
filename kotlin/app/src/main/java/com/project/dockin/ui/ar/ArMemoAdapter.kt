package com.project.dockin.ui.ar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.dockin.R
import com.project.dockin.data.api.ArApi

class ArMemoAdapter(
    private var items: List<ArApi.ArMemoDto>,
    private val onDeleteClick: (ArApi.ArMemoDto) -> Unit
) : RecyclerView.Adapter<ArMemoAdapter.MemoViewHolder>() {

    fun submitList(newItems: List<ArApi.ArMemoDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ar_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMemoText: TextView = itemView.findViewById(R.id.tvMemoText)
        private val tvMeta: TextView = itemView.findViewById(R.id.tvMeta)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(item: ArApi.ArMemoDto) {
            tvMemoText.text = item.memoText
            tvMeta.text = "${item.createdBy} / ${item.createdAt}"

            btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }
}