package com.semyon.keyholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryItemAdapter : RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {

    var items = listOf<History>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            name.text = item.name
            nick.text = item.nick
            from.text = item.from
            to.text = item.to
        }
    }

    inner class HistoryItemViewHolder(
        view: View,
        val name: TextView = view.findViewById(R.id.name),
        val nick: TextView = view.findViewById(R.id.nick),
        val from: TextView = view.findViewById(R.id.from),
        val to: TextView = view.findViewById(R.id.to)
    ) : RecyclerView.ViewHolder(view)
}