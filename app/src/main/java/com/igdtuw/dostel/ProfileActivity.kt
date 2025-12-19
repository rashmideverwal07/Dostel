package com.igdtuw.dostel

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent

class ProfileActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var enrollmentEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var branchEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var roomEditText: EditText
    private lateinit var linkedinEditText: EditText
    private lateinit var instagramEditText: EditText
    private lateinit var publicSwitch: Switch
    private lateinit var saveButton: Button
    private lateinit var editButton: Button
    private lateinit var logoutButton: Button
    private lateinit var progressBar: ProgressBar
    private var isEditing = false

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Link UI elements
        nameEditText = findViewById(R.id.nameEditText)
        enrollmentEditText = findViewById(R.id.enrollmentEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        branchEditText = findViewById(R.id.branchEditText)
        yearEditText = findViewById(R.id.yearEditText)
        roomEditText = findViewById(R.id.roomEditText)
        linkedinEditText = findViewById(R.id.linkedinEditText)
        instagramEditText = findViewById(R.id.instagramEditText)
        publicSwitch = findViewById(R.id.publicSwitch)
        saveButton = findViewById(R.id.saveButton)
        editButton = findViewById(R.id.editButton)
        logoutButton = findViewById(R.id.logoutButton)
        progressBar = findViewById(R.id.progressBar)

        emailEditText.setText(currentUser?.email)
        emailEditText.isEnabled = false

        // Load user data
        loadProfile()

        // Initially disable editing
        setEditingEnabled(false)

        editButton.setOnClickListener {
            isEditing = !isEditing
            setEditingEnabled(isEditing)
            editButton.text = if (isEditing) "Cancel" else "Edit"
        }

        saveButton.setOnClickListener {
            if (!validateForm()) return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Confirm Save")
                .setMessage("Are you sure you want to save changes?")
                .setPositiveButton("Yes") { _, _ -> saveProfile() }
                .setNegativeButton("No", null)
                .show()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadProfile() {
        progressBar.visibility = View.VISIBLE
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        nameEditText.setText(doc.getString("name"))
                        enrollmentEditText.setText(doc.getString("enrollment"))
                        phoneEditText.setText(doc.getString("phone"))
                        branchEditText.setText(doc.getString("branch"))
                        yearEditText.setText(doc.getString("year"))
                        roomEditText.setText(doc.getString("room"))
                        linkedinEditText.setText(doc.getString("linkedin"))
                        instagramEditText.setText(doc.getString("instagram"))
                        publicSwitch.isChecked = doc.getBoolean("isPublic") ?: false
                    }
                    progressBar.visibility = View.GONE
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfile() {
        val user = currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val profileData = hashMapOf(
            "name" to nameEditText.text.toString().trim(),
            "enrollment" to enrollmentEditText.text.toString().trim(),
            "email" to currentUser?.email,
            "phone" to phoneEditText.text.toString().trim(),
            "branch" to branchEditText.text.toString().trim(),
            "year" to yearEditText.text.toString().trim(),
            "room" to roomEditText.text.toString().trim(),
            "linkedin" to linkedinEditText.text.toString().trim(),
            "instagram" to instagramEditText.text.toString().trim(),
            "isPublic" to publicSwitch.isChecked,
            "uid" to currentUser?.uid
        )

        progressBar.visibility = View.VISIBLE
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).set(profileData)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
                    setEditingEnabled(false)
                    isEditing = false
                    editButton.text = "Edit"
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setEditingEnabled(enabled: Boolean) {
        nameEditText.isEnabled = enabled
        enrollmentEditText.isEnabled = enabled
        phoneEditText.isEnabled = enabled
        branchEditText.isEnabled = enabled
        yearEditText.isEnabled = enabled
        roomEditText.isEnabled = enabled
        linkedinEditText.isEnabled = enabled
        instagramEditText.isEnabled = enabled
        publicSwitch.isEnabled = enabled
        saveButton.isEnabled = enabled
    }

    private fun validateForm(): Boolean {
        val phone = phoneEditText.text.toString().trim()

        if (nameEditText.text.isNullOrEmpty()) {
            nameEditText.error = "Name cannot be empty"
            return false
        }
        if (enrollmentEditText.text.isNullOrEmpty()) {
            enrollmentEditText.error = "Enrollment cannot be empty"
            return false
        }
        if (phone.isNotEmpty() && phone.length != 10) {
            phoneEditText.error = "Enter valid 10-digit phone number"
            return false
        }
        return true
    }
}