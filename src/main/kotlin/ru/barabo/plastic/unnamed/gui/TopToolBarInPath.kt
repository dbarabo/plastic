package ru.barabo.plastic.unnamed.gui

import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.plastic.unnamed.data.RowFieldInPath
import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.FilteredStore
import ru.barabo.total.db.ListenerStore
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JTable

class TopToolBarInPath(store: FilteredStore<RowFieldInPath>, focusTable: JTable)
    : AbstractTopToolBar<RowFieldInPath>(store, focusTable),
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

    override fun refreshData(allData: MutableList<RowFieldInPath>?) {
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

    }
}