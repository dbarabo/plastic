package ru.barabo.plastic.schema.gui

import ru.barabo.plastic.schema.gui.schema.TabSchema
import ru.barabo.plastic.schema.gui.account.TabAccount
import ru.barabo.plastic.schema.gui.variable.TabVariable
import javax.swing.JTabbedPane
import javax.swing.SwingConstants

class MainSchemaTab : JTabbedPane() {
    init {

        setTabPlacement(SwingConstants.BOTTOM)

        addTab("Схемы", TabSchema())

        addTab("Счета", TabAccount())

        addTab("Функции", TabVariable())
    }


    companion object {
        const val TITLE = "Схемы"
    }
}