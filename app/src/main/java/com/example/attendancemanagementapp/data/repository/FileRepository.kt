package com.example.attendancemanagementapp.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import java.net.URLDecoder
import javax.inject.Inject

class FileRepository @Inject constructor(@ApplicationContext private val context: Context) {
    fun savePdf(
        body: ResponseBody,
        contentDisposition: String?,
        fallback: String
    ): Uri {
        val fileName = extractFileName(contentDisposition, fallback)
        return savePdfToDownloads(body, fileName)
    }

    // 파일명 추출
    private fun extractFileName(contentDisposition: String?, fallback: String): String {
        if (contentDisposition.isNullOrBlank()) return fallback

        val idx = contentDisposition.indexOf("filename=")
        if (idx == -1) return fallback

        var raw = contentDisposition.substring(idx + "filename=".length)
            .substringBefore(";")
            .trim()
            .removePrefix("\"")
            .removeSuffix("\"")

        val decoded = runCatching { URLDecoder.decode(raw, "UTF-8") }.getOrNull()
        return (decoded ?: fallback).ifBlank { fallback }
    }

    // pdf 파일 저장
    private fun savePdfToDownloads(body: ResponseBody, fileName: String): Uri {
        val safeName = if (fileName.endsWith(".pdf", ignoreCase = true)) fileName else "$fileName.pdf"

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, safeName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // "Download/"
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("MediaStore insert failed")

        resolver.openOutputStream(uri)?.use { output ->
            body.byteStream().use { input ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("openOutputStream failed")

        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)

        return uri
    }
}