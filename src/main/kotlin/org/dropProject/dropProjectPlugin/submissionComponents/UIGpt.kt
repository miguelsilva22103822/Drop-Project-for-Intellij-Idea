package org.dropProject.dropProjectPlugin.submissionComponents

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dropProject.dropProjectPlugin.gpt.GptInteraction
import org.dropProject.dropProjectPlugin.settings.SettingsState
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.text.html.HTMLEditorKit


/*
class UIGpt {

    var gptInteraction = GptInteraction()

    fun buildComponents(): JBScrollPane {

        val panel = JPanel()

        val textField = JBTextField()
        textField.emptyText.text = "placeholder"

        val responseArea = JBTextArea("Response")
        responseArea.foreground = JBColor.WHITE
        responseArea.background = JBColor.BLACK

        //exprimentar a cena do editor n sei q que o gpt recomenda

        val button = JButton("Send Prompt")
        button.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {

                if (textField.text != null && textField.text != "") {
                    gptInteraction.executePrompt(textField.text)
                    responseArea.text = gptInteraction.getChatLog()
                }

            }
        })

        panel.add(button)
        panel.add(textField)
        panel.add(responseArea)


        val scrollPane = JBScrollPane(panel)
        val viewport: JViewport = scrollPane.viewport
        viewport.scrollMode = JViewport.SIMPLE_SCROLL_MODE
        scrollPane.horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        viewport.extentSize = Dimension(0, 0)

        return scrollPane
    }
}
*/

/*
class UIGpt {

    var gptInteraction = GptInteraction()

    fun buildComponents(): JBScrollPane {

        val panel = JPanel()

        val textField = JBTextField()
        textField.emptyText.text = "placeholder"

        val responseArea = JBTextArea("Response")
        responseArea.foreground = JBColor.WHITE
        responseArea.background = JBColor.BLACK



        val button = JButton("Send Prompt")
        button.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {

                if (textField.text != null && textField.text != "") {
                    gptInteraction.executePrompt(textField.text)
                    responseArea.text = gptInteraction.getChatLog()
                }

            }
        })

        panel.add(button)
        panel.add(textField)
        panel.add(responseArea)


        val scrollPane = JBScrollPane(panel)
        val viewport: JViewport = scrollPane.viewport
        viewport.scrollMode = JViewport.SIMPLE_SCROLL_MODE
        scrollPane.horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        viewport.extentSize = Dimension(0, 0)

        return scrollPane
    }
}
*/


class UIGpt(var project: Project) {


    private var gptInteraction = GptInteraction(project)
    private var textField = JBTextField()
    private var phrases = ArrayList<String>()
    private var sendButton = JButton()
    private var phraseComboBox = JComboBox(phrases.toTypedArray())
    private val responseArea = JEditorPane()
    private var inputAndSubmitPanel = JPanel(GridBagLayout())
    private var uI: JBScrollPane = JBScrollPane()
    private var chatTextToShow = ""
    private val cssStyle = """
        <style>
            * {
                font-family: Consolas, 'Courier New', monospace;
                font-size: 14px;
            }
        </style>
    """.trimIndent()

    private var askTwice = true;

    init {
        textField.emptyText.text = "Send a message"
        textField.preferredSize = Dimension(400, 30)  // Set a preferred size for the textField


        responseArea.contentType = "text/html"
        responseArea.setEditorKit(HTMLEditorKit())
        val baseFont = Font("Arial", Font.PLAIN, 15) // Change this to your desired font
        responseArea.setFont(baseFont)
        responseArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
        responseArea.text = cssStyle
        responseArea.isEditable = false

        val settingsState = SettingsState.getInstance()
        phrases = ArrayList(settingsState.sentenceList)

        phrases.add(0, "") //Option that doesn't add anything to the prompt

        phraseComboBox = JComboBox(phrases.toTypedArray())
        phraseComboBox.preferredSize = Dimension(200, 30)


        val scope = CoroutineScope(Dispatchers.Default)

        sendButton = JButton("Send Message")
        sendButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) = sendPrompt()
        })


// Define constraints for components with custom padding and sizes
        val textFieldConstraints = GridBagConstraints()
        textFieldConstraints.fill = GridBagConstraints.HORIZONTAL
        textFieldConstraints.weightx = 1.0
        textFieldConstraints.gridwidth = 2 // Span two columns
        textFieldConstraints.insets = JBUI.insets(3) // Custom padding

        val phraseComboBoxConstraints = GridBagConstraints()
        phraseComboBoxConstraints.fill = GridBagConstraints.HORIZONTAL
        phraseComboBoxConstraints.weightx = 1.0
        phraseComboBoxConstraints.insets = JBUI.insets(3) // Custom padding
        phraseComboBoxConstraints.gridx = 0 // Place at column 0
        phraseComboBoxConstraints.gridy = 1 // Place below the text field

        val sendButtonConstraints = GridBagConstraints()
        sendButtonConstraints.fill = GridBagConstraints.BOTH // Make the button occupy 2 lines
        sendButtonConstraints.insets = JBUI.insets(3) // Custom padding
        sendButtonConstraints.gridx = 2 // Place at column 2
        sendButtonConstraints.gridy = 0 // Place in the first row
        sendButtonConstraints.gridheight = 2 // Span 2 rows

