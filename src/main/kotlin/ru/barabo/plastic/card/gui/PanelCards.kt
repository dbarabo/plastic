package ru.barabo.plastic.card.gui

import ru.barabo.gui.swing.HideTabbedPane
import ru.barabo.plastic.schema.gui.MainSchemaTab
import ru.barabo.plastic.terminal.gui.TabPosTerminal
import ru.barabo.plastic.unnamed.gui.TopToolBarInPath
import ru.barabo.report.gui.ReportOnly
import ru.barabo.report.gui.TabReport
import java.awt.BorderLayout
import javax.swing.JPanel

class PanelCards : JPanel() {

    init {
        layout = BorderLayout()

        val book = HideTabbedPane()

        val leftButtonBar = LeftTabBar(book)

//        book.addTab( TabCard(TableInHomeCard, ToolBarCardInPath(InHomeStoreCardService, TableInHomeCard)) )
//
//        book.addTab(TabCard(TableRegisterCard, ToolBarCardRegister(RegisterCardService, TableRegisterCard, leftButtonBar)) )
//
//        book.addTab( TabCard(TableInPathCard, ToolBarCardInPath(InPathStoreCardService, TableInPathCard)) )
//
//        val outClientToolBar = ToolBarCardOutClient(OutClientStoreCardService, TableOutClientCard, leftButtonBar)
//        leftButtonBar.outClientToolBar = outClientToolBar
//        book.addTab( TabCard(TableOutClientCard, outClientToolBar) )
//
//        book.addTab( TabCard(TableUnclaimedCard, ToolBarCardClosed(UnclaimedStoreCardService, TableUnclaimedCard, leftButtonBar)) )
//
//        book.addTab( TabCard(TableErrorCard, ToolBarCardInPath(ErrorStoreCardService, TableErrorCard)) )
//
//        val closeCardToolBar = ToolBarCardClosed(CloseStoreCardService, TableCloseCard, leftButtonBar)
//        leftButtonBar.closeToolBar = closeCardToolBar
//        book.addTab( TabCard(TableCloseCard, closeCardToolBar))


        book.addTab( TabPosTerminal() )

        book.addTab( MainSchemaTab() )

        book.addTab( TabReport( ReportChecker() ) )

        // TopToolBarInPath.addNewTabIfAbsent(this, TabPosTerminal.TITLE, TabPosTerminal::class.java)

        add(leftButtonBar, BorderLayout.WEST)

        add(book, BorderLayout.CENTER)
    }

    companion object {
        const val TITLE = "Пластик"//"Карты Дебетовые/Кредитные"
    }
}

class ReportChecker() : ReportOnly {

    override var isOnlyReport: Boolean
        get() = true
        set(value) {}
}