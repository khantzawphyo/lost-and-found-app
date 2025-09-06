package com.example.foundit.data.local.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey

@Parcelize
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val imageUri: String?,
    val postedBy: String,
    val isFound: Boolean,
    val phone: String,
    val email: String
) : Parcelable
