package ru.barabo.plastic.unnamed.gui

import ru.barabo.plastic.unnamed.data.RowFieldOutClient
import ru.barabo.plastic.unnamed.general.FilteredStoreOutClient
import ru.barabo.total.gui.filter.impl.FilterTableSimple
import ru.barabo.total.gui.table.TotalRowTable
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JPanel
import javax.swing.JScrollPane

class TabOutClient(store: FilteredStoreOutClient<RowFieldOutClient>) : JPanel()  {

    init {
        layout = BorderLayout()

        val tableFocus = TotalRowTable<RowFieldOutClient>(store)

        val panelWithFilter = JPanel(BorderLayout(), true)

        val filter = FilterTableSimple(store, tableFocus).apply {
            this.background = Color.BLUE
        }

        panelWithFilter.add(filter, BorderLayout.PAGE_START)

        panelWithFilter.add(JScrollPane(tableFocus), BorderLayout.CENTER)

        add(TopToolBarOutClient(store, tableFocus), BorderLayout.NORTH)

        add(panelWithFilter, BorderLayout.CENTER)
    }
}