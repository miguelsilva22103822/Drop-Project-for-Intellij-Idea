package org.dropProject.dropProjectPlugin.gpt

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.*

class MarkdownRenderer : JFrame() {

    init {
        title = "Markdown Renderer"
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(1200, 900)
        setLocationRelativeTo(null)

        val markdown = "Proposta 1:\n" +
                "```kotlin\n" +
                "data class ChatLog(\n" +
                "    val interactionTitle,\n" +
                "    val firstMessage: Message,\n" +
                "    val assignment: Assignment,\n" +
                "    val author: Author\n" +
                ")\n" +
                "\n" +
                "//firstMessage é a primeira mensagem da conversa, é possivel aceder à próxima mensagem a partir do atributo nextMessage.\n" +
                "//Um assignment pode ter vários chatLogs associados(cada nova conversa é um novo chatLog).\n" +
                "\n" +
                "data class Message(\n" +
                "    val text: String,\n" +
                "    val dateTime: Date,\n" +
                "    val ischatGPTResponse: Boolean,\n" +
                "    val previousMessage: Message?,\n" +
                "    val nextMessage: Message?\n" +
                ")\n" +
                "\n" +
                "//dateTime é a data e hora a que foi enviada/recebida a mensagem.\n" +
                "//isChatGPTResponse identifica se a a mensagem é uma prompt do utilizador ou uma resposta do GPT.\n" +
                "```\n" +
                "\n" +
                "Proposta 2:\n" +
                "```kotlin\n" +
                "data class ChatLog(\n" +
                "    val interactionTitle,\n" +
                "    val messageLog: MutableList<Message>,\n" +
                "    val assignment: Assignment,\n" +
                "    val author: Author\n" +
                ")\n" +
                "\n" +
                "//O chatLog tem a lista de mensagens da conversa, tanto do aluno como as respostas do chatgpt.\n" +
                "//Um assignment pode ter vários chatLogs associados(cada nova conversa é um novo chatLog).\n" +
                "\n" +
                "data class Message(\n" +
                "    val text: String,\n" +
                "    val dateTime: Date,\n" +
                "    val ischatGPTResponse: Boolean,\n" +
                ")\n" +
                "\n" +
                "//dateTime é a data e hora a que foi enviada/recebida a mensagem.\n" +
                "//isChatGPTResponse identifica se a a mensagem é uma prompt do utilizador ou uma resposta do GPT.\n" +
                "```"

        // Parse Markdown
        val parser = Parser.builder().build()
        val document = parser.parse(markdown)

        // Render HTML
        val renderer = HtmlRenderer.builder().build()
        val html = renderer.render(document)


        val path = System.getProperty("user.dir")

        //println("Working Directory = $path")


        val jSPath = "resources/prism.js"

        val cSSPath = "resources/prism.css"


        val jSFile = File(jSPath)
        val cSSFile = File(cSSPath)

        println(jSFile.absolutePath)
        println(cSSFile.absolutePath)

        var jSContent = ""
        var cSSContent = ""

        if (jSFile.exists() && cSSFile.exists()) {
            jSContent = jSFile.readText()
            cSSContent = cSSFile.readText()
        }

        //println(jSContent)
        //println(cSSContent)

        val styledHtml = "<html><head>" +
                "<style>$cSSContent</style>" +
                "<script>$jSContent</script>" +
                "</head><body>" +
                html +
                "</body></html>"

        //println(styledHtml)

        // Display HTML in a JEditorPane
        val editorPane = JEditorPane()
        editorPane.contentType = "text/html"
        editorPane.text = styledHtml
        editorPane.isEditable = false

        val scrollPane = JScrollPane(editorPane)
        contentPane.add(scrollPane, BorderLayout.CENTER)
    }

}

fun main() {


    SwingUtilities.invokeLater {
        MarkdownRenderer().isVisible = true
    }
}


