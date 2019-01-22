package ru.barabo.plastic.schema.gui.selector

import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.service.selector.SelectAccountService
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JScrollPane
import javax.swing.SwingUtilities

object SelectAccountTab : SelectorTab<SelectAccount>("Выбор счета") {

    init {
        layout = BorderLayout()

        SelectAccountTable.addMouseListener(
            object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {

                    if(e?.clickCount == 2 && SwingUtilities.isLeftMouseButton(e) ) {
                        tabsSaver.select(SelectAccountService.selectedEntity())
                    }
                }
            })

        add(JScrollPane(SelectAccountTable), BorderLayout.CENTER)

        val filterSelectAccount = FilterSelectAccount(::tabsSaver, SelectAccountService)

        add(filterSelectAccount, BorderLayout.NORTH)
    }
}