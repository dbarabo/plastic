package ru.barabo.plastic.card.gui

import ru.barabo.db.service.StoreService
import ru.barabo.plastic.card.service.*
import ru.barabo.plastic.fio.gui.FioChangeTab
import ru.barabo.plastic.schema.gui.MainSchemaTab
import ru.barabo.plastic.terminal.gui.TabPosTerminal
import ru.barabo.plastic.unnamed.gui.PanelUnnamed
import ru.barabo.plastic.unnamed.gui.TopToolBarInPath.Companion.addNewTabIfAbsent
import ru.barabo.plastic.unnamed.gui.errorMessage
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

        toolButton("home", "в Офисе", 0, buttonGroupList) {
            InHomeStoreCardService.updateWithErrorMessage()
            book.selectedIndex = 0
        }

        registerCardButton = toolButton("application", "на Оформлении", 0, buttonGroupList) {
            RegisterCardService.updateWithErrorMessage()
            book.selectedIndex = 1
        }

        toolButton("parovoz", "в Пути", 0, buttonGroupList) {
            InPathStoreCardService.updateWithErrorMessage()
            book.selectedIndex = 2
        }

        outClientButton = toolButton("ok", "Выданные", 0, buttonGroupList) {
            OutClientStoreCardService.updateWithErrorMessage()
            book.selectedIndex = 3
        }

        closeCardButton = toolButton("closed", "Закрытые", 0, buttonGroupList) {
            CloseStoreCardService.updateWithErrorMessage()
            book.selectedIndex = 6
        }

        toolButton("unclaimed", "Невостребованные", 0, buttonGroupList) {
            UnclaimedStoreCardService.updateWithErrorMessage()
            book.selectedIndex = 4
        }

        toolButton("bug", "с Ошибками", 0, buttonGroupList) {
            ErrorStoreCardService.updateWithErrorMessage()
            book.selectedIndex = 5
        }

        addSeparatorTimeCount(5)

        toolButton("unnamed", "Неименные карты") {
            addNewTabIfAbsent(this, PanelUnnamed.TITLE, PanelUnnamed::class.java)
        }

        toolButton("user", "Смена перс. данных") {
            addNewTabIfAbsent(this, FioChangeTab.TITLE, FioChangeTab::class.java)
        }

        addSeparatorTimeCount(6)

        toolButton("pos", TabPosTerminal.TITLE) {
            addNewTabIfAbsent(this, TabPosTerminal.TITLE, TabPosTerminal::class.java)
        }

        toolButton("schema", "Схемы транзакций") {
            addNewTabIfAbsent(this, MainSchemaTab.TITLE, MainSchemaTab::class.java)
        }

        val max = components.maxBy { it.maximumSize.width } ?: this
        components.forEach {
            it.maximumSize = max.maximumSize
            it.minimumSize = max.maximumSize
            it.preferredSize = max.maximumSize
        }

        (components[0] as? JToggleButton)?.isSelected = true
        InHomeStoreCardService.initData()
    }

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