package com.example.foundit.data.repository

import com.example.foundit.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object UserRepository {
    private val db by lazy { Firebase.firestore }
    private val usersCollection by lazy { db.collection("users") }

    suspend fun saveUser(user: User) {
        usersCollection.document(user.id).set(user).await()
    }

    suspend fun getUserById(userId: String): User? {
        return usersCollection.document(userId).get().await().toObject(User::class.java)
    }
}