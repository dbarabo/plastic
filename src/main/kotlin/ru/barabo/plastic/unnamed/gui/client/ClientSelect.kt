package ru.barabo.plastic.unnamed.gui.client

import ru.barabo.plastic.unnamed.data.RowFieldClient
import ru.barabo.plastic.unnamed.general.ClientCriteriaDBStore
import ru.barabo.total.gui.table.TotalRowTable
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

class ClientSelect(store: ClientCriteriaDBStore<RowFieldClient>) : JPanel() {
    init {
        layout = BorderLayout()

        val panelFilter = DetailClientFilter(store)

        add(panelFilter, BorderLayout.PAGE_START)

        val tableClient = TotalRowTable<RowFieldClient>(store)

        add(JScrollPane(tableClient), BorderLayout.CENTER)
    }
}