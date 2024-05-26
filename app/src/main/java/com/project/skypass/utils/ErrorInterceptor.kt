package com.project.skypass.utils

import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            val errorMessage = parseErrorMessage(errorBody)
            throw HttpException(response.code, errorMessage)
        }

        return response
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val jsonObject = JSONObject(errorBody ?: "")
            if (jsonObject.getString("status") == "Failed") {
                jsonObject.getString("message")
            } else {
                "Unknown error"
            }
        } catch (e: Exception) {
            "Unknown error"
        }
    }

    class HttpException(val code: Int, message: String) : IOException(message)
}