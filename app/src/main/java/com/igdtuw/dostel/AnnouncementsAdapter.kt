package com.igdtuw.dostel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnnouncementsAdapter(private val announcements: List<Announcements>) :
    RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder>() {

    class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.announcementTitle)
        val date: TextView = itemView.findViewById(R.id.announcementDate)
        val content: TextView = itemView.findViewById(R.id.announcementContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_announcements, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.title.text = announcement.title
        holder.date.text = announcement.date
        holder.content.text = announcement.content
    }

    override fun getItemCount(): Int = announcements.size
}
