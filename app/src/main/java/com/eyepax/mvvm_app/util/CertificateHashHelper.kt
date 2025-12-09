package com.eyepax.mvvm_app.utils

import android.util.Log
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.cert.X509Certificate

object CertificateHashHelper {

    private const val TAG = "CertHash"

    /**
     * Get certificate hash from server
     * Replace with YOUR API URL
     */
    fun getCertificateHash(url: String = "https://jsonplaceholder.typicode.com") {
        Thread {
            try {
                val client = OkHttpClient.Builder().build()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                response.handshake?.peerCertificates?.forEachIndexed { index, cert ->
                    val hash = CertificatePinner.pin(cert)

                    // Get subject from X509Certificate (not deprecated)
                    val x509Cert = cert as? X509Certificate
                    val subject = x509Cert?.subjectX500Principal?.name ?: "Unknown"

                    Log.d(TAG, "=================================")
                    Log.d(TAG, "Certificate #$index")
                    Log.d(TAG, "Hash: $hash")
                    Log.d(TAG, "Subject: $subject")
                    Log.d(TAG, "=================================")
                }

                response.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}", e)
            }
        }.start()
    }
}
