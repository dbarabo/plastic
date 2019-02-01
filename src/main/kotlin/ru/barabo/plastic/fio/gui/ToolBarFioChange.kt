package ru.barabo.plastic.fio.gui

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.fio.entity.FioChangeInfo
import ru.barabo.plastic.fio.service.FioChangeService
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.unnamed.data.RowFieldClient
import ru.barabo.plastic.unnamed.gui.client.ClientSelect
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JTable

class ToolBarFioChange : AbstractTopToolBar(JTable()), StoreListener<List<FioChangeInfo>> {

    private val buttons = arrayOf(
        ButtonKarkas("user", "Выбрать клиента", { selectClient() },	null),
        ButtonKarkas("toSent", "Отправить в ПЦ", { sendToPC() },	null),

        ButtonKarkas(null, null, null, null)
    )

    init {
        initButton()

        FioChangeService.addListener(this)
    }

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    override fun refreshAll(elemRoot: List<FioChangeInfo>, refreshType: EditType) {
        if(refreshType in listOf(EditType.INIT, EditType.CHANGE_CURSOR, EditType.ALL)) {
            val client = FioChangeService.selectedEntity()

            buttons[1].button.isEnabled = client?.idInProcCenter?.isEmpty() == false
        }
    }

    private fun selectClient() {
        ClientSelect.selectClient(this, ::processResultFromClient)
    }

    private fun processResultFromClient(rowFieldClient: RowFieldClient?) {

        FioChangeInfo.selectClientId = rowFieldClient?.id?.toLong()
    }

    private fun sendToPC() {

        processShowError {
            FioChangeService.sendBtrt30()

            buttons[1].button.isEnabled = false
        }
    }
}