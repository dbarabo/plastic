package ru.barabo.plastic.schema.gui

import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.schema.gui.schema.TabSchema
import ru.barabo.plastic.schema.gui.account.TabAccount
import ru.barabo.plastic.schema.gui.variable.TabVariable
import ru.barabo.plastic.terminal.gui.TabPosTerminal
import javax.swing.JTabbedPane
import javax.swing.SwingConstants

class MainSchemaTab : JTabbedPane() {
    init {

        setTabPlacement(SwingConstants.BOTTOM)

        addTab("Схемы", ResourcesManager.getIcon("schema"), TabSchema())

        addTab("Счета", ResourcesManager.getIcon("account"), TabAccount())

        addTab("Функции", ResourcesManager.getIcon("fun"), TabVariable())
    }

    companion object {
        const val TITLE = "Схемы"
    }
}