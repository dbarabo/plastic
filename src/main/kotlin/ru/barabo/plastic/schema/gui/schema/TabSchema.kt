package ru.barabo.plastic.schema.gui.schema

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane


class TabSchema : JPanel()  {
    init {
        layout = BorderLayout()

        val topLeftSplitBar = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(TableHeaderTransactType),
            JScrollPane(TableTransType)).apply {
            setDividerLocation(0.3)
            isOneTouchExpandable = true
        }

        val topSplitBar = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, topLeftSplitBar, JScrollPane(TableConditionVariant)).apply {
            isOneTouchExpandable = true
            setDividerLocation(0.85)
        }

        val mainSplitBar = JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitBar, JScrollPane(TableSchema) ).apply {
            setDividerLocation(0.75)
        }

        add(ToolBarSchema(TableSchema), BorderLayout.NORTH)

        add(mainSplitBar, BorderLayout.CENTER)
    }
}