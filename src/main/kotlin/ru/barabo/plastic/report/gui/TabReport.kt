package ru.barabo.plastic.report.gui

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.report.entity.HistoryRun
import ru.barabo.report.gui.DirectoryTree
import ru.barabo.report.gui.MessageInformer
import ru.barabo.report.service.HistoryRunService
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*

class TabReport : JPanel()  {

    init {
        layout = BorderLayout()

        val paramPanel = JPanel()

        val titlelReport = JLabel("", SwingConstants.CENTER).apply {
            isOpaque = true

            this.foreground = Color.WHITE
            this.background = Color.BLACK
        }

        val titlePanelParam = JPanel().apply {
            layout = BorderLayout()

            add(titlelReport, BorderLayout.NORTH)

            add(paramPanel, BorderLayout.CENTER)
        }

        val messageInformer = MessageInformer()

        val paramResultVertSplit = JSplitPane(JSplitPane.VERTICAL_SPLIT, titlePanelParam, messageInformer).apply {
            resizeWeight = 0.4
        }

        val historyPanel = JPanel().apply {
            layout = BorderLayout()
            add(JScrollPane( TableHistory(this) ), BorderLayout.CENTER)
        }

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

class TableHistory(parent: JPanel) : EntityTable<HistoryRun>(historyRunColumns, HistoryRunService) {
    init {
        rowHeight = 60

        background = parent.background
    }
}

private val historyRunColumns = listOf(
    ColumnTableModel("История", 200, HistoryRun::info, false)
)
