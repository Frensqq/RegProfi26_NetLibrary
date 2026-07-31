package com.example.netlibrary.data.remote

import com.example.netlibrary.domain.model.Project
import com.example.netlibrary.domain.model.RequestProject
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PBOkHttpUploader {

    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun postProjectImg(
        token: String,
        data: RequestProject
    ): Project = withContext(Dispatchers.IO) {

        val body = data.image?.takeIf { it.exists() }?.let { image ->
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", data.user_id)
                .addFormDataPart("typeProject", data.typeProject)
                .addFormDataPart("gender", data.gender)
                .addFormDataPart("dateEnd", data.dateEnd)
                .addFormDataPart("title", data.title)
                .addFormDataPart("dateStart", data.dateStart)
                .addFormDataPart("category", data.category)
                .addFormDataPart(
                    "description_source",
                    data.description_source
                )
                .addFormDataPart(
                    "avatar",
                    image.name,
                    image.asRequestBody("image/*".toMediaType())
                )
                .build()
        } ?: gson.toJson(
            mapOf(
                "user_id" to data.user_id,
                "typeProject" to data.typeProject,
                "gender" to data.gender,
                "dateEnd" to data.dateEnd,
                "title" to data.title,
                "dateStart" to data.dateStart,
                "category" to data.category,
                "description_source" to data.description_source
            )
        ).toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("${PBApiServis.BASE_URL}collections/project/records")
            .bearerAuth(token)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val json = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                error("${response.code}: $json")
            }

            gson.fromJson(json, Project::class.java)
        }
    }

    private fun Request.Builder.bearerAuth(token: String) =
        header("Authorization", "Bearer $token")
}