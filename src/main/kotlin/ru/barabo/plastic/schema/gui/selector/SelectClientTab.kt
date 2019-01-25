package ru.barabo.plastic.schema.gui.selector

import ru.barabo.plastic.schema.entity.selector.SelectClient
import ru.barabo.plastic.schema.service.selector.SelectClientService
import java.awt.BorderLayout
import javax.swing.JScrollPane

object SelectClientTab : SelectorTab<SelectClient>("Выбор клиента/банка") {

    init {
        layout = BorderLayout()

        SelectClientTable.doubleClickEvent {
            tabsSaver.select(SelectClientService.selectedEntity())
        }

        add(JScrollPane(SelectClientTable), BorderLayout.CENTER)

        val filterSelectClient = FilterSelectClient(::tabsSaver, SelectClientService)

        add(filterSelectClient, BorderLayout.NORTH)
    }
}