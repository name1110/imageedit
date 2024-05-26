package com.name1110.photoeditor.getaccesstoken


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
class BaiduAuth(private val clientId: String, private val clientSecret: String) {

    private val httpClient = OkHttpClient.Builder().build()

    suspend fun getAccessToken(): String {
        return withContext(Dispatchers.IO) {
            val mediaType = "application/json".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, "")
            val request = Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=$clientId&client_secret=$clientSecret&grant_type=client_credentials")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string() ?: throw IOException("Empty response body")
            val jsonResponse = JSONObject(responseBody)
            jsonResponse.getString("access_token")
        }
    }
}
