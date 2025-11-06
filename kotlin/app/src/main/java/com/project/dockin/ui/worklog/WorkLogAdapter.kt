package com.project.dockin.ui.worklog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.format.DateFormat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.dockin.R
import com.project.dockin.data.db.WorkLogLocal
import android.view.View
import android.widget.TextView

class WorkLogAdapter :
    ListAdapter<WorkLogLocal, WorkLogAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<WorkLogLocal>() {
        override fun areItemsTheSame(a: WorkLogLocal, b: WorkLogLocal) = a.localId == b.localId
        override fun areContentsTheSame(a: WorkLogLocal, b: WorkLogLocal) = a == b
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvTitle)
        val meta: TextView  = v.findViewById(R.id.tvMeta)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_worklog, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)
        h.title.text = item.title
        val ts = DateFormat.format("yyyy-MM-dd HH:mm", item.updatedAt)
        h.meta.text = "localId=${item.localId}  •  sync=${item.syncState}  •  $ts"
    }
}