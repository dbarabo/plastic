package ru.barabo.plastic.schema.gui.account

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

class TabAccount() : JPanel()  {

    init {
        layout = BorderLayout()

        val tableAccount = TableAccount()

        val toolBarAccount = ToolBarAccount(tableAccount)

        add(toolBarAccount, BorderLayout.NORTH)

        add(JScrollPane(tableAccount), BorderLayout.CENTER)
    }
}