package com.example.foundit.data.repository

import androidx.lifecycle.LiveData
import com.example.foundit.data.local.PostDao
import com.example.foundit.data.local.entities.Post

class PostRepository(private val postDao: PostDao) {

    val allPosts: LiveData<List<Post>> = postDao.getAllPosts()

    suspend fun insert(post: Post) {
        postDao.insertPost(post)
    }

    suspend fun update(post: Post) {
        postDao.updatePost(post)
    }

    suspend fun delete(post: Post) {
        postDao.deletePost(post)
    }
}