// Add components to the panel with constraints
        inputAndSubmitPanel = JPanel(GridBagLayout())
        inputAndSubmitPanel.add(textField, textFieldConstraints)
        inputAndSubmitPanel.add(phraseComboBox, phraseComboBoxConstraints)
        inputAndSubmitPanel.add(sendButton, sendButtonConstraints)

// Set preferred size for the panel
        inputAndSubmitPanel.preferredSize = Dimension(600, 90) // Increased height to accommodate the taller button

// Add the panel and response area to the main panel
        val panel = JPanel()
        panel.layout = BorderLayout()
        panel.add(responseArea, BorderLayout.CENTER)
        panel.add(inputAndSubmitPanel, BorderLayout.SOUTH)

// Set up the scroll pane
        val scrollPane = JBScrollPane(panel)
        val viewport: JViewport = scrollPane.viewport
        viewport.scrollMode = JViewport.SIMPLE_SCROLL_MODE
        scrollPane.horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        viewport.extentSize = Dimension(0, 0)

// Assign scroll pane to UI
        uI = scrollPane

    }


    fun buildComponents(): JBScrollPane {

        //val editor: Editor = DataManager.getInstance().getDataContext().getData(DataConstants.EDITOR) as Editor
        //val editor2: Editor = DataManager.getInstance().dataContextFromFocusAsync.getData(DataConstants.EDITOR) as Editor
        //DataManager.getInstance().saveInDataContext(DataManager.getInstance().getDataContext(), Key.create("ObjectGPTUI"), this)
        //editor.putUserData(Key.create("ObjectGPTUI"), this)

        return uI
    }

    private fun escapeKotlinSpecialCharacters(input: String): String {
        // Define the characters to be escaped
        val specialCharacters = setOf("\\", "$", "\"", "'", "\n", "\r", "\t", "\b", "\u000c")

        // Escape each special character
        var escaped = input
        for (character in specialCharacters) {
            escaped = escaped.replace(character, "\\$character")
        }

        return escaped
    }

    fun addToPrompt(text : String) {
        textField.text += text
    }

    fun sendPrompt() {
        val scope = CoroutineScope(Dispatchers.Default)

        if (textField.text != null && textField.text != "") {
            sendButton.isEnabled = false

            val selectedPhrase = phraseComboBox.selectedItem as String
            val message = "${textField.text} $selectedPhrase"

            val escapedMessage = escapeKotlinSpecialCharacters(message)

            textField.text = ""

            // Start a coroutine to perform the GPT interaction
            scope.launch(Dispatchers.Default) {

                //Adding the prompt that is being sent
                gptInteraction.addPromptMessage(escapedMessage)
                chatTextToShow += "<p>User: $escapedMessage</p>\n"

                updateChatScreen()

                val response = gptInteraction.executePrompt(escapedMessage)
                //Adding the response
                chatTextToShow += "<p>ChatGPT: $response</p>\n"
                updateChatScreen()

                if (askTwice) {
                    val altResponse = gptInteraction.executePrompt(escapedMessage)

                    chatTextToShow += "<p>ChatGPT (ALternative Response): $altResponse</p>\n"
                    updateChatScreen()

                    SwingUtilities.invokeLater {
                        openDiffViewer(response, altResponse)
                    }
                }

                SwingUtilities.invokeLater {
                    sendButton.isEnabled = true
                }

            }
        }
    }

    private fun openDiffViewer(response1: String, response2: String) {
        val project = project

        val content1 = DiffContentFactory.getInstance().create(project, response1)
        val content2 = DiffContentFactory.getInstance().create(project, response2)

        val request = SimpleDiffRequest("Response Comparison", content1, content2, "Original Response", "Alternative Response")
        DiffManager.getInstance().showDiff(project, request)
    }

    private fun updateChatScreen() {

        val htmlCrazyWooo = "$cssStyle\n" +
                "$chatTextToShow\n"

        val parser = Parser.builder().build()
        val document = parser.parse(htmlCrazyWooo)
        val htmlRenderer = HtmlRenderer.builder().build()
        val htmlResponse = htmlRenderer.render(document)

        responseArea.text = htmlResponse

        println(htmlResponse)
    }

    fun updatePhrases() {
        val settingsState = SettingsState.getInstance()
        phrases = ArrayList(settingsState.sentenceList)

        phrases.add("") //Option that doesn't add anything to the prompt


        phraseComboBox.removeAll()
    }

    fun addPhrase(phrase : String) {
        phraseComboBox.addItem(phrase)
    }

    fun removePhrase(index : Int) {
        phraseComboBox.removeItemAt(index)
    }

    fun editPhrase(selectedIndex : Int, editedSentence : String) {
        phraseComboBox.removeItemAt(selectedIndex)
        phraseComboBox.insertItemAt(editedSentence, selectedIndex)
        phraseComboBox.selectedIndex = selectedIndex
    }

    companion object {
        var instance1 : UIGpt? = null

        fun getInstance(project: Project) : UIGpt {

            if(instance1 == null) {
                instance1 = UIGpt(project)
            }
            return instance1!!
        }
    }
}
