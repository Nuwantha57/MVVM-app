package com.eyepax.mvvm_app.utils

import android.util.Base64
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit

object CertificateHashHelper {

    private const val TAG = "CertHash"

    /**
     * Gets the SHA-256 certificate hash from the server
     * This bypasses any proxy to get the real server certificate
     */
    fun getCertificateHash() {
        Thread {
            try {
                Log.d(TAG, "Fetching certificate hash from server...")

                // Create OkHttpClient that bypasses proxy and trusts system certificates
                val client = OkHttpClient.Builder()
                    .proxy(Proxy.NO_PROXY)  // Bypass HTTP Toolkit proxy
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("https://jsonplaceholder.typicode.com")
                    .build()

                val response = client.newCall(request).execute()
                val handshake = response.handshake

                if (handshake != null) {
                    val certificates = handshake.peerCertificates

                    if (certificates.isNotEmpty()) {
                        Log.d(TAG, "Found ${certificates.size} certificates in chain")

                        // Get the first certificate (leaf certificate)
                        val cert = certificates[0] as X509Certificate

                        // Calculate SHA-256 hash
                        val sha256 = MessageDigest.getInstance("SHA-256")
                        val hash = sha256.digest(cert.encoded)
                        val pin = "sha256/${hash.toBase64()}"

                        // Log certificate information
                        Log.d(TAG, "================================")
                        Log.d(TAG, "Certificate Subject: ${cert.subjectDN}")
                        Log.d(TAG, "Certificate Issuer: ${cert.issuerDN}")
                        Log.d(TAG, "Valid From: ${cert.notBefore}")
                        Log.d(TAG, "Valid Until: ${cert.notAfter}")
                        Log.d(TAG, "================================")
                        Log.d(TAG, "SHA-256 Certificate Pin:")
                        Log.d(TAG, pin)
                        Log.d(TAG, "================================")
                        Log.d(TAG, "Copy this pin to your CertificatePinner configuration")

                        // Log all certificates in the chain for backup pins
                        if (certificates.size > 1) {
                            Log.d(TAG, "\nBackup Pins (Certificate Chain):")
                            certificates.forEachIndexed { index, certificate ->
                                val x509Cert = certificate as X509Certificate
                                val certHash = sha256.digest(x509Cert.encoded)
                                val certPin = "sha256/${certHash.toBase64()}"
                                Log.d(TAG, "Certificate $index: $certPin")
                            }
                        }

                    } else {
                        Log.e(TAG, "No certificates found in handshake")
                    }
                } else {
                    Log.e(TAG, "No handshake information available")
                }

                response.close()
                Log.d(TAG, "Certificate hash retrieval completed successfully")

            } catch (e: Exception) {
                Log.e(TAG, "Error getting certificate hash: ${e.message}", e)
                Log.e(TAG, "Make sure you have internet connection and the server is reachable")
            }
        }.start()
    }

    /**
     * Extension function to convert ByteArray to Base64 string
     */
    private fun ByteArray.toBase64(): String {
        return Base64.encodeToString(this, Base64.NO_WRAP)
    }
}
