package com.example.foundit.data.repository

import com.example.foundit.data.model.Post
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object PostRepository {

    private val db by lazy { Firebase.firestore }
    private val postsCollection by lazy { db.collection("posts") }

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

    fun getPostsByUserId(userId: String): Flow<List<Post>> = callbackFlow {
        val listenerRegistration = postsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
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
        // Add a new document to the collection
        val newDocumentRef = postsCollection.add(post).await()
        // Get the new ID from the document reference
        val newId = newDocumentRef.id
        // Update the document to include the ID
        postsCollection.document(newId).update("id", newId).await()
    }

    suspend fun updatePost(post: Post) {
        postsCollection.document(post.id).set(post).await()
    }

    suspend fun deletePost(postId: String) {
        postsCollection.document(postId).delete().await()
    }
}
