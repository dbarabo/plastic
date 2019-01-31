package ru.barabo.plastic.fio.gui

import ru.barabo.plastic.fio.entity.FioChangeInfo
import ru.barabo.plastic.unnamed.data.RowFieldClient
import ru.barabo.plastic.unnamed.gui.client.ClientSelect
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JTable

class ToolBarFioChange : AbstractTopToolBar(JTable()) {

    private val buttons = arrayOf(
        ButtonKarkas("user", "Выбрать клиента", { selectClient() },	null),
        ButtonKarkas("toSent", "Отправить в ПЦ", { sendToPC() },	null),

        ButtonKarkas(null, null, null, null)
    )

    init {
        initButton()
    }

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    private fun selectClient() {
        ClientSelect.selectClient(this, ::processResultFromClient)
    }

    private fun processResultFromClient(rowFieldClient: RowFieldClient?) {

        FioChangeInfo.selectClientId = rowFieldClient?.id?.toLong()
    }

    private fun sendToPC() {
        TODO()
    }
}