package ru.barabo.plastic.packet.gui

import org.apache.log4j.Logger
import ru.barabo.plastic.afina.rtf.RtfCashIn
import ru.barabo.plastic.gui.PlasticGui
import ru.barabo.plastic.main.resources.owner.Cfg
import ru.barabo.plastic.release.ivr.xml.IvrXml
import ru.barabo.plastic.release.packet.data.*
import ru.barabo.plastic.release.packet.gui.ListenerContent
import ru.barabo.plastic.release.packet.gui.ToolBarRefresh
import ru.barabo.plastic.release.reissue.gui.TopToolBarReIssueCard
import ru.barabo.plastic.unnamed.gui.errorMessage
import ru.barabo.plastic.unnamed.gui.tryCatchDefaultStore
import ru.barabo.total.db.DBStore
import ru.barabo.total.db.ListenerStore
import ru.barabo.total.db.StateRefresh
import ru.barabo.total.db.impl.AbstractRowFields
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import ru.barabo.total.gui.any.ShowMenuListener
import ru.barabo.total.gui.table.TotalRowTable
import ru.barabo.total.report.rtf.RtfReport
import java.awt.Component
import java.awt.Container
import java.awt.event.ActionEvent
import java.text.DecimalFormat
import java.util.*
import java.util.function.Consumer
import javax.swing.*
import javax.swing.JOptionPane.QUESTION_MESSAGE

