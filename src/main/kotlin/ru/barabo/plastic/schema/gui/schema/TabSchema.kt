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

            isOneTouchExpandable = true
        }

        val topSplitBar = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, topLeftSplitBar, JScrollPane(TableConditionVariant)).apply {
            isOneTouchExpandable = true
        }

        val mainSplitBar = JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitBar, JScrollPane(TableSchema) ).apply {
            isOneTouchExpandable = true
        }

        add(ToolBarSchema(TableSchema), BorderLayout.NORTH)

        add(mainSplitBar, BorderLayout.CENTER)

        with(topLeftSplitBar) {
            resizeWeight = 0.2
            setDividerLocation(0.2)
        }

        with(topSplitBar) {
            resizeWeight = 0.9
            setDividerLocation(0.9)
        }

        with(mainSplitBar) {
            resizeWeight = 0.8
            setDividerLocation(0.8)
        }
    }
}