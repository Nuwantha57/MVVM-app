package com.eyepax.mvvm_app.view

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.network.ApiClient
import com.eyepax.mvvm_app.utils.CertificateHashHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import javax.net.ssl.SSLPeerUnverifiedException

class SSLPinningTestActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private lateinit var btnGetHash: Button
    private lateinit var btnTestSecure: Button
    private lateinit var btnTestInsecure: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ssl_test)

        tvResult = findViewById(R.id.tvResult)
        btnGetHash = findViewById(R.id.btnGetHash)
        btnTestSecure = findViewById(R.id.btnTestSecure)
        btnTestInsecure = findViewById(R.id.btnTestInsecure)

        btnGetHash.setOnClickListener {
            tvResult.text = "Getting certificate hash...\nCheck Logcat for 'CertHash' tag"
            CertificateHashHelper.getCertificateHash()
        }

        btnTestSecure.setOnClickListener {
            testSecureConnection()
        }

        btnTestInsecure.setOnClickListener {
            testInsecureConnection()
        }
    }

    private fun testSecureConnection() {
        tvResult.text = "Testing with SSL Pinning..."

        lifecycleScope.launch {
            try {
                // Get OkHttpClient directly
                val client: OkHttpClient = ApiClient.provideOkHttpClient()

                val request = Request.Builder()
                    .url("https://jsonplaceholder.typicode.com/posts/1")
                    .build()

                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    tvResult.text = "✅ SSL PINNING ACTIVE!\n\n" +
                            "Connection successful with pinned certificate.\n\n" +
                            "Your app is protected against MITM attacks.\n\n" +
                            "Status: ${response.code}\n" +
                            "Response: ${responseBody?.take(100)}..."
                } else {
                    tvResult.text = "❌ Connection failed: ${response.code}"
                }

                response.close()

            } catch (e: SSLPeerUnverifiedException) {
                tvResult.text = "✅ SSL PINNING WORKING!\n\n" +
                        "Certificate mismatch detected.\n" +
                        "Connection blocked by SSL Pinning.\n\n" +
                        "This means your app successfully blocked an invalid certificate!\n\n" +
                        "Error: ${e.message}"
            } catch (e: Exception) {
                tvResult.text = "❌ Error: ${e.message}"
            }
        }
    }


    private fun testInsecureConnection() {
        tvResult.text = "Testing WITHOUT SSL Pinning..."

        lifecycleScope.launch {
            try {
                val client = OkHttpClient.Builder().build()
                val request = Request.Builder()
                    .url("https://jsonplaceholder.typicode.com/posts/1")
                    .build()

                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    tvResult.text = "⚠️ INSECURE CONNECTION!\n\n" +
                            "Connection succeeded WITHOUT SSL Pinning.\n\n" +
                            "This connection is vulnerable to MITM attacks.\n\n" +
                            "Status: ${response.code}"
                    response.close()
                } else {
                    tvResult.text = "❌ Connection failed: ${response.code}"
                    response.close()
                }

            } catch (e: Exception) {
                tvResult.text = "❌ Error: ${e.message}\n\n${e.stackTraceToString()}"
            }
        }
    }
}
