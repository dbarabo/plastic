package ru.barabo.plastic.terminal.gui

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

class TabPosTerminal : JPanel()  {

    init {
        layout = BorderLayout()

        val toolBarTerminal = ToolBarPosTerminal()

        add(toolBarTerminal, BorderLayout.NORTH)

        add(JScrollPane(TablePosTerminal), BorderLayout.CENTER)
    }

    companion object {
        const val TITLE = "POS-Терминалы"
    }
}
