package com.example.foundit.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val date: String = "",
    val imageUri: String? = null,
    val postedBy: String = "",
    val isFound: Boolean = false,
    val phone: String = "",
    val email: String = ""
) : Parcelable