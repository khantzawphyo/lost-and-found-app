package com.example.foundit.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.model.Post
import com.example.foundit.data.repository.AuthRepository
import com.example.foundit.data.repository.PostRepository
import com.example.foundit.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val postRepository: PostRepository = PostRepository
    private val authRepository: AuthRepository = AuthRepository
    private val userRepository: UserRepository = UserRepository
    private val db by lazy { Firebase.firestore }
    private val postsCollection by lazy { db.collection("posts") }

    val allPosts: Flow<List<Post>> = postRepository.getAllPosts()

    suspend fun getPostById(postId: String): Post? {
        return postRepository.getPostById(postId)
    }

    val myPosts: Flow<List<Post>> = postRepository.getPostsByUserId(authRepository.getCurrentUserId()!!)

    suspend fun createAndSavePost(
        title: String,
        description: String,
        location: String,
        timestamp: Long,
        imageUrl: String?,
        found: Boolean,
        phone: String,
        email: String
    ) {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            val user = userRepository.getUserById(userId)
            val postedBy = user?.name ?: "Anonymous"

            val newPostId = postsCollection.document().id

            val newPost = Post(
                id = newPostId,
                userId = userId,
                title = title,
                description = description,
                location = location,
                timestamp = timestamp,
                imageUrl = imageUrl,
                found = found,
                phone = phone,
                email = email,
                postedBy = postedBy
            )
            postRepository.savePost(newPost)

            Log.d("PostViewModel", "Post saved successfully with ID: $newPostId")
        } else {
            throw IllegalStateException("User not authenticated.")
        }
    }


    fun savePost(post: Post) {
        viewModelScope.launch {
            postRepository.savePost(post)
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            postRepository.updatePost(post)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
        }
    }
}