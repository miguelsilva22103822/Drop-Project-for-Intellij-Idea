package org.dropProject.dropProjectPlugin.settings

import com.intellij.openapi.options.Configurable
import org.dropProject.dropProjectPlugin.submissionComponents.UIGpt
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class SettingsConfigurable : Configurable {
    private var mySettingsComponent: SettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "SDK: Application Settings Example"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return mySettingsComponent?.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent {
        mySettingsComponent = SettingsComponent()
        return mySettingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val settings: SettingsState = SettingsState.getInstance()
        return (!mySettingsComponent?.getServerURL().equals(settings.serverURL)) or
                (!mySettingsComponent?.getNameField().equals(settings.username)) or
                (!mySettingsComponent?.getNumberField().equals(settings.usernumber)) or
                (!mySettingsComponent?.getTokenField().contentEquals(settings.token)) or
                (!mySettingsComponent?.getOpenAiTokenField().contentEquals(settings.openAiToken)) or
                (mySettingsComponent?.isAutoSendPromptSelected() != settings.autoSendPrompt) or
                (mySettingsComponent?.getSentenceList() != settings.sentenceList)
    }

    override fun apply() {
        val settings: SettingsState = SettingsState.getInstance()
        settings.serverURL = mySettingsComponent?.getServerURL()!!
        settings.username = mySettingsComponent?.getNameField()!!
        settings.usernumber = mySettingsComponent?.getNumberField()!!
        settings.token = mySettingsComponent?.getTokenField()!!
        settings.openAiToken = mySettingsComponent?.getOpenAiTokenField()!!
        settings.autoSendPrompt = mySettingsComponent?.isAutoSendPromptSelected() ?: false
        settings.sentenceList = (mySettingsComponent?.getSentenceList() as MutableList<String>?)!!
        UIGpt.instance1?.updatePhrases()
    }

    override fun reset() {
        val settings: SettingsState = SettingsState.getInstance()
        settings.serverURL.let { mySettingsComponent?.setServerURL(it) }
        settings.username.let { mySettingsComponent?.setNameField(it) }
        settings.usernumber.let { mySettingsComponent?.setNumberField(it) }
        settings.token.let { mySettingsComponent?.setTokenField(it) }
        settings.openAiToken.let { mySettingsComponent?.setOpenAiTokenField(it) }
        settings.autoSendPrompt.let { mySettingsComponent?.setAutoSendPrompt(it) }
        settings.sentenceList.let { mySettingsComponent?.setSentenceList(it) }
        settings.sentenceList.let {
            if (it.isEmpty()|| it.size == 1) {
                it.addAll(listOf("Find the bug", "Improve the performance", "Explain this code", "Write tests for this function"))
            }
            mySettingsComponent?.setSentenceList(it)
        }
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
