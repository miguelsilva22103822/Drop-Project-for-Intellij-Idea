package org.dropProject.dropProjectPlugin.gpt

import com.intellij.openapi.project.Project
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.dropProject.dropProjectPlugin.DefaultNotification
import org.dropProject.dropProjectPlugin.settings.SettingsState
import org.dropProject.dropProjectPlugin.toolWindow.DropProjectToolWindow
import java.util.concurrent.TimeUnit

class GptInteraction(var project: Project) {

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

        if (chatGptResponse.contains("Error code")) {
            return chatGptResponse
        }

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

        if (apiKey == "") {
            DefaultNotification.notify(project, "No API key set")
            return "Error: No API key set"
        }


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

            println("res0: $response")

            if (!response.isSuccessful) {
                val json = response.body?.string()
                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(ErrorResponse::class.java)
                println(json)
                val myResponse = adapter.fromJson(json!!) ?: return "didnt work"

                DefaultNotification.notify(project, "Response unsuccseessful, no tokens")

                return "Error code: {${myResponse.error.code}}"
            }

            println("res1: $response")

            val json = response.body?.string()
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(GPTResponse::class.java)
            println(json)
            val myResponse = adapter.fromJson(json!!) ?: return ""

            client.connectionPool.evictAll()

            responseLog.add(myResponse)
            return myResponse.choices.first().message.content

        } catch (exception : Exception) {
            //mostrar uma notificação a dizer que o chatgpt não respondeu
            return "Erro desconhecido"
        }
    }
}