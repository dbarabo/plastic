package ru.barabo.plastic.schema.gui.selector

import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.entity.selector.SelectClient
import ru.barabo.plastic.schema.service.selector.SelectAccountService
import ru.barabo.plastic.schema.service.selector.SelectClientService
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JScrollPane
import javax.swing.SwingUtilities

object SelectClientTab : SelectorTab<SelectClient>("Выбор клиента/банка") {

    init {
        layout = BorderLayout()

        SelectClientTable.doubleClickEvent{
            tabsSaver.select(SelectClientService.selectedEntity())
        }

        add(JScrollPane(SelectClientTable), BorderLayout.CENTER)

        val filterSelectClient = FilterSelectClient(::tabsSaver, SelectClientService)

        SelectAccountTab.add(filterSelectClient, BorderLayout.NORTH)
    }
}