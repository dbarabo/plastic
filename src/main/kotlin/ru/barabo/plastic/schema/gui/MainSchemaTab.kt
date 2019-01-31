package ru.barabo.plastic.schema.gui

import ru.barabo.plastic.schema.gui.schema.TabSchema
import ru.barabo.plastic.schema.gui.account.TabAccount
import javax.swing.JTabbedPane
import javax.swing.SwingConstants

class MainSchemaTab : JTabbedPane() {
    init {

        setTabPlacement(SwingConstants.BOTTOM)

        addTab("Счета", TabAccount())

        addTab("Схема", TabSchema())
    }


    companion object {
        const val TITLE = "Схемы"
    }
}