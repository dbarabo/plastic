package ru.barabo.plastic.report.gui

import ru.barabo.report.gui.DirectoryTree
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JSplitPane

class TabReport : JPanel()  {

    init {
        layout = BorderLayout()

        val paramPanel = JPanel()

        val resultPanel = JPanel()

        val paramResultVertSplit = JSplitPane(JSplitPane.VERTICAL_SPLIT, paramPanel, resultPanel).apply {
            resizeWeight = 0.4
        }

        val historyPanel = JPanel()

        val mainHorizontalSplit = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paramResultVertSplit, historyPanel).apply {
            resizeWeight = 0.7
        }

        add(ToolBarReport(), BorderLayout.NORTH)

        add(DirectoryTree(paramPanel), BorderLayout.WEST)

        add(mainHorizontalSplit, BorderLayout.CENTER)
    }

    companion object {
        const val TITLE = "Отчеты"
    }
}
