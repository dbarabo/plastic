package ru.barabo.plastic.unnamed.gui

import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.plastic.unnamed.data.RowFieldInPath
import ru.barabo.plastic.unnamed.general.FilteredStoreInPath
import ru.barabo.plastic.unnamed.general.StoreInTotal
import ru.barabo.plastic.unnamed.gui.dialog.OrderDialog
import ru.barabo.plastic.unnamed.gui.dialog.ResultOrder
import ru.barabo.plastic.unnamed.gui.dialog.runPleaseWait
import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.ListenerStore
import ru.barabo.total.db.StateRefresh
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import java.awt.Component
import java.awt.Container
import javax.swing.JOptionPane
import javax.swing.JOptionPane.ERROR_MESSAGE
import javax.swing.JTabbedPane
import javax.swing.JTable

class TopToolBarInPath(private val store: FilteredStoreInPath<RowFieldInPath>, focusTable: JTable)
    : AbstractTopToolBar(focusTable),
    ListenerStore<RowFieldInPath> {

    private val defaultStateButton = ButtonKarkas("wait", "Ждём", {}, null)

    private val stateButtons = mapOf(
        StatePlasticPacket.OCI_ALL.ordinal to ButtonKarkas("home", "Карты в ГО", {}, null),
        StatePlasticPacket.CARD_GO.ordinal to ButtonKarkas("toDopiki", "В доп. офис->", {}, null),
        StatePlasticPacket.CARD_SENT_OFFICCES.ordinal to ButtonKarkas("toGet", "Получить в офисе", {}, null),
        StatePlasticPacket.CARD_HOME_OFFICCES.ordinal to ButtonKarkas("outClient", "Выдать карту", {}, null)
    )

    private var stateButton: ButtonKarkas? =  null

    private val buttons = arrayOf(
        ButtonKarkas("order", "Заказать карты", { orderCards() },	null),
        ButtonKarkas("toApplication", "Заявление", { gotoApplication(store) }, null),
        ButtonKarkas(null, null, null, null))

    init {

        store.addListenerStore(this)

        initButton()

        setCursor(store.row)
    }

    override fun setCursor(row: RowFieldInPath?) {
        refreshButtons(row?.getFieldByLabel("Статус"))
    }

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    override fun refreshData(allData: MutableList<RowFieldInPath>?, stateRefresh: StateRefresh?) {
        setCursor(store.row)
    }

    private fun refreshButtons(stateField: FieldItem?) {

        val oldButton = stateButton?.button

        val newStateButton = stateField?.let { state -> (state.getVal() as? Number)
            ?.let { stateButtons[it.toInt()] }  } ?: defaultStateButton

        val newButton = newStateButton.button ?: createButton(newStateButton)

        if(oldButton == newButton) {
            repaint()
            return
        }

        stateButton = newStateButton

        oldButton?.let {
            isEnabled = false
            remove(it)
        }

        add(newButton)
        invalidate()
        repaint()
    }

    private fun orderCards() {

        val resultOrder = OrderDialog.showResultDialog()

        when {
            resultOrder.countCard == 0 ->  return

            resultOrder.countCard > MAX_CARD_COUNT -> {
                JOptionPane.showMessageDialog(null, ERROR_MAX_MESSAGE, ERROR_MAX_TITLE, ERROR_MESSAGE )
                return
            }
            else -> createUnnamedCards(resultOrder)
        }
    }

    private fun createUnnamedCards(resultOrder: ResultOrder) {

        val pleaseWait = runPleaseWait()

        processCreateCards(resultOrder)

        pleaseWait.dispose()
    }

    private fun processCreateCards(resultOrder: ResultOrder) {
        try {
            store.createUnnamedCards(resultOrder.countCard, resultOrder.productCardId)
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(null, e.message, ERROR_ORDER_TITLE, ERROR_MESSAGE )
        }
    }

    companion object {
        private const val MAX_CARD_COUNT = 100

        private const val ERROR_MAX_MESSAGE = "Куда Вы столько заказываете!\nУ нас не Сбербанк!"

        private const val ERROR_MAX_TITLE = "Превышено максимальное кол-во карт"

        private const val ERROR_ORDER_TITLE = "Ошибка при заказе(отправке) карт"
    }
}

fun errorMessage(message: String?): Boolean {
    JOptionPane.showMessageDialog(null, message, null, JOptionPane.ERROR_MESSAGE)

    return false
}

fun Component.mainBook(): JTabbedPane? {

    var loopParent: Container? = parent

    var topApplicationBook: JTabbedPane? = null

    while (loopParent != null) {

        if(loopParent is JTabbedPane) {
            topApplicationBook = loopParent
        }
        loopParent = loopParent.parent
    }

    return topApplicationBook
}

fun Component.gotoApplication(store: StoreInTotal): Boolean {

    try {
        store.selectApplicationStore()
    } catch (e: Exception) {
        return errorMessage(e.message)
    }

    val mainBook = mainBook() ?: return false
    mainBook.selectedIndex = (mainBook.tabCount  - 1)

    return true
}