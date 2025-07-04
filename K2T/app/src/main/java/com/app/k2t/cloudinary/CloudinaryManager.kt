package com.app.k2t.cloudinary

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.app.k2t.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryManager(context: Context) {

    init {
        // Now use BuildConfig fields
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUD_NAME,
            "api_key" to BuildConfig.API_KEY,
            "api_secret" to BuildConfig.API_SECRET
        )
        MediaManager.init(context.applicationContext, config) // Use applicationContext for broader scope if needed
    }

    suspend fun uploadImage(uri: Uri): String {
        return suspendCancellableCoroutine { continuation ->
            val requestId = MediaManager.get().upload(uri)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            continuation.resume(url)
                        } else {
                            continuation.resumeWithException(Exception("Upload succeeded but URL is missing."))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resumeWithException(Exception("Upload failed: ${error.description}"))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        // It's good practice to handle rescheduling, perhaps by retrying or logging
                        // For simplicity here, we'll just log and not complete the coroutine,
                        // or you could resumeWithException.
                        // Log.w("CloudinaryManager", "Upload rescheduled: ${error.description}")
                        // continuation.resumeWithException(Exception("Upload rescheduled: ${error.description}"))
                    }
                }).dispatch()

            continuation.invokeOnCancellation {
                MediaManager.get().cancelRequest(requestId)
            }
        }
    }
}
