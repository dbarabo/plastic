package ru.barabo.plastic.schema.gui.schema

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.SwingConstants.CENTER

class TabSchema : JPanel()  {
    init {
        layout = BorderLayout()

        val leftSplitBar = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(TableHeaderTransactType),
            JScrollPane(TableTransType)).apply { isOneTouchExpandable = true }

        val mainSplitBar = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitBar, JScrollPane(TableSchema) )

        add(mainSplitBar, CENTER)
    }
}