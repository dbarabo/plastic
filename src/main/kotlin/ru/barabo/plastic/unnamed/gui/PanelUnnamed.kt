package ru.barabo.plastic.unnamed.gui

import ru.barabo.gui.swing.HideTabbedPane
import ru.barabo.plastic.release.main.data.DBStorePlastic
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Panel
import javax.swing.JPanel

class PanelUnnamed(dbStorePlastic: DBStorePlastic) : JPanel() {

    init {
        layout = BorderLayout()

        val book = HideTabbedPane()

        book.addTab( TabInPath(dbStorePlastic.unnamedInPath) )

        book.addTab( TabInHome(dbStorePlastic.unnamedInHome) )

        book.addTab( TabOutClient(dbStorePlastic.unnamedOutClient) )

        book.addTab( TabError(dbStorePlastic.unnamedError) )

        val leftButton = LeftTabToolBar(book)

        add(leftButton, BorderLayout.WEST)

        add(book, BorderLayout.CENTER)
    }
}