package com.example.foundit.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

object ImageUploader {

    private const val FREEIMAGE_UPLOAD_URL = "https://freeimage.host/api/1/upload"
    private const val FREEIMAGE_API_KEY = "6d207e02198a847aa98d0a2a901485a5"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Uploads an image from a content URI to freeimage.host and returns the public URL.
     * @param context The application context.
     * @param imageUri The content URI of the image.
     * @return The public URL of the uploaded image, or null if the upload fails.
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): String? {
        return try {
            val imageFile = createTempImageFile(context, imageUri)
            if (imageFile == null) {
                Log.e("ImageUploader", "Failed to create temporary image file.")
                return null
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", FREEIMAGE_API_KEY)
                .addFormDataPart("action", "upload")
                .addFormDataPart("format", "json")
                .addFormDataPart(
                    "source",
                    imageFile.name,
                    imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url(FREEIMAGE_UPLOAD_URL)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseData = response.body?.string()
                val jsonResponse = JSONObject(responseData)

                // The example JSON shows the success message is in a nested object.
                // We check for the presence of the "success" object and the "image" object.
                if (jsonResponse.has("success") && jsonResponse.has("image")) {
                    val imageUrl = jsonResponse.getJSONObject("image").getString("url")
                    Log.d("ImageUploader", "Image uploaded successfully: $imageUrl")
                    imageUrl
                } else {
                    // If the "success" and "image" objects are not present, assume failure.
                    // Log the full response to help with debugging.
                    Log.e("ImageUploader", "Image upload failed. Response: ${jsonResponse.toString(2)}")
                    null
                }
            } else {
                Log.e("ImageUploader", "Image upload failed with code: ${response.code}")
                null
            }
        } catch (e: Exception) {
            Log.e("ImageUploader", "Error during image upload", e)
            null
        }
    }

    /**
     * Creates a temporary file from the content URI with a unique filename using a UUID.
     * @param context The application context.
     * @param imageUri The content URI of the image.
     */
    private fun createTempImageFile(context: Context, imageUri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val uniqueFileName = "temp_img_${UUID.randomUUID()}.jpg"
            val tempFile = File(context.cacheDir, uniqueFileName)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: IOException) {
            Log.e("ImageUploader", "Error creating temporary file", e)
            null
        }
    }
}

