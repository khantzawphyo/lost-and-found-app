package com.example.foundit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.model.Post
import com.example.foundit.data.repository.AuthRepository
import com.example.foundit.data.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val postRepository: PostRepository = PostRepository
    private val authRepository: AuthRepository = AuthRepository

    val allPosts: Flow<List<Post>> = postRepository.getAllPosts()

    suspend fun getPostById(postId: String): Post? {
        return postRepository.getPostById(postId)
    }

    val myPosts: Flow<List<Post>> = postRepository.getPostsByUserId(authRepository.getCurrentUserId()!!)

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
