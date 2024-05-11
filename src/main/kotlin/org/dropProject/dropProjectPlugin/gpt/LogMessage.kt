package org.dropProject.dropProjectPlugin.gpt

class LogMessage(
    private val author: String,
    private val content: String,
    private val localDateTime: java.time.LocalDateTime,
    private var model: String?,
    private var useful: Boolean?
) {
    override fun toString(): String {
        if (author == "ChatGPT") {

        }

        return """
            Author: $author
            Message: $content
            DateTime: $localDateTime
            Model: $model
        """.trimIndent()
    }
}