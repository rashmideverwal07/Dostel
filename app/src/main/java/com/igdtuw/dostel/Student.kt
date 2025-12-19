package com.igdtuw.dostel
import com.google.firebase.firestore.PropertyName

data class Student(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val enrollmentNumber: String = "",
    val profilePicUrl: String = "",
    val year: String = "",
    val branch: String = "",
    val roomNumber: String = "",
    val linkedin: String = "",
    val instagram: String = "",
    val isPublic: Boolean = false
)
