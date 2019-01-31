package ru.barabo.plastic.fio.gui

import java.awt.BorderLayout
import javax.swing.JPanel

class FioChangeTab : JPanel() {

    init {
        layout = BorderLayout()

        add(ToolBarFioChange(), BorderLayout.NORTH)

        add(DetailFioChange(), BorderLayout.CENTER)
    }
}