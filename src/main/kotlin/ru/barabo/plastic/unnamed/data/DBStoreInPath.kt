package ru.barabo.plastic.unnamed.data

import oracle.jdbc.OracleTypes
import org.apache.log4j.Logger
import ru.barabo.afina.AfinaQuery
import ru.barabo.afina.clobToString
import ru.barabo.db.SessionException
import ru.barabo.plastic.gui.PlasticGui
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.general.FilteredStoreInPath
import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.impl.AbstractFilterStore
import ru.barabo.total.db.impl.AbstractRowFields
import java.io.File
import java.nio.charset.Charset
import java.sql.Clob
import java.time.LocalDate
import java.time.format.DateTimeFormatter

open class DBStoreInPath(protected val dbStorePlastic: DBStorePlastic) : AbstractFilterStore<RowFieldInPath>(),
    FilteredStoreInPath<RowFieldInPath> {

    private val logger = Logger.getLogger(DBStoreInPath::class.simpleName)

    override fun selectApplicationStore() {

        val applicationId =  row.getApplicationId()?.toInt() ?: throw SessionException(MSG_ERROR_NO_CONTENT)

        PlasticGui.openApplicationForm(applicationId)
    }

    override fun createUnnamedCards(countCards: Int, productCardTypeId: Long) {
        logger.error("before createUnnamedCards countCards=$countCards productCardTypeId=$productCardTypeId")
        val values = AfinaQuery.execute(CREATE_UNNAMED_CARDS, arrayOf<Any?>(countCards, productCardTypeId), intArrayOf(OracleTypes.NUMBER))

        logger.error("AFTER createUnnamedCards values=$values")

        saveFilePacket(values!![0]!!)

        updateAllData()
    }

    override fun goHomeCard() {
        row?.getPacketId()?.let {
            AfinaQuery.execute(EXECUTE_GO_HOME_PACKET, arrayOf(it))

            1
        } ?: throw SessionException(MSG_ERROR_NO_CONTENT)

        updateAllData()
    }

    override fun getTypeSelect(): Int = 0

    override fun setViewType(type: Int) {}

    override fun getFields(): MutableList<FieldItem> = row?.fieldItems() ?: RowFieldInPath().fieldItems()

    override fun createEmptyRow(): RowFieldInPath? = null

    override fun cloneRow(row: RowFieldInPath?): RowFieldInPath? = null

    override fun initData(): MutableList<RowFieldInPath> =
        AfinaQuery.selectCursor(SELECT_INPATH).map { createRowField<RowFieldInPath>(it) }.toMutableList()

    override fun remove(row: RowFieldInPath?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertRow(row: RowFieldInPath?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateRow(oldData: RowFieldInPath?, newData: RowFieldInPath?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun saveFilePacket(packetId: Any, execQuery: String = CREATE_FILE) {

            val file = File("${hCardOutToday()}/${getApplicationFileName()}")

            val clob = AfinaQuery.execute(execQuery,
                arrayOf(packetId, file.name), intArrayOf(OracleTypes.CLOB))!![0] as Clob

            file.writeText(clob.clobToString(), charset = Charset.forName("cp1251"))
        }

        private const val SELECT_INPATH = "{ ? = call od.PTKB_PLASTIC_AUTO.getUnnamedCards }"

        private const val CREATE_UNNAMED_CARDS = "{ call od.PTKB_PLASTIC_AUTO.createUnnamedCards(?, ?, ?) }"

        private const val CREATE_FILE = "{ call od.PTKB_PLASTIC_AUTO.createAppFileData(?, ?, ?) }"

        private const val EXECUTE_GO_HOME_PACKET = "{ call od.PTKB_PLASTIC_AUTO.goHomePacket(?) }"
    }
}


const val MSG_ERROR_NO_CONTENT = "Не указано ни одной текущей позиции в таблице"

inline fun <reified T: AbstractRowFields> createRowField(row: Array<Any?>): T = AbstractRowFields.create(row, T::class.java)

val H_CARD = "H:/КартСтандарт".ifTest("C:/КартСтандарт")

private val H_CARD_OUT = "$H_CARD/OUT"

fun String.byFolderExists(): String {

    return this.apply {
        val folder = File(this)

        if(!folder.exists()) {
            folder.mkdirs()
        }
    }
}

fun String.ifTest(testPath: String) = if(AfinaQuery.isTestBaseConnect())testPath else this

fun todayFolder(): String = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDate.now())

fun hCardOutToday() = "$H_CARD_OUT/${todayFolder()}".byFolderExists()

private const val SELECT_FILENAME = "select od.PTKB_PLASTIC_AUTO.getFileNameIIA from dual"

fun getApplicationFileName() = AfinaQuery.selectValueType<String>(SELECT_FILENAME)