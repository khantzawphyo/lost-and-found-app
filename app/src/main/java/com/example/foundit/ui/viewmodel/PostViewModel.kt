package com.example.foundit.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.model.Post
import com.example.foundit.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {


    // Exposes the list of posts as LiveData from the Flow in the repository
    val allPosts: LiveData<List<Post>> = PostRepository.getAllPosts().asLiveData()

    fun savePost(post: Post) {
        viewModelScope.launch {
            PostRepository.savePost(post)
        }
    }
}