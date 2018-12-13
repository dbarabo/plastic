package ru.barabo.plastic.unnamed.gui

import ru.barabo.plastic.unnamed.data.RowFieldOutClient
import ru.barabo.plastic.unnamed.general.FilteredStoreOutClient
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JTable

class TopToolBarOutClient(private val store: FilteredStoreOutClient<RowFieldOutClient>, focusTable: JTable)
    : AbstractTopToolBar(focusTable) {

    private val buttons = arrayOf(
        ButtonKarkas("toApplication", "Заявление", { gotoApplication(store) }, null),
        ButtonKarkas("password", "сменить ПИН-код!", { changePinCode(store) } , null),
        ButtonKarkas(null, null, null, null))

    init {
        initButton()
    }

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons
}

fun changePinCode(store: FilteredStoreOutClient<*>) {

    try {
        store.changePinCode()
    } catch (e: Exception) {
        errorMessage(e.message)

        store.updateAllData()
    }
}

