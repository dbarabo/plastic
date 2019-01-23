package ru.barabo.plastic.unnamed.gui.client

import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.data.RowFieldClient
import ru.barabo.plastic.unnamed.general.ClientCriteriaDBStore
import ru.barabo.plastic.unnamed.gui.errorMessage
import ru.barabo.plastic.unnamed.gui.mainBook
import ru.barabo.total.gui.table.TotalRowTable
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.SwingUtilities

object ClientSelect : JPanel() {

    private val store: ClientCriteriaDBStore<RowFieldClient> = DBStorePlastic.getInstance().clientSelect

    private val detailClientFilter = DetailClientFilter(store)

    init {
        layout = BorderLayout()

        add(detailClientFilter, BorderLayout.PAGE_START)

        val tableClient = TotalRowTable<RowFieldClient>(store)

        tableClient.addMouseListener(
            object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {

                    if(e?.clickCount == 2 && SwingUtilities.isLeftMouseButton(e) ) {
                        detailClientFilter.okSelect()
                    }
                }
            })

        add(JScrollPane(tableClient), BorderLayout.CENTER)
    }

    fun selectClient(component: Component, processResultOk: (RowFieldClient?)->Unit) {

        detailClientFilter.processResultOk = processResultOk

        detailClientFilter.tabsInBook = component.selectClient(TITLE, ClientSelect)

        detailClientFilter.fioTextFocused()
    }

    @JvmStatic
    fun selectClient(component: Component) {
        val mainBook = component.mainBook() ?: return errorMessage(ERROR_MAIN_BOOK_NOT_FOUND).run { TabsInBook() }

        mainBook.addTab(TITLE, ClientSelect)
    }
}

fun Component.selectClient(title: String, tabPanel: Container): TabsInBook {
    val mainBook = mainBook() ?: return errorMessage(ERROR_MAIN_BOOK_NOT_FOUND).run { TabsInBook() }

    val result = mainBook.saveTabs()

    mainBook.addTab(title, tabPanel)

    return result
}

private const val TITLE = "Выбрать клиента"

private const val ERROR_MAIN_BOOK_NOT_FOUND = "Не найдена главная форма"

//private val logger = Logger.getLogger(ClientSelect::class.java.name)

fun JTabbedPane.saveTabs(): TabsInBook {

    val panels = ArrayList<Pair<String, Component>>()

    for (index in 0 until tabCount) {

        panels += Pair(getTitleAt(index), getComponentAt(index))
    }

    val selectIndex = if(selectedIndex < 0)0 else selectedIndex

    removeAll()

    return TabsInBook(panels, selectIndex, this)
}

data class TabsInBook(val panels: List<Pair<String, Component>> = emptyList(), val selectedPanel: Int = 0, val book: JTabbedPane = JTabbedPane()) {

    fun restoreTabs() {

        book.removeAll()

        for (pairs in panels) book.addTab(pairs.first, pairs.second)

        book.selectedIndex = selectedPanel
    }
}
