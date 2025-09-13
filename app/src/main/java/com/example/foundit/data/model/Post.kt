package com.example.foundit.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val timestamp: Long = 0,
    val imageUrl: String? = null,
    val postedBy: String = "",
    val found: Boolean = false,
    val phone: String = "",
    val email: String = ""
) : Parcelable {
    constructor() : this("", "", "", "", "", 0, "", "", false, "", "")
}
