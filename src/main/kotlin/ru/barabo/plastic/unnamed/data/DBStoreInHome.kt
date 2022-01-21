package ru.barabo.plastic.unnamed.data

import oracle.jdbc.OracleTypes
import org.apache.log4j.Logger
import ru.barabo.afina.AfinaQuery
import ru.barabo.afina.clobToString
import ru.barabo.db.SessionException
import ru.barabo.db.SessionSetting
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.entity.Department
import ru.barabo.plastic.unnamed.general.FilteredStoreInHome
import ru.barabo.plastic.unnamed.general.ResultOutClient
import ru.barabo.plastic.unnamed.service.DepartmentService
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset
import java.sql.Clob

class DBStoreInHome(dbStorePlastic: DBStorePlastic) : DBStoreInPath(dbStorePlastic),
    FilteredStoreInHome<RowFieldInPath> {

    private val logger = Logger.getLogger(DBStoreInHome::class.java.name)

    override fun cardSendToDopOffice() {
        row?.getPacketId()?.let {
            AfinaQuery.execute(EXECUTE_TO_MAIN_OFFICE_PACKET, arrayOf(it))

            1
        } ?: throw SessionException(MSG_ERROR_NO_CONTENT)

        updateAllData()
    }

    override fun moveCardToDepartment(toDepartment: Department?) {
        val department = toDepartment ?: throw Exception("Не выбран офис назначения")

        AfinaQuery.execute(EXEC_MOVE_CARD_TO_DEPARTMNENT, arrayOf(department.id, row?.id))

        updateAllData()
    }

    override fun cardGetFromOffice() {
        row?.getPacketId()?.let {
            AfinaQuery.execute(EXECUTE_TO_OFFICE_FROM_OFFICE_PACKET, arrayOf(it))

            1
        } ?: throw SessionException(MSG_ERROR_NO_CONTENT)

        updateAllData()
    }

    override fun outCardToClient(resultOutClient: ResultOutClient) {

        logger.info("resultOutClient.clientId=${resultOutClient.clientId}")
        logger.info("resultOutClient.contentId=${resultOutClient.contentId}")
        logger.info("resultOutClient.phone=${resultOutClient.phone}")
        logger.info("resultOutClient.codeWord=${resultOutClient.codeWord}")

        outCard(resultOutClient)

        sendIvr(resultOutClient.contentId)

        updateAllData()
    }

    override fun prepareOutCard(resultOutClient: ResultOutClient, limit: Number) {

        prepareOut(resultOutClient, limit)

        updateAllData()
    }

    private fun prepareOut(resultOutClient: ResultOutClient, limit: Number) {
        val uniqueSession = AfinaQuery.uniqueSession()

        try {
            AfinaQuery.execute( EXEC_BIND_CLIENT, resultOutClient.toParamsBind(), uniqueSession)

            AfinaQuery.execute( EXEC_PREPARE_OUT_CARD, arrayOf<Any?>(resultOutClient.contentId, limit), uniqueSession)

            AfinaQuery.commitFree(uniqueSession)
        } catch (e: Exception) {
            logger.error(e)

            AfinaQuery.rollbackFree(uniqueSession)

            throw SessionException(e.message!!)
        }
    }

    private fun outCard(resultOutClient: ResultOutClient) {

        val uniqueSession = AfinaQuery.uniqueSession()

        try {
            AfinaQuery.execute( EXEC_BIND_CLIENT, resultOutClient.toParamsBind(), uniqueSession)

            AfinaQuery.execute( EXEC_OUT_CLIENT_CONTENT, resultOutClient.paramContentId(), uniqueSession)

            val file = smsOn(resultOutClient, uniqueSession)

            sendBTRT30(resultOutClient, uniqueSession, file.name)

            AfinaQuery.commitFree(uniqueSession)
        } catch (e: Exception) {
            logger.error(e)

            AfinaQuery.rollbackFree(uniqueSession)

            throw SessionException(e.message!!)
        }
    }

    private fun sendBTRT30(resultOutClient: ResultOutClient, session: SessionSetting, notFileName: String): File =
        createIIAFile(CREATE_BTRT30_FILE, resultOutClient.contentId, session, notFileName)


    private fun smsOn(resultOutClient: ResultOutClient, session: SessionSetting): File =
        createIIAFile(CREATE_SMS_ADD_FILE, resultOutClient.contentId, session)

    override fun initData(): MutableList<RowFieldInPath> =
        AfinaQuery.selectCursor(SELECT_IN_HOME).map { createRowField<RowFieldInPath>(it) }.toMutableList()

    companion object {
        private val EXEC_MOVE_CARD_TO_DEPARTMNENT = "{ call od.PTKB_PLASTIC_AUTO.moveUnnamedCardToOffice(?, ?)}"

        private const val SELECT_IN_HOME = "{ ? = call od.PTKB_PLASTIC_AUTO.getUnnamedCardsInHome }"

        private const val EXEC_PREPARE_OUT_CARD = "{ call od.PTKB_PLASTIC_AUTO.prepareOutCard(?, ?) }"

        private const val EXEC_BIND_CLIENT = "{ call od.PTKB_PLASTIC_AUTO.bindClientToUnnamedCard(?, ?, ?, ?) }"

        private const val EXEC_OUT_CLIENT_CONTENT = "{ call od.PTKB_PLASTIC_AUTO.cardToOneClient(?) }"

        private const val CREATE_SMS_ADD_FILE = "{ call od.PTKB_PLASTIC_AUTO.createSmsToUnnamedClient(?, ?, ?) }"

        private const val CREATE_BTRT30_FILE = "{ call od.PTKB_PLASTIC_AUTO.createBtrt30ToUnnamedClient(?, ?, ?)} "

        private const val EXECUTE_TO_MAIN_OFFICE_PACKET =  "{ call od.PTKB_PLASTIC_AUTO.toMainOfficeDopik(?) }"

        private const val EXECUTE_TO_OFFICE_FROM_OFFICE_PACKET =  "{ call od.PTKB_PLASTIC_AUTO.getHomesPacket(?) }"
    }
}

private fun ResultOutClient.toParamsBind(): Array<Any?> = arrayOf(contentId, clientId, phone, codeWord)

private fun ResultOutClient.paramContentId(): Array<Any?> = arrayOf(contentId)

fun createIIAFile(query: String, firstParamId: Number,
                  session: SessionSetting = SessionSetting(false),
                  notFileName: String = ""): File {
    val file = unicFileName(notFileName)

    val clob = AfinaQuery.execute(query, arrayOf(firstParamId, file.name),
        session, intArrayOf(OracleTypes.CLOB))!![0] as Clob

    file.writeText(clob.clobToString(), charset = Charset.forName("cp1251"))

    return file
}

fun unicFileName(notFileName: String): File {

    val file = File("${hCardOutToday()}/${getApplicationFileName()}")

    if(notFileName.isNotEmpty() && file.name == notFileName) {
        Thread.sleep(1000)

        return unicFileName(notFileName)
    }
    return file
}

private const val SELECT_NEW_CARD_CONTENT = "select pc.new_card from od.ptkb_plast_pack_content pc where pc.id = ?"

fun sendIvr(idContent: Number) {

    val newCard = AfinaQuery.selectValueType<Number>(SELECT_NEW_CARD_CONTENT, arrayOf(idContent))
        ?: throw SessionException("Не найдена карта для контента id=[$idContent]")

    // AfinaQuery.sendIvrRequest(newCard)
}
