package com.example.foundit.utils

import android.widget.ImageView
import coil3.load
import coil3.request.placeholder
import com.example.foundit.R


object ImageLoader {

    /**
     * Loads an image from a URL into an ImageView.
     *
     * @param imageView The ImageView where the image will be displayed.
     * @param imageUrl The URL of the image to load. Can be null or an empty string.
     */
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        imageView.load(imageUrl) {
            placeholder(R.drawable.ic_placeholder)
        }
    }
}
