package ru.barabo.plastic.unnamed.gui

import ru.barabo.gui.swing.HideTabbedPane
import ru.barabo.plastic.release.main.data.DBStorePlastic
import java.awt.BorderLayout
import javax.swing.JPanel

class PanelUnnamed : JPanel() {

    init {
        layout = BorderLayout()

        val book = HideTabbedPane()

        val dbStorePlastic = DBStorePlastic.getInstance()

        book.addTab( TabInPath(dbStorePlastic.unnamedInPath) )

        book.addTab( TabInHome(dbStorePlastic.unnamedInHome) )

        book.addTab( TabOutClient(dbStorePlastic.unnamedOutClient) )

        book.addTab( TabError(dbStorePlastic.unnamedError) )

        val leftButton = LeftTabToolBar(book, dbStorePlastic)

        add(leftButton, BorderLayout.WEST)

        add(book, BorderLayout.CENTER)
    }

    companion object {
        const val TITLE = "Неименные карты"
    }
}