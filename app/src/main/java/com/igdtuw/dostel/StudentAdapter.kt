package com.igdtuw.dostel

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StudentAdapter(private var studentList: List<Student>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameEditText)
        val branchYear: TextView = itemView.findViewById(R.id.branchYearEditText)
        val linkedin: TextView = itemView.findViewById(R.id.linkedinEditText)
        val instagram: TextView = itemView.findViewById(R.id.instagramEditText)
        val profilePic: ImageView = itemView.findViewById(R.id.profilePic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_card, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.name.text = student.name
        holder.branchYear.text = "${student.branch} | Year ${student.year}"


        Glide.with(holder.profilePic.context)
            .load(student.profilePicUrl)
            .placeholder(R.drawable.ic_profile)
            .into(holder.profilePic)

        holder.linkedin.setOnClickListener {
            val url = student.linkedin
            if (url.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            } else {
            // Show a message if the LinkedIn URL is empty
            Toast.makeText(it.context, "LinkedIn URL not available", Toast.LENGTH_SHORT).show()
        }
        }

        holder.instagram.setOnClickListener {
            val url = student.instagram
            if (url.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }else {
                // Show a message if the Instagram URL is empty
                Toast.makeText(it.context, "Instagram URL not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = studentList.size
}
