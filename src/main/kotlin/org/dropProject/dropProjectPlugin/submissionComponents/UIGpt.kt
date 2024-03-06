package org.dropProject.dropProjectPlugin.submissionComponents

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
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

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


class UIGpt {

    var gptInteraction = GptInteraction()
    var textField = JBTextField()
    private var phrases = ArrayList<String>()
    private var sendButton = JButton()
    private var phraseComboBox = JComboBox(phrases.toTypedArray())
    private val responseArea = JEditorPane()
    private var inputAndSubmitPanel = JPanel(GridBagLayout())
    private var uI: JBScrollPane = JBScrollPane()
    val cssStyle =
        """
            <style>
                body {
                    font-family: Arial, sans-serif; /* Set the base font-family */
                    font-size: 12px; /* Set the base font size */
                }
            </style>
            """

    init {
        textField.emptyText.text = "Send a message"
        textField.preferredSize = Dimension(400, 30)  // Set a preferred size for the textField



        responseArea.contentType = "text/html"
        responseArea.text = "$cssStyle<html><body></body></html>"

        responseArea.isEditable = false

        val settingsState = SettingsState.getInstance()
        phrases = ArrayList(settingsState.sentenceList)

        phrases.add(0, "") //Option that doesn't add anything to the prompt

        phraseComboBox = JComboBox(phrases.toTypedArray())
        phraseComboBox.preferredSize = Dimension(200, 30)


        val scope = CoroutineScope(Dispatchers.Default)

        sendButton = JButton("Send Message")
        sendButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                if (textField.text != null && textField.text != "") {
                    sendButton.isEnabled = false

                    val selectedPhrase = phraseComboBox.selectedItem as String
                    val message = "${textField.text} $selectedPhrase"

                    val escapedMessage = escapeKotlinSpecialCharacters(message)

                    // Start a coroutine to perform the GPT interaction
                    scope.launch(Dispatchers.Default) {
                        // Execute GPT interaction in the background
                        //print(escapedMessage)
                        val response = gptInteraction.executePrompt(escapedMessage)

                        // Convert Markdown to HTML in the background
                        val markdownResponse = gptInteraction.getChatLogHtml()
                        val parser = Parser.builder().build()
                        val document = parser.parse(markdownResponse)
                        val htmlRenderer = HtmlRenderer.builder().build()
                        val htmlResponse = htmlRenderer.render(document)


                        SwingUtilities.invokeLater {
                            responseArea.text = "$cssStyle$htmlResponse"
                            sendButton.isEnabled = true
                        }

                    }
                }
            }
        })

        val textFieldConstraints = GridBagConstraints()
        textFieldConstraints.fill = GridBagConstraints.HORIZONTAL
        textFieldConstraints.weightx = 1.0
        textFieldConstraints.insets = JBUI.insets(5)

        val comboBoxConstraints = GridBagConstraints()
        comboBoxConstraints.fill = GridBagConstraints.NONE
        comboBoxConstraints.insets = JBUI.insets(5)

        val buttonConstraints = GridBagConstraints()
        buttonConstraints.fill = GridBagConstraints.NONE
        buttonConstraints.insets = JBUI.insets(5)

        inputAndSubmitPanel = JPanel(GridBagLayout())
        inputAndSubmitPanel.add(textField, textFieldConstraints)
        inputAndSubmitPanel.add(phraseComboBox, comboBoxConstraints)
        inputAndSubmitPanel.add(sendButton, buttonConstraints)

        inputAndSubmitPanel.preferredSize = Dimension(600, 60)

        val panel = JPanel()
        panel.layout = BorderLayout()

        panel.add(responseArea, BorderLayout.CENTER)
        panel.add(inputAndSubmitPanel, BorderLayout.SOUTH)

        val scrollPane = JBScrollPane(panel)
        val viewport: JViewport = scrollPane.viewport
        viewport.scrollMode = JViewport.SIMPLE_SCROLL_MODE
        scrollPane.horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        viewport.extentSize = Dimension(0, 0)

        uI = scrollPane
    }


    fun buildComponents(): JBScrollPane {

        //val editor: Editor = DataManager.getInstance().getDataContext().getData(DataConstants.EDITOR) as Editor
        //val editor2: Editor = DataManager.getInstance().dataContextFromFocusAsync.getData(DataConstants.EDITOR) as Editor
        //DataManager.getInstance().saveInDataContext(DataManager.getInstance().getDataContext(), Key.create("ObjectGPTUI"), this)
        //editor.putUserData(Key.create("ObjectGPTUI"), this)

        return uI
    }

    fun escapeKotlinSpecialCharacters(input: String): String {
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

            // Start a coroutine to perform the GPT interaction
            scope.launch(Dispatchers.Default) {
                // Execute GPT interaction in the background
                //print(escapedMessage)
                val response = gptInteraction.executePrompt(escapedMessage)

                // Convert Markdown to HTML in the background
                val markdownResponse = gptInteraction.getChatLogHtml()
                val parser = Parser.builder().build()
                val document = parser.parse(markdownResponse)
                val htmlRenderer = HtmlRenderer.builder().build()
                val htmlResponse = htmlRenderer.render(document)

                SwingUtilities.invokeLater {
                    responseArea.text = "$cssStyle$htmlResponse"
                    sendButton.isEnabled = true
                }

            }
        }
    }

    fun updatePhrases() {
        val settingsState = SettingsState.getInstance()
        phrases = ArrayList(settingsState.sentenceList)

        phrases.add("") //Option that doesn't add anything to the prompt

        println("fuck this shit if this doesnt work i swear i will break my fucking computer")
        println(phrases)

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

        fun getInstance() : UIGpt {

            if(instance1 == null) {
                instance1 = UIGpt()
            }
            return instance1!!
        }
    }
}
