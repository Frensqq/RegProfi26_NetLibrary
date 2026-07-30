package com.example.netlibrary.data.remote

import android.util.Log
import com.example.netlibrary.domain.model.Project
import com.example.netlibrary.domain.model.RequestProject
import com.google.gson.Gson
import io.ktor.http.HttpMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

class PBOkHttp {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private suspend fun executeRequest(
        url: String,
        token: String,
        method: String,
        body: okhttp3.RequestBody,
        logMessage: String
    ): String = withContext(Dispatchers.IO){
        Log.d("OkHttpUploader", logMessage)

        val request = Request.Builder()
            .url(url)
            .method(method, body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""
            Log.d("OkHttpUploader", "Response code: ${response.code}")

            if (!response.isSuccessful){
                Log.e("OkHttpUploader", "Error: $responseBody")
                throw Exception("Request failed: ${response.code}")
            }
            responseBody
        }
    }

    suspend fun postProjectImg(
        baseUrl: String,
        token: String,
        data: RequestProject
    ): Project{
        val url = "${baseUrl}collections/groups/records"

        val body = if (data.image?.exists() == true){
            Log.d("OkHttpUploader", "With image, size: ${data.image.length()} bytes")
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", data.user_id)
                .addFormDataPart("typeProject",data.typeProject)
                .addFormDataPart("gender",data.gender)
                .addFormDataPart("dateEnd",data.dateEnd)
                .addFormDataPart("title",data.title)
                .addFormDataPart("dateStart",data.dateStart)
                .addFormDataPart("category",data.category)
                .addFormDataPart("description_source",data.description_source)
                .addFormDataPart("avatar", data.image.name,data.image.asRequestBody("image/jpeg".toMediaType()))
                .build()
        }else{
            Log.d("OkHttpUploader", "Without image (JSON)")
            val dataMap = mapOf(
                "user_id" to data.user_id,
                "typeProject" to data.typeProject,
                "gender" to data.gender,
                "dateEnd" to data.dateEnd,
                "title" to data.title,
                "dateStart" to data.dateStart,
                "category" to data.category,
                "description_source" to data.description_source
            )
            gson.toJson(dataMap).toRequestBody("application/json".toMediaType())
        }

        val response = executeRequest(url, token, "POST", body, "Create project: ${data.title}")
        return gson.fromJson(response, Project::class.java)
    }
}