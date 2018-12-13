package ru.barabo.plastic.unnamed.data

import oracle.jdbc.OracleTypes
import org.apache.log4j.Logger
import ru.barabo.db.SessionException
import ru.barabo.db.SessionSetting
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.afina.clobToString
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.general.FilteredStoreInHome
import ru.barabo.plastic.unnamed.general.ResultOutClient
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset
import java.sql.Clob

class DBStoreInHome(dbStorePlastic: DBStorePlastic) : DBStoreInPath(dbStorePlastic),
    FilteredStoreInHome<RowFieldInPath> {

    private val logger = Logger.getLogger(DBStoreInHome::class.java.name)

    override fun outCardToClient(resultOutClient: ResultOutClient) {

        logger.info("resultOutClient.clientId=${resultOutClient.clientId}")
        logger.info("resultOutClient.contentId=${resultOutClient.contentId}")
        logger.info("resultOutClient.phone=${resultOutClient.phone}")
        logger.info("resultOutClient.codeWord=${resultOutClient.codeWord}")

        outCard(resultOutClient)

        sendIvr(resultOutClient.contentId)

        updateAllData()
    }

    private fun outCard(resultOutClient: ResultOutClient) {

        val uniqueSession = AfinaQuery.uniqueSession()

        try {
            AfinaQuery.execute( EXEC_BIND_CLIENT, resultOutClient.toParamsBind(), uniqueSession)

            AfinaQuery.execute( EXEC_OUT_CLIENT_CONTENT, resultOutClient.paramContentId(), uniqueSession)

            smsOn(resultOutClient, uniqueSession)

            sendBTRT30(resultOutClient, uniqueSession)

            AfinaQuery.commitFree(uniqueSession)
        } catch (e: Exception) {
            logger.error(e)

            AfinaQuery.rollbackFree(uniqueSession)

            throw SessionException(e.message!!)
        }
    }

    private fun sendBTRT30(resultOutClient: ResultOutClient, session: SessionSetting) {

        createIIAFile(CREATE_BTRT30_FILE, resultOutClient.contentId, session) // 1182344115
    }

    private fun smsOn(resultOutClient: ResultOutClient, session: SessionSetting) {

        createIIAFile(CREATE_SMS_ADD_FILE, resultOutClient.contentId, session)
    }

    override fun initData(): MutableList<RowFieldInPath> =
        AfinaQuery.selectCursor(SELECT_IN_HOME).map { createRowField<RowFieldInPath>(it) }.toMutableList()

    companion object {
        private const val SELECT_IN_HOME = "{ ? = call od.PTKB_PLASTIC_AUTO.getUnnamedCardsInHome }"

        private const val EXEC_BIND_CLIENT = "{ call od.PTKB_PLASTIC_AUTO.bindClientToUnnamedCard(?, ?, ?, ?) }"

        private const val EXEC_OUT_CLIENT_CONTENT = "{ call od.PTKB_PLASTIC_AUTO.cardToOneClient(?) }"

        private const val CREATE_SMS_ADD_FILE = "{ call od.PTKB_PLASTIC_AUTO.createSmsToUnnamedClient(?, ?, ?) }"

        private const val CREATE_BTRT30_FILE = "{ call od.PTKB_PLASTIC_AUTO.createBtrt30ToUnnamedClient(?, ?, ?)} "
    }
}

private fun ResultOutClient.toParamsBind(): Array<Any?> = arrayOf(contentId, clientId, phone, codeWord)

private fun ResultOutClient.paramContentId(): Array<Any?> = arrayOf(contentId)

fun createIIAFile(query: String, firstParamId: Number, session: SessionSetting = SessionSetting(false)) {
    val file = File("${hCardOutToday()}/${getApplicationFileName()}")

    val clob = AfinaQuery.execute(query, arrayOf(firstParamId, file.name),
        session, intArrayOf(OracleTypes.CLOB))!![0] as Clob

    file.writeText(clob.clobToString(), charset = Charset.forName("cp1251"))
}


private const val SELECT_NEW_CARD_CONTENT = "select pc.new_card from od.ptkb_plast_pack_content pc where pc.id = ?"

fun sendIvr(idContent: Number) {

    val newCard = AfinaQuery.selectValueType<Number>(SELECT_NEW_CARD_CONTENT, arrayOf(idContent))
        ?: throw SessionException("Не найдена карта для контента id=[$idContent]")

    AfinaQuery.sendIvrRequest(newCard)
}
