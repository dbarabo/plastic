package ru.barabo.plastic.unnamed.gui

import ru.barabo.plastic.packet.gui.dialogGetLimit
import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.plastic.unnamed.data.RowFieldInPath
import ru.barabo.plastic.unnamed.general.FilteredStoreInHome
import ru.barabo.plastic.unnamed.general.ResultOutClient
import ru.barabo.plastic.unnamed.gui.dialog.ChangeOfficeCard
import ru.barabo.plastic.unnamed.gui.dialog.OutCardToClient
import ru.barabo.total.db.DBStore
import ru.barabo.total.db.ListenerStore
import ru.barabo.total.db.StateRefresh
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JTable

class TopToolBarInHome(private val store: FilteredStoreInHome<RowFieldInPath>, focusTable: JTable)
    : AbstractTopToolBar(focusTable),
    ListenerStore<RowFieldInPath> {

    private val defaultStateButton = ButtonKarkas("wait", "Ждём", {}, null)

    private val stateButtons = mapOf(
        StatePlasticPacket.OCI_ALL.dbValue to ButtonKarkas("home", "Карты в ГО", {}, null),
        StatePlasticPacket.CARD_GO.dbValue to ButtonKarkas("toDopiki", "В доп. офис->", { cardSendToDopOffice() }, null),
        StatePlasticPacket.CARD_SENT_OFFICCES.dbValue to ButtonKarkas("toGet", "Получить в офисе", { cardGetToDopMainOffice() }, null),
        StatePlasticPacket.CARD_HOME_OFFICCES.dbValue to ButtonKarkas("prepare", "Подготовить к выдаче") { prepareCardOut() },
        StatePlasticPacket.PREPARE_CARD_TO_OUT.dbValue to ButtonKarkas("outClient", "Выдать карту", { outCardToClient() }, null)
    )

    private var stateButton: ButtonKarkas? =  null

    private val buttons = arrayOf(
            ButtonKarkas("toApplication", "Заявление", { gotoApplication(store) }, null),
            ButtonKarkas("inversia", "Передать м/у офисами", { moveToDepartment() }, null),

            ButtonKarkas(null, null, null, null))

    init {

        store.addListenerStore(this)

        initButton()

        setCursor(store.row)
    }

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    override fun setCursor(row: RowFieldInPath?) {
        refreshButtons(row?.getState(), row?.getClassCard()?:0)
    }

    override fun refreshData(allData: MutableList<RowFieldInPath>?, stateRefresh: StateRefresh?) {
        setCursor(store.row)
    }

    private fun prepareCardOut() {
        OutCardToClient(this, store.row).showResultDialog(::processPrepareCardOut)
    }

    private fun moveToDepartment() {
        tryCatchDefaultStore(store) {
            ChangeOfficeCard(this, store).showDialogResultOk()
        }
    }

    private fun processPrepareCardOut(resultOutClient: ResultOutClient) {

        val limit = dialogGetLimit() ?: return

        tryCatchDefaultStore(store) {
            store.prepareOutCard(resultOutClient, limit)
        }
    }

    private fun refreshButtons(state: Int?, classCard: Int) {

        val oldButton = stateButton?.button

        val realState = getRealStateDbValue(state, classCard)

        val newStateButton = realState?.let { stateButtons[it] } ?: defaultStateButton

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

    private fun getRealStateDbValue(state: Int?, classCard: Int): Int? {
        if(state == null) return null

        return if(state != StatePlasticPacket.CARD_HOME_OFFICCES.dbValue || classCard != 0) state
        else StatePlasticPacket.PREPARE_CARD_TO_OUT.dbValue
    }

    private fun cardGetToDopMainOffice() {
        tryCatchDefaultStore(store) {
            store.cardGetFromOffice()
        }
    }

    private fun cardSendToDopOffice() {
        tryCatchDefaultStore(store) {
            store.cardSendToDopOffice()
        }
    }

    private fun outCardToClient() {
        OutCardToClient(this, store.row).showResultDialog(::processOutCardToClient)
    }

    private fun processOutCardToClient(resultOutClient: ResultOutClient) {

        tryCatchDefaultStore(store) {
            store.outCardToClient(resultOutClient)
        }
    }
}

fun tryCatchDefaultStore(store: DBStore<*>, process: ()->Unit) {
    try {
        process()
    } catch (e: Exception) {
        errorMessage(e.message)

        store.updateAllData()
    }
}