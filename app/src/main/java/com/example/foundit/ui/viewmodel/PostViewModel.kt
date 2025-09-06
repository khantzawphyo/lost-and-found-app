package com.example.foundit.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.foundit.data.local.AppDatabase
import com.example.foundit.data.local.entities.Post
import com.example.foundit.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository
    val allPosts: LiveData<List<Post>>

    init {
        val postDao = AppDatabase.getDatabase(application).postDao()
        repository = PostRepository(postDao)
        allPosts = repository.allPosts
    }

    fun insert(post: Post) {
        viewModelScope.launch {
            repository.insert(post)
        }
    }

    fun delete(post: Post) {
        viewModelScope.launch {
            repository.delete(post)
        }
    }

    fun update(post: Post) {
        viewModelScope.launch {
            repository.update(post)
        }
    }
}
