package ru.barabo.plastic.unnamed.gui

import ru.barabo.plastic.unnamed.data.RowFieldInPath
import ru.barabo.total.db.FilteredStore
import ru.barabo.total.gui.filter.impl.FilterTableSimple
import ru.barabo.total.gui.table.TotalRowTable
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JPanel
import javax.swing.JScrollPane

class TabError (store: FilteredStore<RowFieldInPath>) : JPanel()  {

    init {
        layout = BorderLayout()

        val tableFocus = TotalRowTable<RowFieldInPath>(store)

        val panelWithFilter = JPanel(BorderLayout(), true)

        val filter = FilterTableSimple(store, tableFocus)

        panelWithFilter.add(filter, BorderLayout.PAGE_START)

        panelWithFilter.add(JScrollPane(tableFocus), BorderLayout.CENTER)

        add(panelWithFilter, BorderLayout.CENTER)
    }
}