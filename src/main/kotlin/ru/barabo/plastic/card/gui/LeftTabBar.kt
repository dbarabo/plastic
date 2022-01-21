package ru.barabo.plastic.card.gui

import ru.barabo.db.service.StoreService
import ru.barabo.plastic.card.service.*
import ru.barabo.plastic.terminal.gui.TabPosTerminal
import ru.barabo.plastic.unnamed.gui.errorMessage
import ru.barabo.report.gui.TabReport
import javax.swing.*

class LeftTabBar (private val book: JTabbedPane) : JToolBar(VERTICAL) {

    private val buttonGroupList = ArrayList<ButtonGroup>()

    private var closeCardButton: AbstractButton? = null
    private var registerCardButton: AbstractButton? = null
    private var outClientButton: AbstractButton? = null

    var closeToolBar: ToolBarCardClosed? = null
    var outClientToolBar: ToolBarCardOutClient? = null

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        isFloatable = true

//        toolButton("home", "в Офисе", 0, buttonGroupList) {
//            InHomeStoreCardService.updateWithErrorMessage()
//            book.selectedIndex = 0
//        }
//
//        registerCardButton = toolButton("application", "на Оформлении", 0, buttonGroupList) {
//            RegisterCardService.updateWithErrorMessage()
//            book.selectedIndex = 1
//        }
//
//        registerCardButton?.isEnabled = isDelbOnlyAccess()
//
//        val inPathButton = toolButton("parovoz", "в Пути", 0, buttonGroupList) {
//            InPathStoreCardService.updateWithErrorMessage()
//            book.selectedIndex = 2
//        }
//        inPathButton?.isEnabled = isDelbOnlyAccess()
//
//
//        outClientButton = toolButton("ok", "Выданные", 0, buttonGroupList) {
//            OutClientStoreCardService.updateWithErrorMessage()
//            book.selectedIndex = 3
//        }
//
//        closeCardButton = toolButton("closed", "Закрытые", 0, buttonGroupList) {
//            CloseStoreCardService.updateWithErrorMessage()
//            book.selectedIndex = 6
//        }
//        closeCardButton?.isEnabled = isDelbOnlyAccess()
//
//        val unclaimedButton = toolButton("unclaimed", "Невостребованные", 0, buttonGroupList) {
//            UnclaimedStoreCardService.updateWithErrorMessage()
//            book.selectedIndex = 4
//        }
//        unclaimedButton?.isEnabled = isDelbOnlyAccess()
//
//        val errorButton = toolButton("bug", "с Ошибками", 0, buttonGroupList) {
//            ErrorStoreCardService.updateWithErrorMessage()
//            book.selectedIndex = 5
//        }
//        errorButton?.isEnabled = isDelbOnlyAccess()
//
//        addSeparatorTimeCount(5)
//
//        toolButton("unnamed", "Неименные карты") {
//            addNewTabIfAbsent(this, PanelUnnamed.TITLE, PanelUnnamed::class.java)
//        }
//
//        toolButton("user", "Смена перс. данных") {
//            addNewTabIfAbsent(this, FioChangeTab.TITLE, FioChangeTab::class.java)
//        }
//
//        addSeparatorTimeCount(6)

        val posTerminalButton = toolButton("pos", TabPosTerminal.TITLE) {
            //addNewTabIfAbsent(this, TabPosTerminal.TITLE, TabPosTerminal::class.java)
            book.selectedIndex = 0
        }
        posTerminalButton?.isEnabled = isDelbOnlyAccess()

        val schemaButton = toolButton("schema", "Схемы транзакций") {
            //addNewTabIfAbsent(this, MainSchemaTab.TITLE, MainSchemaTab::class.java)
            book.selectedIndex = 1
        }
        schemaButton?.isEnabled = isDelbOnlyAccess()

        addSeparatorTimeCount(5)

        val reportButton = toolButton("importXLS", TabReport.TITLE) {
            //addNewTabIfAbsent(this, TabReport.TITLE, TabReport::class.java)
            book.selectedIndex = 2
        }

        val max = components.maxByOrNull { it.maximumSize.width } ?: this
        components.forEach {
            it.maximumSize = max.maximumSize
            it.minimumSize = max.maximumSize
            it.preferredSize = max.maximumSize
        }

        (components[0] as? JToggleButton)?.isSelected = true

        //InHomeStoreCardService.initData()
    }

    private fun isDelbOnlyAccess(): Boolean = true
        //AfinaQuery.getUserDepartment().accessMode in listOf(AccessMode.FullAccess, AccessMode.DelbAccess)

    private fun addSeparatorTimeCount(count: Int) {
        for(index in 1..count) {
            addSeparator()
        }
    }

    fun selectRegister() {
        registerCardButton?.isSelected = true
        registerCardButton?.doClick()
    }

    fun selectCloseButton() {
        closeToolBar?.addSelectedButtons()

        closeCardButton?.isSelected = true
        closeCardButton?.doClick()
    }

    fun selectOutClientButton() {
        outClientToolBar?.addSelectedButtons()

        outClientButton?.isSelected = true
        outClientButton?.doClick()
    }

}

fun StoreService<*,*>.updateWithErrorMessage() {
    try {
        initData()
    } catch (e: Exception) {
        errorMessage(e.message)
    }
}