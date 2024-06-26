package org.dropProject.dropProjectPlugin.gpt

import com.google.gson.Gson

class LogMessage(
    private val author: String,
    private val content: String,
    private val localDateTime: java.time.LocalDateTime,
    private var model: String?,
    private var useful: Boolean?
) {
    override fun toString(): String {
        if (isFromGPT()) {
            return """
            Author: $author
            Message: $content
            DateTime: $localDateTime
            Model: $model
            Useful: $useful
            
            """.trimIndent()
        }

        return """
            Author: $author
            Message: $content
            DateTime: $localDateTime
            
        """.trimIndent()
    }

    fun isFromGPT(): Boolean {
        return author == "ChatGPT"
    }

    fun writeToJSON(): String {
        return Gson().toJson(this)
    }

    fun markAs(useful: Boolean?) {
        this.useful = useful
    }

    fun getContent(): String {
        return content
    }
}