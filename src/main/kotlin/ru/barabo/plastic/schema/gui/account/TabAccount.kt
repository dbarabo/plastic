package ru.barabo.plastic.schema.gui.account

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane

class TabAccount : JPanel()  {

    init {
        layout = BorderLayout()


        val toolBarAccount = ToolBarAccount(TableAccount)

        val detailAccountValue = DetailAccountValue()

        val selectorTypeAccount = SelectorTypeAccount()

        val mainSplitBar = JSplitPane(JSplitPane.VERTICAL_SPLIT, JScrollPane(TableAccount), selectorTypeAccount)
            .apply { isOneTouchExpandable = true }

        add(toolBarAccount, BorderLayout.NORTH)

        add(mainSplitBar, BorderLayout.CENTER)

        add(detailAccountValue, BorderLayout.EAST)
    }
}