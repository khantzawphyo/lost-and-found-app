package com.example.foundit.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Formats a Unix timestamp into a readable date string.
 */
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Generates a unique filename for an image.
 *
 * This function creates a globally unique identifier (UUID)
 * and appends the provided file extension, ensuring that
 * every uploaded image has a distinct name.
 *
 * @param fileExtension The file extension for the image (e.g., ".jpg", ".png").
 * @return A unique string that can be used as a filename.
 */
fun generateUniqueImageName(fileExtension: String): String {
    // Generate a UUID (Universally Unique Identifier) to ensure the filename is unique.
    val uniqueId = UUID.randomUUID().toString()

    // Combine the unique ID with the file extension.
    return "$uniqueId$fileExtension"
}