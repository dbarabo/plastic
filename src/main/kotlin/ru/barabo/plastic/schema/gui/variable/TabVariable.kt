package ru.barabo.plastic.schema.gui.variable

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

class TabVariable : JPanel() {

    init {
        layout = BorderLayout()


        add(ToolBarVariable(TableVariable), BorderLayout.NORTH)

        add(JScrollPane(TableVariable), BorderLayout.CENTER)
    }
}