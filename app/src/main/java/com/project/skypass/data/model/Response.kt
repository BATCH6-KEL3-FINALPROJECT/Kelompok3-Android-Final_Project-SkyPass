package com.project.skypass.data.model

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("is_success")
    val isSuccess: Boolean,
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: T,
    @SerializedName("message")
    val message: String
)