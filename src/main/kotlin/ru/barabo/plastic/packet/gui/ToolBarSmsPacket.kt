package ru.barabo.plastic.packet.gui

import ru.barabo.gui.swing.ButtonKarkas
import ru.barabo.plastic.release.packet.data.PacketRowField
import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.plastic.release.reissue.gui.TopToolBarReIssueCard
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacket
import ru.barabo.total.db.DBStore
import javax.swing.JComponent

class ToolBarSmsPacket(private val store: DBStore<PacketRowField>, focusComp: JComponent) : ToolBarPacket<PacketRowField>(store, focusComp) {

    private val mapStateButtons = mapOf(
        StatePlasticPacket.NEW to ButtonKarkas("sms", "Отправить на SMS") { this.toCreateFileSmsState() },
        StatePlasticPacket.OUT to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SENT to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SENT_OK to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SENT_ERROR to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.RESPONSE_OK_ALL to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.UNCLAIMED to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.RESPONSE_ERROR_ALL to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.RESPONSE_ERROR_PART to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.OCI_ALL to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.OCI_PART to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_SENT to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_SENT_ACCESS to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_SENT_ERROR to ButtonKarkas("sms", "Вернуть в SMS") { this.toSmsState() },
        StatePlasticPacket.SMS_RESPONSE_OK_ALL_OIA to ButtonKarkas(null, null),
        StatePlasticPacket.SMS_RESPONSE_OK_PART_OIA  to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_RESPONSE_ERROR_ALL_OIA to ButtonKarkas("wait", "Ждём") { this.waitPc() },
        StatePlasticPacket.SMS_RESPONSE_ERROR_PART_OIA  to ButtonKarkas("wait", "Ждём") { this.waitPc() }
    )

    private fun toCreateFileSmsState() {
        val dBStoreSmsPacket = store as DBStoreSmsPacket
        val result = dBStoreSmsPacket.createSms()
        if (result != null) {
            TopToolBarReIssueCard.messageError(result)
        }
        dBStoreSmsPacket.updateAllData()
    }

    private fun toSmsState() {
        val dBStoreSmsPacket = store as DBStoreSmsPacket
        val result = dBStoreSmsPacket.toSmsState()
        if (result != null) {
            TopToolBarReIssueCard.messageError(result)
        }
        dBStoreSmsPacket.updateAllData()

        focusComp.requestFocus()
    }

    override fun getStateButton(state: StatePlasticPacket?): ButtonKarkas? {
        if(mapStateButtons == null) return null

        return state?.let { mapStateButtons[it] }
    }
}