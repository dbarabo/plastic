package ru.barabo.plastic.report.gui

import ru.barabo.report.gui.DirectoryTree
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.SwingConstants

class TabReport : JPanel()  {

    init {
        layout = BorderLayout()

        val paramPanel = JPanel()

        val titlelReport = JLabel("", SwingConstants.CENTER).apply {
            setOpaque(true)

            this.foreground = Color.WHITE
            this.background = Color.BLACK
        }

        val titlePanelParam = JPanel().apply {
            layout = BorderLayout()

            add(titlelReport, BorderLayout.NORTH)

            add(paramPanel, BorderLayout.CENTER)
        }

        val resultPanel = JPanel()

        val paramResultVertSplit = JSplitPane(JSplitPane.VERTICAL_SPLIT, titlePanelParam, resultPanel).apply {
            resizeWeight = 0.4
        }

        val historyPanel = JPanel()

        val mainHorizontalSplit = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paramResultVertSplit, historyPanel).apply {
            resizeWeight = 0.7
        }

        add(ToolBarReport(), BorderLayout.NORTH)

        add(DirectoryTree(paramPanel, titlelReport), BorderLayout.WEST)

        add(mainHorizontalSplit, BorderLayout.CENTER)
    }

    companion object {
        const val TITLE = "Отчеты"
    }
}
