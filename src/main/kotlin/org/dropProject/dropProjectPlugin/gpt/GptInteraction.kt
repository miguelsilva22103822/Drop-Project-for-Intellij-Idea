package org.dropProject.dropProjectPlugin.gpt

import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.dropProject.dropProjectPlugin.settings.SettingsState
import java.util.concurrent.TimeUnit

class GptInteraction {

    private var responseLog = ArrayList<GPTResponse>()
    private var chatLog = ArrayList<Message>()
    private var messages = mutableListOf(
        Message("system", "You are a helpful assistant"),
    )

    fun executePrompt(prompt: String): String {
        val message = Message("user", prompt)

        messages.add(message)
        val chatGptResponse = processPrompt()

        //add prompt and response to chatLog
        chatLog.add(message)
        chatLog.add(Message("system", chatGptResponse))

        return responseLog.last().choices.first().message.content
    }

    fun getChatLog(): String {
        var log = ""

        for (message in chatLog) {
            if (message.role == "user")
            {
                log += "User: " + message.content + "\n"
            } else {
                log += "ChatGPT: " + message.content + "\n"
            }
        }

        return log
    }

    fun getChatLogHtml(): String {
        var log = ""

        for (message in chatLog) {
            if (message.role == "user")
            {
                log += "User: " + message.content + "<br><br>"
            } else {
                log += "ChatGPT: " + message.content + "<br><br>"
            }
        }

        log.removeSuffix("<br><br>")

        return log
    }

    private fun processPrompt(): String {

        val settingsState = SettingsState.getInstance()
        val apiKey = settingsState.openAiToken


        val apiUrl = "https://api.openai.com/v1/chat/completions"

        val messagesJson = messages.joinToString(",") {
            """
            {
                "role": "${it.role}",
                "content": "${it.content}"
            }
            """
        }

        val requestBody =
            """
            {
                "model": "gpt-3.5-turbo",
                "messages": [$messagesJson]
            }
            """.trimIndent()

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        try {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return ""
            }

            val json = response.body?.string()
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(GPTResponse::class.java)
            val myResponse = adapter.fromJson(json!!) ?: return ""

            client.connectionPool.evictAll()

            responseLog.add(myResponse)
            return myResponse.choices.first().message.content

        } catch (exception : Exception) {
            //mostrar uma notificação a dizer que o chatgpt não respondeu
            return ""
        }
    }
}