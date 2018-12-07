package ru.barabo.plastic.afina

import oracle.jdbc.OracleTypes.NUMBER
import oracle.jdbc.OracleTypes.CLOB
import ru.barabo.db.Query
import ru.barabo.db.SessionException
import ru.barabo.plastic.release.ivr.xml.IvrInfo
import ru.barabo.plastic.release.ivr.xml.IvrXml
import ru.barabo.total.resources.owner.CfgTotal
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset
import java.sql.Clob

object AfinaQuery : Query(AfinaConnect) {

    @JvmStatic
    @Synchronized
    fun nextSequence(): Number = selectValue(query = NEXT_SEQUENCE) as Number

    private const val NEXT_SEQUENCE = "select classified.nextval from dual"

    private const val SEL_USER = "select user from dual"

    @JvmStatic
    fun getUser(): String = selectValue(query = SEL_USER) as String

    @JvmStatic
    fun isTestBaseConnect() = dbConnection.isTestBase()

    @JvmStatic
    @Throws(SessionException::class)
    fun execBbrRtf(procedureName: String, query: String, params :Array<Any?>?, fileSave: File, charset: String): File? {

        val data = getBbrText(procedureName, query, params) ?: return null

        fileSave.writeText(data.clobToString(), Charset.forName(charset))

        return fileSave
    }

    private fun getBbrText(procedureName: String, query: String, params :Array<Any?>?): Clob? {
        val settings = uniqueSession()

        val data = try {
            val outValues = execute(CfgTotal.query().prepareBbr(),
                arrayOf(procedureName), settings, intArrayOf(NUMBER, NUMBER, NUMBER))

            execute(query, params, settings, null)

            execute(CfgTotal.query().bbrText, outValues?.toTypedArray(), settings, intArrayOf(CLOB) )?.apply {
                commitFree(settings)
            }
        } catch (e: Exception) {
            rollbackFree(settings)

            throw e
        }

        return if(data.isNullOrEmpty() ) null else data[0] as? Clob
    }

    fun sendIvrRequest(newCardId: Number) {
        if(isTestBaseConnect()) throw SessionException("Невозможно отправить IVR-запрос на тестовой базе")

        val ivrInfo = IvrInfo.create(newCardId)

        ivrInfo?.let { IvrXml.startIvrProccessByDb(it) } ?: throw SessionException("Ошибка при создании IVR-запроса для карты: $newCardId")
    }
}

fun Clob.clobToString() = this.getSubString(1, this.length().toInt())