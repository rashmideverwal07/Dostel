package com.igdtuw.dostel

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Linking each card with a function (start with Toast for testing)
        //1
        val complaintCard = findViewById<CardView>(R.id.card_complaint)
        complaintCard.setOnClickListener {
            // Remove the Toast if you want to open the ComplaintActivity directly
            val intent = Intent(this@MainActivity, ComplaintActivity::class.java)
            startActivity(intent)
        }
        //2
        val socialHubCard = findViewById<CardView>(R.id.card_socialhub)
        socialHubCard.setOnClickListener {
            val intent = Intent(this, SocialActivity::class.java)
            startActivity(intent)
        }
        //3
        val announcementCard = findViewById<CardView>(R.id.card_announcement)
        announcementCard.setOnClickListener {
            startActivity(Intent(this, AnnouncementsActivity::class.java))
        }
        //4
        val hostelInfoCard = findViewById<CardView>(R.id.card_hostelinfo)
        hostelInfoCard.setOnClickListener {
            val intent = Intent(this, HostelinfoActivity::class.java)
            startActivity(intent)
        }

        //5
        val profileCard = findViewById<CardView>(R.id.card_profile)

        profileCard.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)

        }

        //6
        val messMenuCard = findViewById<CardView>(R.id.card_messmenu)

        messMenuCard.setOnClickListener {
            val intent = Intent(this, MessMenuActivity::class.java)  // Navigate to Mess Menu activity
            startActivity(intent)
        }
    }
}
