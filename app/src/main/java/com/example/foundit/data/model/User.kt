package com.example.foundit.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = ""
) : Parcelable
