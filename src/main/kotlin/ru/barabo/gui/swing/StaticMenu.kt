package ru.barabo.gui.swing

import ru.barabo.total.gui.any.ButtonKarkas
import java.awt.Component
import java.awt.event.ActionListener
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class StaticMenu(ico: String?, name: String?, items: Array<ButtonKarkas>) : ButtonKarkas(ico, name) {

    private val popupMenu = createPopup(items)

    init {
       listener = createPopupListener()
    }

    private fun createPopupListener(): ActionListener  = ActionListener {

        val src = it.source as Component

        popupMenu.show(src, 1, src.height + 1)
    }

    private fun createPopup(items: Array<ButtonKarkas>): JPopupMenu = JPopupMenu().apply {
        for (karkas in items) {
            karkas.name?.let {karkasName ->
                add( JMenuItem(karkasName, karkas.imageIco).apply { addActionListener(karkas.listener) } )
            } ?: addSeparator()
        }
    }
}



