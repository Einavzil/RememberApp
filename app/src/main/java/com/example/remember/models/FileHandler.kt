package com.example.remember.models

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class FileHandler(private val context: Context) {

    fun saveImageToInternalStorage(uri: Uri?): String? {
        if (uri == null) return null

        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "saved_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}