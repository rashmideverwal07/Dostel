package com.igdtuw.dostel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ComplaintActivity : AppCompatActivity() {

    private lateinit var descriptionEditText: EditText
    private lateinit var charCountTextView: TextView
    private lateinit var priorityLow: Button
    private lateinit var priorityMedium: Button
    private lateinit var priorityHigh: Button
    private lateinit var locationEditText: EditText
    private lateinit var spinner: Spinner
    private lateinit var mediaPreview: ImageView
    private lateinit var btnAddMedia: Button

    private var selectedPriority: String = "Low"
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var selectedMediaUri: Uri? = null
    private val PICK_MEDIA_REQUEST = 101

    private fun requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO
            )
            requestPermissions(permissions, 101)
        } else {
            val permissions = arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            requestPermissions(permissions, 101)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complain)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        spinner = findViewById(R.id.spinner_complaint_type)
        val complaintTypes = arrayOf("Maintenance", "Cleanliness", "Noise", "Other")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, complaintTypes)

        descriptionEditText = findViewById(R.id.edit_description)
        locationEditText = findViewById(R.id.editTextLocation)
        charCountTextView = findViewById(R.id.char_count)
        mediaPreview = findViewById(R.id.media_preview)
        btnAddMedia = findViewById(R.id.btn_add_media)

        // Character counter
        descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                charCountTextView.text = "${s?.length ?: 0}/300"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Priority buttons
        priorityLow = findViewById(R.id.priority_low)
        priorityMedium = findViewById(R.id.priority_medium)
        priorityHigh = findViewById(R.id.priority_high)

        val priorityButtons = listOf(priorityLow, priorityMedium, priorityHigh)
        fun selectPriority(button: Button, level: String, colorRes: Int) {
            selectedPriority = level
            priorityButtons.forEach {
                it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            }
            button.setBackgroundColor(ContextCompat.getColor(this, colorRes))
        }

        priorityLow.setOnClickListener { selectPriority(priorityLow, "Low", android.R.color.holo_green_light) }
        priorityMedium.setOnClickListener { selectPriority(priorityMedium, "Medium", android.R.color.holo_orange_light) }
        priorityHigh.setOnClickListener { selectPriority(priorityHigh, "High", android.R.color.holo_red_light) }

        // Add media
        btnAddMedia.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "*/*"
            val mimeTypes = arrayOf("image/*", "video/*")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, PICK_MEDIA_REQUEST)
        }

        // Submit complaint
        val submitButton: Button = findViewById(R.id.btn_submit)
        submitButton.setOnClickListener {
            val complaintType = spinner.selectedItem.toString()
            val location = locationEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val priority = selectedPriority
            val userId = auth.currentUser?.uid

            if (userId == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedMediaUri != null) {
                val mediaRef = storage.reference.child("complaints/${UUID.randomUUID()}")
                mediaRef.putFile(selectedMediaUri!!)
                    .addOnSuccessListener {
                        mediaRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            saveComplaintToFirestore(
                                userId,
                                complaintType,
                                location,
                                description,
                                priority,
                                downloadUri.toString()
                            )
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Media upload failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                saveComplaintToFirestore(userId, complaintType, location, description, priority, null)
            }
        }
    }

    private fun saveComplaintToFirestore(
        userId: String,
        type: String,
        location: String,
        description: String,
        priority: String,
        mediaUrl: String?
    ) {
        val complaintData = hashMapOf(
            "userId" to userId,
            "type" to type,
            "location" to location,
            "description" to description,
            "priority" to priority,
            "mediaUrl" to mediaUrl,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("complaints")
            .add(complaintData)
            .addOnSuccessListener {
                Toast.makeText(this, "Complaint submitted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit complaint", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_MEDIA_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedMediaUri = data?.data
            mediaPreview.setImageURI(selectedMediaUri)
            mediaPreview.visibility = ImageView.VISIBLE
        }
    }
}
