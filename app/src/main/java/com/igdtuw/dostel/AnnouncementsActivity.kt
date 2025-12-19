package com.igdtuw.dostel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AnnouncementsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnnouncementsAdapter
    private val announcementList = mutableListOf<Announcements>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcements)

        recyclerView = findViewById(R.id.announcementRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AnnouncementsAdapter(announcementList)
        recyclerView.adapter = adapter

        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        db.collection("announcements")
            .get()
            .addOnSuccessListener { documents ->
                announcementList.clear()
                for (doc in documents) {
                    val announcement = doc.toObject(Announcements::class.java)
                    announcementList.add(announcement)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch announcements", Toast.LENGTH_SHORT).show()
            }
    }
}
