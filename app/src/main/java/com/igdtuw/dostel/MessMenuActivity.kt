package com.igdtuw.dostel

import android.graphics.Color
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MessMenuActivity : AppCompatActivity() {

    private lateinit var currentDayText: TextView
    private lateinit var breakfastTextView: TextView
    private lateinit var lunchTextView: TextView
    private lateinit var snacksTextView: TextView
    private lateinit var dinnerTextView: TextView
    private lateinit var rootLayout: LinearLayout

    private val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    private var currentIndex = 0
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mess_menu)

        // View bindings
        currentDayText = findViewById(R.id.currentDayText)
        breakfastTextView = findViewById(R.id.breakfastTextView)
        lunchTextView = findViewById(R.id.lunchTextView)
        snacksTextView = findViewById(R.id.snacksTextView)
        dinnerTextView = findViewById(R.id.dinnerTextView)
        rootLayout = findViewById(R.id.rootLayout)

        val prevDayBtn = findViewById<Button>(R.id.prevDayBtn)
        val nextDayBtn = findViewById<Button>(R.id.nextDayBtn)

        // Get today's day
        val today = getTodayDay()
        currentIndex = days.indexOf(today).takeIf { it >= 0 } ?: 0
        updateMenuForDay(days[currentIndex])

        // Button listeners
        prevDayBtn.setOnClickListener { goToPreviousDay() }
        nextDayBtn.setOnClickListener { goToNextDay() }
    }

    private fun getTodayDay(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun updateMenuForDay(day: String) {
        TransitionManager.beginDelayedTransition(rootLayout, AutoTransition())
        currentDayText.text = "$day's Menu"

        db.collection("Mess_menu").document(day)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    breakfastTextView.text = "Breakfast: ${document.getString("breakfast") ?: "N/A"}"
                    lunchTextView.text = "Lunch: ${document.getString("lunch") ?: "N/A"}"
                    snacksTextView.text = "Snacks: ${document.getString("snacks") ?: "N/A"}"
                    dinnerTextView.text = "Dinner: ${document.getString("dinner") ?: "N/A"}"
                } else {
                    showEmptyMenu(day)
                    Toast.makeText(this, "No menu found for $day", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                showEmptyMenu(day)
                Toast.makeText(this, "Failed to load menu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEmptyMenu(day: String) {
        breakfastTextView.text = "Breakfast: N/A"
        lunchTextView.text = "Lunch: N/A"
        snacksTextView.text = "Snacks: N/A"
        dinnerTextView.text = "Dinner: N/A"
    }

    private fun goToNextDay() {
        if (currentIndex < days.size - 1) {
            currentIndex++
            updateMenuForDay(days[currentIndex])
        }
    }

    private fun goToPreviousDay() {
        if (currentIndex > 0) {
            currentIndex--
            updateMenuForDay(days[currentIndex])
        }
    }
}
