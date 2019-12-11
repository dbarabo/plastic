package ru.barabo.plastic.card.gui

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.rtf.RtfCashIn
import ru.barabo.plastic.card.entity.Card
import ru.barabo.plastic.card.service.PacketCardInfoService
import ru.barabo.plastic.card.service.StoreCardService
import ru.barabo.plastic.packet.gui.CashInDialog
import ru.barabo.plastic.packet.gui.dialogGetLimit
import ru.barabo.plastic.release.packet.data.ClassCard
import ru.barabo.plastic.release.packet.data.PlatinaCashIn
import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.unnamed.gui.errorMessage
import ru.barabo.total.report.rtf.RtfReport
import javax.swing.*


class StateButton (private val store: StoreCardService, private val toolBarParent: JToolBar, private val focusComponent: JComponent) : StoreListener<List<Card>> {
    private var state: StatePlasticPacket? = null
    private var stateButton: AbstractButton? = null

    init {
        store.addListener(this)
    }

    override fun refreshAll(elemRoot: List<Card>, refreshType: EditType) {

        val realState = getRealStateButton()

        refreshStateButtonByRealState(realState)
    }

    private fun getRealStateButton(): StatePlasticPacket? {

        return if (store.selectedEntity()?.statePlasticPacket != StatePlasticPacket.CARD_HOME_OFFICCES ||
            store.selectedEntity()?.classCard != ClassCard.Any)
            store.selectedEntity()?.statePlasticPacket
        else
            StatePlasticPacket.PREPARE_CARD_TO_OUT
    }

    private fun refreshStateButtonByRealState(realState: StatePlasticPacket?) {
        val isRemove = isRemoveStateButton(realState)

        if (realState == null || state == realState) return

        state = realState

        stateButton = getStateButton(state)?.apply {
            isEnabled = true
            toolBarParent.add(this)
        }

        if(isRemove || stateButton != null) {
            toolBarParent.repaint()
        }
    }

    private fun isRemoveStateButton(realState: StatePlasticPacket?): Boolean {
        return if (stateButton != null && realState != state) {
            stateButton?.isEnabled = false
            toolBarParent.remove(stateButton)
            state = null
            stateButton = null

            true
        } else {
            false
        }
    }

    private val mapStateButtons = mapOf(
        StatePlasticPacket.NEW to button("mc", "Оформить") { reIssueCards() },
        StatePlasticPacket.OUT to
                popup("Отправить файл ➧", "newFile") {
                    menuItem("только Текущее", "sendOne") { createFile() }
                    menuItem("Все выпущенные пачкой", "sendAll") { createFileToAllOut() }
                }, // button("newFile", "Отправить файл") { createFile() },

        StatePlasticPacket.SENT to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.SENT_OK to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.SENT_ERROR to button("returnNew", "Вернуть на отправку") { toCreateFileState() },
        StatePlasticPacket.RESPONSE_OK_ALL to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.UNCLAIMED to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.RESPONSE_ERROR_ALL to button("returnNew", "Вернуть на отправку") { toCreateFileState() },
        StatePlasticPacket.RESPONSE_ERROR_PART to button("returnNew", "Вернуть на отправку") { toCreateFileState() },
        StatePlasticPacket.OCI_ALL to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.OCI_PART to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.SMS_SENT to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.SMS_SENT_ACCESS to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.SMS_SENT_ERROR to button("sms", "Вернуть в SMS") { toSmsState() },
        StatePlasticPacket.SMS_RESPONSE_OK_ALL_OIA to button("home", "Карты в ГО") { goHome() },
        StatePlasticPacket.SMS_RESPONSE_OK_PART_OIA  to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.SMS_RESPONSE_ERROR_ALL_OIA to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.SMS_RESPONSE_ERROR_PART_OIA  to button("wait", "Ждём") { waitPc() },
        StatePlasticPacket.CARD_GO to button("toDopiki", "В доп. офис->") { toDopik() },
        StatePlasticPacket.CARD_SENT_OFFICCES to button("toGet", "Получить в офисе") { toGetInOffice() },
        StatePlasticPacket.CARD_HOME_OFFICCES to button("prepare", "Подготовить к выдаче") { prepareCardOut() },
        StatePlasticPacket.PREPARE_CARD_TO_OUT to button("outClient", "Выдать карту") { outClientOnly() },
        StatePlasticPacket.CARD_TO_CLIENT to null
    )

    private fun getStateButton(state: StatePlasticPacket?): AbstractButton? = state?.let { mapStateButtons[it] }

    private fun waitPc() {
        JOptionPane.showMessageDialog(null, MSG_WAIT, null, JOptionPane.INFORMATION_MESSAGE)
        focusComponent.requestFocus()
    }

    private fun reIssueCards() {
        processShowError {
            store.reIssueCards()

            focusComponent.requestFocus()
        }
    }

    private fun createFile() {
        processShowError {
            store.createFile()

            focusComponent.requestFocus()
        }
    }

    private fun createFileToAllOut() {
        if(!isConfirmMessageYesNo(MSG_TO_SEND_ALL, TITLE_TO_SEND_ALL)) return

        processShowError {
            store.createFileForAll()

            focusComponent.requestFocus()
        }
    }

    private fun toCreateFileState() {
        processShowError {
            store.toCreateFileState()

            focusComponent.requestFocus()
        }
    }

