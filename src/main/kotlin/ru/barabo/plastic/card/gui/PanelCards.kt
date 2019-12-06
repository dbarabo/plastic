package ru.barabo.plastic.card.gui

import ru.barabo.gui.swing.HideTabbedPane
import ru.barabo.plastic.card.service.*
import java.awt.BorderLayout
import javax.swing.JPanel

class PanelCards : JPanel() {

    init {
        layout = BorderLayout()

        val book = HideTabbedPane()

        val leftButtonBar = LeftTabBar(book)

        book.addTab( TabCard(TableInHomeCard, ToolBarCardInPath(InHomeStoreCardService, TableInHomeCard)) )

        book.addTab(TabCard(TableRegisterCard, ToolBarCardRegister(RegisterCardService, TableRegisterCard, leftButtonBar)) )

        book.addTab( TabCard(TableInPathCard, ToolBarCardInPath(InPathStoreCardService, TableInPathCard)) )

        val outClientToolBar = ToolBarCardOutClient(OutClientStoreCardService, TableOutClientCard, leftButtonBar)
        leftButtonBar.outClientToolBar = outClientToolBar
        book.addTab( TabCard(TableOutClientCard, outClientToolBar) )

        book.addTab( TabCard(TableUnclaimedCard, ToolBarCardClosed(UnclaimedStoreCardService, TableUnclaimedCard, leftButtonBar)) )

        book.addTab( TabCard(TableErrorCard, ToolBarCardInPath(ErrorStoreCardService, TableErrorCard)) )

        val closeCardToolBar = ToolBarCardClosed(CloseStoreCardService, TableCloseCard, leftButtonBar)
        leftButtonBar.closeToolBar = closeCardToolBar
        book.addTab( TabCard(TableCloseCard, closeCardToolBar))

        add(leftButtonBar, BorderLayout.WEST)

        add(book, BorderLayout.CENTER)
    }

    companion object {
        const val TITLE = "Карты Дебетовые/Кредитные"
    }
}