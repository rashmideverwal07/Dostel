package com.igdtuw.dostel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class SocialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private val db = FirebaseFirestore.getInstance()
    private val studentList = mutableListOf<Student>()
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        recyclerView = findViewById(R.id.socialRecyclerView)
        searchBar = findViewById(R.id.searchBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter(studentList)
        recyclerView.adapter = adapter

        fetchPublicProfiles()

        // Search bar functionality
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchPublicProfiles() {
        db.collection("users")
            .whereEqualTo("isPublic", true)
            .get()
            .addOnSuccessListener { documents ->
                studentList.clear()
                for (doc in documents) {
                    val student = doc.toObject(Student::class.java)
                    studentList.add(student)
                }

                if (studentList.isEmpty()) {
                    Toast.makeText(this, "No public profiles found", Toast.LENGTH_SHORT).show()
                }

                adapter.updateList(studentList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterList(query: String) {
        val filteredList = studentList.filter { student ->
            student.name.contains(query, ignoreCase = true) ||
                    student.branch.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}


