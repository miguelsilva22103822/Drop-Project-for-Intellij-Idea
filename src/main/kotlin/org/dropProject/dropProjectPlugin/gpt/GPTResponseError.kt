package org.dropProject.dropProjectPlugin.gpt

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "error")
    val error: Error
)

@JsonClass(generateAdapter = true)
data class Error(
    @Json(name = "message")
    val message: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "param")
    val param: String?,
    @Json(name = "code")
    val code: String
)
