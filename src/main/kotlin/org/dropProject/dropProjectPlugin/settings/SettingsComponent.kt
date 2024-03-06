package org.dropProject.dropProjectPlugin.settings

import com.intellij.ui.JBColor
import com.intellij.ui.components.*
import com.intellij.util.ui.FormBuilder
import org.dropProject.dropProjectPlugin.submissionComponents.UIGpt
import org.jetbrains.annotations.NotNull
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class SettingsComponent {
    private val mainPanel: JPanel
    private val serverURL = JBTextField()
    private var nameField = JBTextField()

    private val numberField = JBTextField()
    private val tokenField = JBPasswordField()
    private val showToken = JBCheckBox("Show")

    private val openAiTokenField = JBPasswordField()
    private val showOpenAiToken = JBCheckBox("Show")
    private val openAiTokenPanel = JPanel(BorderLayout())
    private val autoSendPrompt = JBCheckBox("<html>Send prompt automatically.<br>If checked, GPT will be prompted as soon as the \"Ask GPT\" button is clicked.<br>Otherwise, the plugin will wait for further input to be added before sending the request.</html>") // New checkbox
    private val tokenpanel = JPanel(BorderLayout())

    private val addEditRemovePanel = JPanel()
    private val sentenceListModel: DefaultListModel<String> = DefaultListModel()
    private var sentenceList: JBList<String> = JBList(sentenceListModel)
    private val sentenceTextField: JBTextField = JBTextField()
    private val addButton = JButton("Add")
    private val editButton = JButton("Edit")
    private val removeButton = JButton("Remove")

    init {
        // TOKEN FIELD AND SHOW CHECKBOX COMBINED
        tokenpanel.add(tokenField, BorderLayout.CENTER)
        tokenpanel.add(showToken, BorderLayout.EAST)

        openAiTokenPanel.add(openAiTokenField, BorderLayout.CENTER)
        openAiTokenPanel.add(showOpenAiToken, BorderLayout.EAST)

        sentenceTextField.emptyText.text = "Text to be added at the end of the prompt"

        // BUILD SETTINGS FORM
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Server URL: "), serverURL, 1, false)
            .addLabeledComponent(JBLabel("Name: "), nameField, 1, false)
            .addLabeledComponent(JBLabel("Number: "), numberField, 1, false)
            .addLabeledComponent(JBLabel("Token: "), tokenpanel, 1, false)
            .addLabeledComponent(JBLabel("OpenAI API Key: "), openAiTokenPanel, 1, false)
            .addLabeledComponent(JBLabel("Ask ChatGPT: "), autoSendPrompt, 1, false)
            .addLabeledComponent(JBLabel("ChatGPT - Messages to add to prompts"), sentenceTextField, 1, true)
            .addComponent(createAddEditRemovePanel())
            .addComponentFillVertically(JPanel(), 0)
            .panel


        showToken.addActionListener {
            val checkbox = it.source as JCheckBox
            tokenField.echoChar = if (checkbox.isSelected) 0.toChar() else '\u2022'
        }

        showOpenAiToken.addActionListener {
            val checkbox = it.source as JCheckBox
            openAiTokenField.echoChar = if (checkbox.isSelected) 0.toChar() else '\u2022'
        }

        addButton.addActionListener {
            addSentence()
        }

        editButton.addActionListener {
            editSentence()
        }

        removeButton.addActionListener {
            removeSentence()
        }
    }

    private fun createAddEditRemovePanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)  // Use BoxLayout with top-to-bottom alignment

        val scrollPane = JBScrollPane(sentenceList)
        scrollPane.preferredSize = Dimension(0, 100)

        val emptyBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3) // Adjust the margins as needed
        val lineBorder = BorderFactory.createLineBorder(JBColor(0xB5B5B5, 0x5C5C5C)) // You can adjust the color
        val compoundBorder = BorderFactory.createCompoundBorder(emptyBorder, lineBorder)

        scrollPane.border = compoundBorder

        // Create a sub-panel for buttons with BoxLayout (default takes full width)
        val buttonPanel = JPanel(GridLayout(1, 0))

        buttonPanel.add(addButton)
        buttonPanel.add(editButton)
        buttonPanel.add(removeButton)

        panel.add(scrollPane)
        panel.add(sentenceTextField)
        panel.add(buttonPanel)

        return panel
    }

    private fun addSentence() {
        val sentence = sentenceTextField.text
        if (sentence.isNotEmpty()) {
            sentenceListModel.addElement(sentence)
            sentenceTextField.text = ""
            val uiGpt = UIGpt.getInstance()
            uiGpt.addPhrase(sentence)
        }
    }

    private fun editSentence() {
        val selectedIndex = sentenceList.selectedIndex
        if (selectedIndex != -1) {
            val editedSentence = JOptionPane.showInputDialog(
                mainPanel,
                "Edit Sentence:",
                sentenceListModel.getElementAt(selectedIndex)
            )
            if (editedSentence != null) {
                sentenceListModel.setElementAt(editedSentence, selectedIndex)
                val uiGpt = UIGpt.getInstance()
                uiGpt.editPhrase(selectedIndex + 1, editedSentence)
            }
        }
    }

    private fun removeSentence() {
        val selectedIndex = sentenceList.selectedIndex
        if (selectedIndex != -1) {
            sentenceListModel.remove(selectedIndex)
        }

        val uiGpt = UIGpt.getInstance()
        uiGpt.removePhrase(selectedIndex + 1)
    }

    fun getPanel(): JPanel {
        return mainPanel
    }

    fun getServerURL(): String {
        return serverURL.text
    }

    fun setServerURL(@NotNull text: String) {
        serverURL.text = text
    }

    fun getNameField(): String {
        return nameField.text
    }

    fun setNameField(@NotNull text: String) {
        nameField.text = text
    }

    fun getNumberField(): String {
        return numberField.text
    }

    fun setNumberField(@NotNull text: String) {
        numberField.text = text
    }

    fun getTokenField(): String {
        return String(tokenField.password)
    }

    fun setTokenField(token: String) {
        tokenField.text = token
    }

    fun isAutoSendPromptSelected(): Boolean {
        return autoSendPrompt.isSelected
    }

    fun setAutoSendPrompt(autoSend: Boolean) {
        autoSendPrompt.isSelected = autoSend
    }

    fun getPreferredFocusedComponent(): JComponent {
        return nameField
    }

    fun getOpenAiTokenField(): String {
        return String(openAiTokenField.password)
    }

    fun setOpenAiTokenField(token: String) {
        openAiTokenField.text = token
    }

    fun getSentenceList(): List<String> {
        return sentenceListModel.elements().toList()
    }

    fun setSentenceList(sentences: List<String>) {
        sentenceListModel.clear()
        sentences.forEach { sentenceListModel.addElement(it) }
    }

}
