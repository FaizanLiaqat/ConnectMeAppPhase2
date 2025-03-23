package com.example.connectmeapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView


class CommentsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var userProfileImage: CircleImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var addCommentEditText: EditText
    private lateinit var postCommentButton: Button

    private val commentsList = mutableListOf<CommentModel>()
    private var postId: String? = null
    private lateinit var commentsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        postId = intent.getStringExtra("POST_ID")
        if (postId == null) {
            Toast.makeText(this, "Post ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId!!)

        toolbar = findViewById(R.id.comments_toolbar)
        userProfileImage = findViewById(R.id.user_profile_image)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Comments"
            setDisplayHomeAsUpEnabled(true)
        }

        loadUserProfileImage()

        recyclerView = findViewById(R.id.recyclerview_comments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(commentsList)
        recyclerView.adapter = commentAdapter

        addCommentEditText = findViewById(R.id.add_comment)
        postCommentButton = findViewById(R.id.post_comment)
        postCommentButton.setOnClickListener {
            val commentText = addCommentEditText.text.toString().trim()
            if (commentText.isNotEmpty()) {
                postComment(commentText)
            } else {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }

        loadComments()
    }

    private fun loadUserProfileImage() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageBase64 = snapshot.child("profileImageUrl").getValue(String::class.java) ?: ""
                val bitmap = decodeBase64(profileImageBase64)
                if (bitmap != null) {
                    userProfileImage.setImageBitmap(bitmap)
                } else {
                    userProfileImage.setImageResource(R.drawable.profile_placeholder)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun postComment(commentText: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java) ?: "Unknown"
                val profileImageBase64 = snapshot.child("profileImageUrl").getValue(String::class.java) ?: ""

                val newComment = CommentModel(
                    commentId = commentsRef.push().key ?: System.currentTimeMillis().toString(),
                    userId = userId,
                    username = username,
                    profileImageBase64 = profileImageBase64,
                    text = commentText,
                    timestamp = System.currentTimeMillis()
                )

                commentsRef.child(newComment.commentId).setValue(newComment)
                    .addOnSuccessListener {
                        Toast.makeText(this@CommentsActivity, "Comment posted", Toast.LENGTH_SHORT).show()
                        addCommentEditText.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@CommentsActivity, "Failed to post comment: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadComments() {
        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentsList.clear()

                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(CommentModel::class.java)
                    if (comment != null) {
                        commentsList.add(comment)
                    }
                }
                commentsList.sortBy { it.timestamp }
                commentAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun decodeBase64(base64String: String?): Bitmap? {
        return try {
            if (base64String.isNullOrEmpty()) return null
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
