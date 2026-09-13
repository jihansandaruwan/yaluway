package com.example.yaluway.util

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.example.yaluway.R
import java.io.File
import java.util.UUID

object ImageBinder {

    fun saveFromUri(context: Context, uri: Uri): String? {
        return try {
            val folder = File(context.filesDir, "images").apply { mkdirs() }
            val outFile = File(folder, UUID.randomUUID().toString() + ".jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                outFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return null
            outFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    fun bind(imageView: ImageView, path: String?, fallback: Int = R.drawable.yaluway_app_icon) {
        if (path.isNullOrBlank()) {
            imageView.setImageResource(fallback)
            return
        }
        if (path.startsWith("drawable:")) {
            val name = path.removePrefix("drawable:")
            val id = imageView.resources.getIdentifier(name, "drawable", imageView.context.packageName)
            imageView.setImageResource(if (id != 0) id else fallback)
            return
        }
        val file = File(path)
        if (file.exists()) {
            imageView.setImageURI(Uri.fromFile(file))
        } else {
            imageView.setImageResource(fallback)
        }
    }
}