open class ToolBarPacket <E : AbstractRowFields?> (private val store: DBStore<E>, focusComp: JComponent, private val contentComp: JComponent? = null)
    : AbstractTopToolBar(focusComp), ListenerStore<E>, ToolBarRefresh {

    companion object {
        private val logger = Logger.getLogger(ToolBarPacket::class.java.simpleName)

        private const val MSG_WAIT = "Сейчас нужно просто ждать, когда придут ответные файлы от ПЦ"

        private const val MSG_GO_HOME = "Перевести карты в Головной офис?"
        private const val TITLE_GO_HOME = "Карты в 'ГО'"

        private const val MSG_TO_GET = "Перевести карты в состояние 'Получено в доп. офисах'?"
        private const val TITLE_TO_GET = "Карты в Доп. офисах"

        private const val MSG_ERROR_NO_CONTENT = "Не Выбрано ни одного клиента в таблице клиентов"
        private const val MSG_OUT_CLIENT_ONLY = "Перевести карту %s в конечное состояние 'Выдано клиенту'?"
        private const val TITLE_OUT_CLIENT_ONLY = "Карту клиенту"

        private const val REMOVE_ALL_PACKET = "Удалить весь пакет '%s' ?"
        private const val REMOVE_TITLE_PACKET = "Удаление пакета"

        private const val REMOVE_RECORD_PACKET = "Удалить запись пакета по '%s' ?"
        private const val REMOVE_TITLE_RECORD_PACKET = "Удаление записи в пакете"

        private const val MSG_GET_NEW_NAME = "Новое имя пакета"


        private const val SEND_IN_DOPIC = "Отправить в "
    }

    private var stateButton: AbstractButton? = null
    private var state: StatePlasticPacket? = null
    private var stateMenuItemContent: JMenuItem? = null

    private var stateButtonContentIndex: Int = 0

    private var isMySelect: Boolean = false
    private var filterWork: FilterWork? = null

    private val menuFilterProblemType =
        arrayOf(ButtonKarkas("allFilter", "Отбор:Все", { selectAll() }, 0),
            ButtonKarkas("newFilter", "Отбор:'Новые'", { this.selectNew() }, 0),
            ButtonKarkas("workFilter", "Отбор:'В Работе'", { this.selectWork() }, 0),
            ButtonKarkas("endFilter", "Отбор:'Закрытые'", { this.selectEndWork() }, 0),
            ButtonKarkas("bug", "Отбор:'С Ошибками'", { this.selectError() }, 0)
        )

    private val menuFilterByUser =
        arrayOf(ButtonKarkas("allUsers", "Отбор:Любые", { this.selectAllUser() }, 1),
            ButtonKarkas("user", "Отбор:Мои", { this.selectMy() }, 1)
        )

    private val buttonKarkases = arrayOf(ButtonKarkas("refresh", "Обновить", { this.refresh() }, null),
        ButtonKarkas("deleteAll", "Удалить", ::showMenuDelete, null),
        ButtonKarkas("rename", "Правка", { this.renamePacket() }, null),
        ButtonKarkas("toApplication", "Заявление", { this.gotoApplication() }, null),
        ButtonKarkas("changeProduct", "Сменить продукт", { this.changeProduct() }, null),
        ButtonKarkas("death", "BTRT25 Send", { this.sendBtr25() }, null),
        ButtonKarkas(null, null, null, null),

        ShowMenuListener(menuFilterProblemType).createButtonKarkas(0),
        ShowMenuListener(menuFilterByUser).createButtonKarkas(1),
        ButtonKarkas(null, null, null, null)
    )

    private var deleteRecordPopup: JPopupMenu? = null

    private val mapStateButtons = mapOf(
        StatePlasticPacket.NEW to ButtonKarkas("mc", "Перевыпустить") { this.reIssueCards() },
        StatePlasticPacket.OUT to ButtonKarkas("newFile", "Отправить файл") { this.createFile() },
        StatePlasticPacket.SENT to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SENT_OK to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SENT_ERROR to ButtonKarkas("returnNew", "Вернуть на отправку") { this.toCreateFileState() },
        StatePlasticPacket.RESPONSE_OK_ALL to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.UNCLAIMED to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.RESPONSE_ERROR_ALL to ButtonKarkas("returnNew", "Вернуть на отправку") { this.toCreateFileState() },
        StatePlasticPacket.RESPONSE_ERROR_PART to ButtonKarkas("returnNew", "Вернуть на отправку") { this.toCreateFileState() },
        StatePlasticPacket.OCI_ALL to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.OCI_PART to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_SENT to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_SENT_ACCESS to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_SENT_ERROR to ButtonKarkas("sms", "Вернуть в SMS") { this.toSmsState() },
        StatePlasticPacket.SMS_RESPONSE_OK_ALL_OIA to ButtonKarkas("home", "Карты в ГО") { this.goHome() },
        StatePlasticPacket.SMS_RESPONSE_OK_PART_OIA  to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_RESPONSE_ERROR_ALL_OIA to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_RESPONSE_ERROR_PART_OIA  to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.CARD_GO to ButtonKarkas("toDopiki", "В доп. офис->", this::toDopiki),
        StatePlasticPacket.CARD_SENT_OFFICCES to ButtonKarkas("toGet", "Получить в офисе") { this.toGet() },
        StatePlasticPacket.CARD_HOME_OFFICCES to ButtonKarkas("prepare", "Подготовить к выдаче") { this.prepareCardOut() },
        StatePlasticPacket.PREPARE_CARD_TO_OUT to ButtonKarkas("outClient", "Выдать карту") { this.outClientOnly() },
        StatePlasticPacket.CARD_TO_CLIENT to ButtonKarkas(null, null, null, null)
    )

    private val stateButtonsContent = mapOf(
        StatePlasticPacket.PREPARE_CARD_TO_OUT to ButtonKarkas("outClient", "Выдать карту") { this.outClientOnly() }
    )

    init {
        initButton()

        state = null
        stateButton = null

        stateMenuItemContent = null
        stateButtonContentIndex = -1

        isMySelect = false
        filterWork = FilterWork.ALL

        val packet = store as DBStorePacket

        store.addListenerStore(this)

        if(contentComp != null) {
            packet.dbStorePacketContentPacket.addListenerStore( ListenerContent(this) )
        }

        val data = store.data as List<E>

        refreshData(data, StateRefresh.ALL)
    }

    override fun initButton() {
        super.initButton()

        val btrt25Button = getButtonKarkases()[5].button ?: return

        btrt25Button.isVisible = (store as DBStorePacket).isSuperWorkspace
    }

    private fun selectAll() {

        filterWork = FilterWork.ALL

        (focusComp as TotalRowTable<*>).setMustFullRefresh()
        store.setViewType(filterWork!!.getSelectTypes(isMySelect).ordinal)

        focusComp.requestFocus()
    }

    private fun selectNew() {

        filterWork = FilterWork.NEW

        (focusComp as TotalRowTable<*>).setMustFullRefresh()
        store.setViewType(filterWork!!.getSelectTypes(isMySelect).ordinal)
        focusComp.requestFocus()
    }

    private fun selectWork() {

        filterWork = FilterWork.WORK

        (focusComp as TotalRowTable<*>).setMustFullRefresh()
        store.setViewType(filterWork!!.getSelectTypes(isMySelect).ordinal)
        focusComp.requestFocus()
    }

    private fun selectEndWork() {

        filterWork = FilterWork.END

        (focusComp as TotalRowTable<*>).setMustFullRefresh()
        store.setViewType(filterWork!!.getSelectTypes(isMySelect).ordinal)
        focusComp.requestFocus()
    }

    private fun selectError() {

        filterWork = FilterWork.ERROR

        (focusComp as TotalRowTable<*>).setMustFullRefresh()
        store.setViewType(filterWork!!.getSelectTypes(isMySelect).ordinal)
        focusComp.requestFocus()
    }

    private fun selectMy() {

        isMySelect = true
        (focusComp as TotalRowTable<*>).setMustFullRefresh()
        store.setViewType(filterWork!!.getSelectTypes(isMySelect).ordinal)
        focusComp.requestFocus()
    }

    private fun selectAllUser() {

        isMySelect = false
        (focusComp as TotalRowTable<*>).setMustFullRefresh()
        store.setViewType(filterWork!!.getSelectTypes(isMySelect).ordinal)
        focusComp.requestFocus()
    }

    private fun renamePacket() {
        val newName = JOptionPane.showInputDialog(MSG_GET_NEW_NAME) ?: return

        val dBStorePacket = store as DBStorePacket

        val error = dBStorePacket.renamePacket(newName)
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
        }

        focusComp.requestFocus()
    }

    /**
     * перевыпуск всех карт
     */
    private fun reIssueCards() {

        val dBStorePacket = store as DBStorePacket
        val result = dBStorePacket.reIssueCards()

        if (result != null) {
            TopToolBarReIssueCard.messageError(result)
            focusComp.requestFocus()
            return
        }

        focusComp.requestFocus()

        val emptyDesignCards = ArrayList<String>()

        val error = dBStorePacket.getEmptyDesignCard(emptyDesignCards)
        if (error != null) {
            TopToolBarReIssueCard.messageError(result)
            focusComp.requestFocus()
            return
        }

        if (emptyDesignCards.size > 0) {
            val cards = emptyDesignCards.joinToString("\n")

            JOptionPane.showMessageDialog(
                null, Cfg.msg().msgDesignCardIsEmpty(cards),
                Cfg.msg().subjDesignCardIsEmpty(), JOptionPane.INFORMATION_MESSAGE
            )
        }

        focusComp.requestFocus()
    }

    /**
     * создание файла
     */
    private fun createFile() {
        val dBStorePacket = store as DBStorePacket
        val result = dBStorePacket.createFile()
        if (result != null) {
            TopToolBarReIssueCard.messageError(result)
        }

        focusComp.requestFocus()
    }

    private fun toGet() {
        val dBStorePacket = store as DBStorePacket

        UIManager.put("OptionPane.yesButtonText", "Да")
        UIManager.put("OptionPane.noButtonText", "Нет")
        val reply = JOptionPane.showConfirmDialog(
            null,
            MSG_TO_GET,
            TITLE_TO_GET, JOptionPane.YES_NO_OPTION
        )

        if (reply != JOptionPane.YES_OPTION) {
            focusComp.requestFocus()
            return
        }

        focusComp.requestFocus()

        val error = dBStorePacket.toGetHomes()
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
        }

        focusComp.requestFocus()
    }

    private fun prepareCardOut() {
        val dBStorePacket = store as DBStorePacket
        val contentDb = dBStorePacket.dbStorePacketContentPacket

        when (contentDb.row?.classCard) {
            ClassCard.CreditCard -> prepareCreditCardOut(contentDb)

            ClassCard.PlatinaCard -> preparePlatinaCardOut(contentDb)

            else -> errorMessage("Не выбрана строка содержимого пакета!")
        }
    }

    private fun prepareCreditCardOut(contentDb: DBStorePacketContent) {
        val limit = dialogGetLimit() ?: return

        tryCatchDefaultStore(contentDb) {
            contentDb.prepareCreditCardOut(limit)
        }
    }

    private fun preparePlatinaCardOut(contentDb: DBStorePacketContent) {
        tryCatchDefaultStore(contentDb) {

            val platinaCashIn = contentDb.beforePreparePlatinaCardOut()
            if(platinaCashIn.label == null) return@tryCatchDefaultStore

            CashInDialog(this, platinaCashIn, ::endOkPreparePlatina).isVisible = true
        }
    }

    private fun endOkPreparePlatina(platinaCashIn: PlatinaCashIn) {

        val contentDb = (store as DBStorePacket).dbStorePacketContentPacket

        tryCatchDefaultStore(contentDb) {
            val idCashIn = contentDb.endPreparePlatinaCardOut(platinaCashIn)

            logger.error("idCashIn=$idCashIn")

            RtfReport.build(RtfCashIn(idCashIn))
        }
    }

    private fun outClientOnly() {

        val dBStorePacket = store as DBStorePacket

        val contentDb = dBStorePacket.dbStorePacketContentPacket

        val contentRow = contentDb.row

        if (contentRow == null || contentRow.id == null) {
            TopToolBarReIssueCard.messageError(MSG_ERROR_NO_CONTENT)
            return
        }

        UIManager.put("OptionPane.yesButtonText", "Да")
        UIManager.put("OptionPane.noButtonText", "Нет")
        val reply = JOptionPane.showConfirmDialog(
            null,
            String.format(MSG_OUT_CLIENT_ONLY, contentRow.name),
            TITLE_OUT_CLIENT_ONLY, JOptionPane.YES_NO_OPTION
        )

        if (reply != JOptionPane.YES_OPTION) return

        val error = contentDb.outClientOnly()
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
            return
        }


        if (!dBStorePacket.isTestBaseConnect) {
            val ivrInfo = contentDb.ivrInfo

            if (ivrInfo != null) {
                IvrXml.startIvrProccessByDb(ivrInfo)
            }
        }

        focusComp.requestFocus()
    }

    private fun toDopik(e: ActionEvent?) {
        if (e == null || e.source !is AbstractButton) {
            return
        }

        val menuDopik = e.source as AbstractButton

        var dopic = menuDopik.text

        if (dopic.indexOf(SEND_IN_DOPIC) != 0) {
            TopToolBarReIssueCard.messageError("Не выбрано подразделение!")
            return
        }

        dopic = dopic.substring(SEND_IN_DOPIC.length).trim { it <= ' ' }

        val dBStorePacket = store as DBStorePacket

        val error = dBStorePacket.toDopik(dopic)
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
        }

        dBStorePacket.updateAllData()
        focusComp.requestFocus()
    }

    private fun initPopupToDopiki(): JPopupMenu? {
        val dBStorePacket = store as DBStorePacket

        val dopikiInPacket = dBStorePacket.dopikiInPacket ?: return null

        val popupMenu = JPopupMenu()

        for (dopik in dopikiInPacket) {
            val menuDopik = JMenuItem()
            menuDopik.text = SEND_IN_DOPIC + dopik
            menuDopik.addActionListener { this.toDopik(it) }
            popupMenu.add(menuDopik)
        }

        return popupMenu
    }


    private fun toDopiki(e: ActionEvent) {

        val dopikMenu = initPopupToDopiki()

        val src = e.source as Component

        dopikMenu?.show(src, 1, src.height + 1)
    }

    private fun toSmsState() {
        val dBStorePacket = store as DBStorePacket
        val result = dBStorePacket.toSmsState()
        if (result != null) {
            TopToolBarReIssueCard.messageError(result)
        }
        dBStorePacket.updateAllData()

        focusComp.requestFocus()
    }

    private fun toCreateFileState() {
        val dBStorePacket = store as DBStorePacket
        val result = dBStorePacket.toCreateFileState()
        if (result != null) {
            TopToolBarReIssueCard.messageError(result)
        }
        dBStorePacket.updateAllData()

        focusComp.requestFocus()
    }


    private fun showMenuDelete(e: ActionEvent) {
        if (deleteRecordPopup == null) {
            deleteRecordPopup = initDelete()
        }

        val src = e.source as Component

        deleteRecordPopup!!.show(src, 1, src.height + 1)
    }

    private fun initDelete(): JPopupMenu {
        val popupMenu = JPopupMenu()

        val alldel = JMenuItem()
        alldel.text = "Удалить весь пакет"
        alldel.addActionListener { this.deleteAll() }
        popupMenu.add(alldel)

        val dell = JMenuItem()
        dell.text = "Удалить запись из пакета"
        dell.addActionListener { this.deleteOne() }
        popupMenu.add(dell)

        return popupMenu
    }

    private fun getMainBook(): JTabbedPane? {

        var findBook: Container? = this.parent

        while (findBook != null && findBook !is JTabbedPane) {
            findBook = findBook.parent
        }

        return findBook as JTabbedPane?
    }

    private fun sendBtr25() {
        val dBStorePacket = store as DBStorePacket

        val error = dBStorePacket.sendBrt25()
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
        }
    }

    private fun changeProduct() {
        val dBStorePacket = store as DBStorePacket

        val fromProducts = ArrayList<String>()

        val error = dBStorePacket.getChangeFromProductItems(fromProducts)
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
            return
        }

        if (fromProducts.size != 0) {
            dialogChangeProduct(fromProducts)
        }
    }

    private fun getDialogProductFrom(fromProducts: List<String>): String? {

        val combo = JComboBox<String>()
        fromProducts.forEach(Consumer<String> { combo.addItem(it) })

        val buttons = arrayOf("OK", "Отмена")

        val title = "Выберите изменяемый (старый) продукт"
        val selection = JOptionPane.showOptionDialog(
            null, combo, title,
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
            buttons, buttons[0]
        )

        return if (selection != 0) {
            null
        } else combo.selectedItem as String

    }

    private fun dialogChangeProduct(fromProducts: List<String>) {

        var fromProduct: String? = if (fromProducts.size == 1) fromProducts[0] else null

        if (fromProduct == null) {
            fromProduct = getDialogProductFrom(fromProducts)
        }

        if (fromProduct == null) {
            return
        }

        val dBStorePacket = store as DBStorePacket

        val toProducts = ArrayList<String>()
        var error = dBStorePacket.getChangeToProductItems(fromProduct, toProducts)
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
            return
        }

        if (toProducts.size == 0) {
            TopToolBarReIssueCard.messageError("Невозможно сменить этот продукт")
            return
        }

        var toProduct: String? = if (toProducts.size == 1) toProducts[0] else null

        if (toProduct == null) {
            toProduct = getDialogProductTo(toProducts)
        }

        if (toProduct == null) {
            return
        }

        error = dBStorePacket.changeProduct(fromProduct, toProduct)
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
            return
        }

        val contentDb = dBStorePacket
            .dbStorePacketContentPacket

        contentDb.setCursor(dBStorePacket.row)
    }

    private fun gotoApplication() {
        val contentRow = (store as DBStorePacket).dbStorePacketContentPacket.row

        if (contentRow == null || contentRow.id == null) {
            TopToolBarReIssueCard.messageError(MSG_ERROR_NO_CONTENT)
            return
        }

        PlasticGui.openApplicationForm(contentRow.applicationId)
        /*
        val applicationDB = dBStorePacket.dbStoreApplicationCard
        applicationDB.setViewType(contentRow.applicationId)

        val mainBook = getMainBook() ?: return
        mainBook.selectedIndex = mainBook.tabCount - 1
         */
    }

    private fun deleteOne() {
        val dBStorePacket = store as DBStorePacket

        val contentDb = dBStorePacket.dbStorePacketContentPacket

        val contentRow = contentDb.row

        if (contentRow == null || contentRow.id == null) {
            TopToolBarReIssueCard.messageError(MSG_ERROR_NO_CONTENT)
            return
        }

        UIManager.put("OptionPane.yesButtonText", "Да")
        UIManager.put("OptionPane.noButtonText", "Нет")
        val reply = JOptionPane.showConfirmDialog(
            null,
            String.format(REMOVE_RECORD_PACKET, contentRow.name),
            REMOVE_TITLE_RECORD_PACKET, JOptionPane.YES_NO_OPTION
        )

        if (reply != JOptionPane.YES_OPTION) return

        contentDb.removeRow()

        focusComp.requestFocus()
    }

    private fun deleteAll() {
        val dBStorePacket = store as DBStorePacket

        UIManager.put("OptionPane.yesButtonText", "Да")
        UIManager.put("OptionPane.noButtonText", "Нет")
        val reply = JOptionPane.showConfirmDialog(
            null,
            String.format(REMOVE_ALL_PACKET, store.row?.name),
            REMOVE_TITLE_PACKET, JOptionPane.YES_NO_OPTION
        )

        if (reply != JOptionPane.YES_OPTION) return

        val error = dBStorePacket.removePacket()
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
        }

        focusComp.requestFocus()
    }

    protected fun waitPc() {

        JOptionPane.showMessageDialog(
            null,
            MSG_WAIT, null, JOptionPane.INFORMATION_MESSAGE
        )

        focusComp.requestFocus()
    }

    private fun goHome() {

        val dBStorePacket = store as DBStorePacket

        UIManager.put("OptionPane.yesButtonText", "Да")
        UIManager.put("OptionPane.noButtonText", "Нет")
        val reply = JOptionPane.showConfirmDialog(
            null,
            MSG_GO_HOME,
            TITLE_GO_HOME, JOptionPane.YES_NO_OPTION
        )

        if (reply != JOptionPane.YES_OPTION) return

        val error = dBStorePacket.goHomeState()
        if (error != null) {
            TopToolBarReIssueCard.messageError(error)
        }

        focusComp.requestFocus()
    }


    private fun refresh() {
        val dBStorePacket = store as DBStorePacket
        dBStorePacket.updateAllData()
        dBStorePacket.sendListenersRefreshAllData(StateRefresh.ALL)

        focusComp.requestFocus()
    }

    override fun getButtonKarkases(): Array<ButtonKarkas> {

        return buttonKarkases
    }

    /////////////ListenerStore<PacketRowField>//////////////
    override fun setCursor(row: E) {

        refreshButtons(row as? PacketRowField)
    }

    /**
     * кнопка - зависит от состояния
     */
    private fun refreshStateButton(row: PacketRowField?) {

        val realState = getRealStateButton(row)

        refreshStateButtonByRealState(realState)
    }

    private fun getRealStateButton(row: PacketRowField?): StatePlasticPacket? {
        val statePlasticPacket = row?.state?.let { StatePlasticPacket.getStateByDbValue(it) } ?: return null

        if(statePlasticPacket != StatePlasticPacket.CARD_HOME_OFFICCES) {
            return statePlasticPacket
        }

        val content = (store as? DBStorePacket)?.dbStorePacketContentPacket?.row ?: return null

        val contentState = StatePlasticPacket.getStateByDbValue(content.state) ?: return null

        return if(contentState != StatePlasticPacket.CARD_HOME_OFFICCES || content.classCard != ClassCard.Any) contentState
        else StatePlasticPacket.PREPARE_CARD_TO_OUT
    }

    private fun getRealStateButtonContent(row: PacketContentRowField?): StatePlasticPacket? {
        val stateContent = row?.state?.let { StatePlasticPacket.getStateByDbValue(it) } ?: return null

        return if(stateContent != StatePlasticPacket.CARD_HOME_OFFICCES || row.classCard != ClassCard.Any) stateContent
        else StatePlasticPacket.PREPARE_CARD_TO_OUT
    }

    override fun refreshStateContentButton(row: PacketContentRowField?) {

        val realState = getRealStateButtonContent(row)

        checkPopupMenu(realState)
        refreshStateButtonByRealState(realState)
    }

    private fun checkPopupMenu(realState: StatePlasticPacket?) {

        if(realState == state) return

        val popup = contentComp?.componentPopupMenu ?: return

        if(stateMenuItemContent != null && realState != state) {
            popup.remove(stateMenuItemContent)
            stateMenuItemContent = null
        }

        val karkas = realState?.let { getStateButtonContent(it) } ?: return

        stateMenuItemContent = createMenuItem(karkas).apply { popup.add(this) }
    }

    private fun getStateButtonContent(realState: StatePlasticPacket): ButtonKarkas? = stateButtonsContent[realState]

    private fun createMenuItem(button: ButtonKarkas): JMenuItem {

        val menu = JMenuItem(button.name)

        menu.text = button.name

        menu.addActionListener(button.listener)

        return menu
    }

    private fun refreshStateButtonByRealState(realState: StatePlasticPacket?) {
        val isRemove = isRemoveStateButton(realState)

        if (realState == null || state == realState) return

        state = realState

        stateButton = getStateButton(state)?.let { createButton(it)?.apply { this@ToolBarPacket.add(this) } }

        if(isRemove || stateButton != null) {
            repaint()
        }
    }

    protected open fun getStateButton(state: StatePlasticPacket?): ButtonKarkas? = state?.let { mapStateButtons[it] }

    private fun isRemoveStateButton(realState: StatePlasticPacket?): Boolean {
        return if (stateButton != null && realState != state) {
            stateButton?.isEnabled = false
            this.remove(stateButton)
            state = null
            stateButton = null

            true
        } else {
            false
        }
    }

    private fun refreshButtons(row: PacketRowField?) {
        refreshStateButton(row)
        refreshDeleteButtons(row)
    }

    private fun refreshDeleteButtons(row: PacketRowField?) {

        val deleteAllButton = getButtonKarkases()[1].button ?: return

        deleteAllButton.isEnabled = !(row == null || row.state != 0 && row.state != 1)
    }

    override fun refreshData(allData: List<E>, stateRefresh: StateRefresh) {

        val row = if (allData.isEmpty()) null else allData[0]

        refreshButtons(row as? PacketRowField)
    }
}

internal enum class FilterWork {
    ALL,
    NEW,
    WORK,
    END,
    ERROR;

    fun getSelectTypes(isMy: Boolean): SelectTypes {

        return SelectTypes.values()[this.ordinal * 2 + (if (isMy) 1 else 0)]
    }
}

fun dialogGetLimit(): Number? = parseLimit(
    JOptionPane.showInputDialog(
        null, "Заполните одобренный макс. лимит кредитной карты",
        "Лимит кредита", QUESTION_MESSAGE
    )
)

private fun parseLimit(limit: String?): Number? {
    val limitCheck = limit?.trim()?.replace(",", ".")?.replace(" ", "") ?: return null

    if(limitCheck.isEmpty()) return null

    return DecimalFormat("#######.##").parse(limitCheck)
}

fun getDialogProductTo(toProducts: List<String>): String? {

    val combo = JComboBox<String>()
    toProducts.forEach(Consumer<String> { combo.addItem(it) })

    val buttons = arrayOf("OK", "Отмена")

        val title = "Выберите Новый продукт"
    val selection = JOptionPane.showOptionDialog(
        null, combo, title,
        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
        buttons, buttons[0]
    )

    return if (selection != 0) {
        null
    } else combo.selectedItem as String
}