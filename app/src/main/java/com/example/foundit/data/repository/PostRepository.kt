package com.example.foundit.data.repository

import com.example.foundit.data.model.Post
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object PostRepository {

    private val db = Firebase.firestore
    private val postsCollection = db.collection("posts")

    fun getAllPosts(): Flow<List<Post>> = callbackFlow {
        val listenerRegistration = postsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val posts = snapshot.toObjects(Post::class.java)
                trySend(posts)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun getPostById(postId: String): Post? {
        val document = postsCollection.document(postId).get().await()
        return document.toObject(Post::class.java)
    }

    suspend fun savePost(post: Post) {
        postsCollection.add(post).await()
    }
}