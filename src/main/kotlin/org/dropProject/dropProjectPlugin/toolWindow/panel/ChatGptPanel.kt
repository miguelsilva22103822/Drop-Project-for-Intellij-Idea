package org.dropProject.dropProjectPlugin.toolWindow.panel

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.dsl.builder.panel
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JTextArea
import javax.swing.JViewport

class ChatGptPanel : JBScrollPane(panel { row { label("hello test") } }) {
    init {

        val viewport: JViewport = this.viewport
        viewport.scrollMode = JViewport.SIMPLE_SCROLL_MODE
        horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_NEVER
        verticalScrollBarPolicy = VERTICAL_SCROLLBAR_ALWAYS
        viewport.extentSize = Dimension(0, 0)

    }
}