package ru.barabo.plastic.afina

import ru.barabo.afina.AfinaQuery
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.gui.tryCatchDefaultStore

object TimerThreadData {

    @Volatile private var platinaCashInWaitExec: Long = 0L
    @Volatile private var platinaContentId: Long = 0L

    private const val SELECT_IS_EXEC_PLATINA_CASH_IN = "select docstate from od.doctree where classified = ?"

    private const val EXEC_CONTENT_TO_OUT_CARD_STATE = "{ call od.PTKB_PLASTIC_AUTO.updateContentIdToPrepareState(?) }"

    @JvmStatic
    fun setPlatinaCashInId(cashInId: Number, contentId: Number) {
        platinaCashInWaitExec = cashInId.toLong()
        platinaContentId = contentId.toLong()
    }

    fun processCheckPlatinaCashInIsExec() {

        if(platinaCashInWaitExec == 0L || platinaContentId == 0L) return

        val params: Array<Any?> = arrayOf(platinaCashInWaitExec)

        val result = AfinaQuery.select(SELECT_IS_EXEC_PLATINA_CASH_IN, params)

        val row = if(result.isNotEmpty()) result[0] else return

        val state = if(row.isNotEmpty()) row[0] as? Number else return

        if(state?.toLong() == 1000000035L && platinaContentId != 0L) {
            processToOutPlatinaContent()
        }
    }

    private fun processToOutPlatinaContent() {

        val packetDb =  DBStorePlastic.getInstance().packet

        tryCatchDefaultStore(packetDb) {
            AfinaQuery.execute(EXEC_CONTENT_TO_OUT_CARD_STATE, arrayOf(platinaContentId))

            platinaCashInWaitExec = 0L
            platinaContentId = 0L

            packetDb.updateAllData()
        }
    }
}