    private fun toSmsState() {
        processShowError {
            store.toSmsState()

            focusComponent.requestFocus()
        }
    }

    private fun goHome() {
        if(!isConfirmMessageYesNo(MSG_GO_HOME, TITLE_GO_HOME)) return

        processShowError {
            val plasticPack = store.goHomeState()

            showPacketMove(plasticPack, null)

            focusComponent.requestFocus()
        }
    }

    private fun showPacketMove(packet: Long?, departmentLabel: String? = null, checkDepartmentByUser: Boolean = false) {
        if(store.contentCount(packet) <= 1) return

        if(checkDepartmentByUser) {
            PacketCardInfoService.reSelectByDepartmentUser(packet)
        } else {
            PacketCardInfoService.reSelect(packet, departmentLabel)
        }
        DialogPacketCardInfo(focusComponent).showDialogResultOk()

        store.initData()
    }

    private fun toDopik() {
        processShowError {
            if(!store.toDopikIsSuccess()) {
                throw Exception(MSG_NOT_SENT_TO_DOPIK)
            }

            val packet = store.selectedEntity()?.plasticPack
            val department = store.selectedEntity()?.departmentName

            showPacketMove(packet, department)

            focusComponent.requestFocus()
        }
    }

    private fun toGetInOffice() {
        if(!isConfirmMessageYesNo(MSG_TO_GET, TITLE_TO_GET)) return

        processShowError {
            if(!store.toGetFromOfficeIsSuccess()) {
                errorMessage(MSG_NOT_GET_TO_DOPIK)
            }

            val packet = store.selectedEntity()?.plasticPack
            val department = store.selectedEntity()?.departmentName
            showPacketMove(packet, department)

            focusComponent.requestFocus()
        }
    }

    private fun outClientOnly() {

        val message = String.format(MSG_OUT_CLIENT_ONLY, store.selectedEntity()?.personName)
        if(!isConfirmMessageYesNo(message, TITLE_OUT_CLIENT_ONLY)) return

        processShowError {
            if(!store.outClientOnlyIsSuccess()) {
                errorMessage(MSG_NOT_OUT_CLIENT)
            }
            focusComponent.requestFocus()
        }
    }

    private fun prepareCardOut() {
        when (store.selectedEntity()?.classCard) {
            ClassCard.CreditCard -> prepareCreditCardOut()

            ClassCard.PlatinaCard -> preparePlatinaCardOut()

            else -> errorMessage("Карта не может быть подготовлена!")
        }
    }

    private fun prepareCreditCardOut() {
        val limit = dialogGetLimit() ?: return

        processShowError {
            store.prepareCreditCardOut(limit)

            focusComponent.requestFocus()
        }
    }

    private fun preparePlatinaCardOut() {
        processShowError {
            val platinaCashIn = store.beforePreparePlatinaCardOut()
            if(platinaCashIn.label == null) return@processShowError

            CashInDialog(toolBarParent, platinaCashIn, ::endOkPreparePlatina).isVisible = true
        }
    }

    private fun endOkPreparePlatina(platinaCashIn: PlatinaCashIn) {
        processShowError {
            val idCashIn = store.endPreparePlatinaCardOut(platinaCashIn)

            RtfReport.build(RtfCashIn(idCashIn))
        }
    }
}

fun isConfirmMessageYesNo(message: String, title: String = ""): Boolean {
    UIManager.put("OptionPane.yesButtonText", "Да")
    UIManager.put("OptionPane.noButtonText", "Нет")

    val reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION)

    return (reply == JOptionPane.YES_OPTION)
}

fun inputDialog(message: String, inputValue: String? = null): String? {

    return JOptionPane.showInputDialog(null,null, message,
        JOptionPane.QUESTION_MESSAGE, null,null, inputValue) as String
}

fun showMessage(message: String?): Boolean {
    JOptionPane.showMessageDialog(null, message, null, JOptionPane.INFORMATION_MESSAGE)

    return true
}

private const val MSG_TO_SEND_ALL = "Все заявления в состоянии 'ВЫПУЩЕН' отправить в ПЦ одной пачкой?"

private const val TITLE_TO_SEND_ALL = "Отправка файла в ПЦ"

private const val MSG_WAIT = "Сейчас нужно просто ждать, когда придут ответные файлы от ПЦ"

private const val MSG_GO_HOME = "Перевести карты в Головной офис?"

private const val TITLE_GO_HOME = "Карты в 'ГО'"

private const val MSG_TO_GET = "Перевести карты в состояние 'Получено в доп. офисах'?"

private const val TITLE_TO_GET = "Карты в Доп. офисах"

private const val MSG_NOT_SENT_TO_DOPIK = "Карта не отправлена в доп. офис.\nТолько сотрудник ГО может это сделать"

private const val MSG_NOT_GET_TO_DOPIK = "Карта не получена в доп. офисе.\nТолько сотрудник офиса, которому предазначалась карта может это сделать"

private const val MSG_OUT_CLIENT_ONLY = "Перевести карту %s в конечное состояние 'Выдано клиенту'?"

private const val TITLE_OUT_CLIENT_ONLY = "Карту клиенту"

private const val MSG_NOT_OUT_CLIENT = "Карта не может быть выдана клиенту.\nВозможно Ваше подразделение не соответствует выдаваемому подразделению"