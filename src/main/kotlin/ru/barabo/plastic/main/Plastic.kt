package ru.barabo.plastic.main

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import ru.barabo.afina.AfinaQuery
import ru.barabo.plastic.main.CheckerXFile.dateFile
import ru.barabo.plastic.main.CheckerXFile.fileName
import ru.barabo.plastic.main.gui.Start
import java.io.File
import java.nio.charset.Charset
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import javax.xml.parsers.SAXParserFactory
import kotlin.concurrent.timer

object Plastic {

    fun runApplication() {
        Start()
    }
}

fun main() {

    Plastic.runApplication()
}

object VerCheck {

    private val timer = timer(name = this.javaClass.simpleName, initialDelay = 30_000, daemon = false, period = 600_000) { checkRun() }

    @JvmStatic
    fun startCheck() {

        CheckerFiles.initStart()

        CheckerXmFile.initStart(CheckerFiles.findFiles)

        timer.apply {  }
    }

    private fun checkRun() {
        try {
            CheckerFiles.findProcess()
            CheckerXmFile.findProcess()
        } catch (_: Exception) {

        }
    }

    @JvmStatic
    fun exitCheckVersion() {

        timer.cancel()
        timer.purge()
    }
}

fun Pattern.isFind(name: String): Boolean = this.matcher(name).matches()

object CheckerFiles : QuoteSeparatorLoader {

    private val pathVt = File(PATH_VT)

    private val patternVt = Pattern.compile(REGEXP_VT, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)

    val findFiles: MutableList<String> = ArrayList()

    fun initStart() {

        val files: List<String> = AfinaQuery.selectCursor(SEL_F).map { it[0] as String }

        findFiles.addAll( files )
    }

    fun findProcess() {

        if(findFiles.isEmpty()) return

        try {
            processNoError()
        } catch (_: Exception) {
        }
    }

    private fun processNoError() {
        val newFiles = pathVt.listFiles { f ->
            (!f.isDirectory) &&
                    (patternVt.isFind(f.name)) &&
                    (!findFiles.contains(f.name.uppercase(Locale.getDefault())))
        }

        if (newFiles.isNullOrEmpty()) return

        for (newFile in newFiles) {
            val isExists = (AfinaQuery.selectValue(
                SELECT_CHECK_FILE,
                arrayOf(newFile.name.uppercase(Locale.getDefault()))
            ) as Number).toInt()

            if (isExists == 0) {
                processFileVt(newFile)
            }
            findFiles.add(newFile.name.uppercase(Locale.getDefault()))
        }
    }

    private lateinit var fileProcess: File

    private fun processFileVt(newFile: File) {
        fileProcess = newFile

        load(newFile, Charset.forName("CP1251"))
    }

    override val headerColumns: Map<Int, (String?) -> Any> = emptyMap()
    override val headerQuery: String? = null

    override val tailColumns: Map<Int, (String?) -> Any> = emptyMap()
    override val tailQuery: String? = null

    override val bodyColumns: Map<Int, (String?) -> Any> = mapOf(
        0 to ::parseToString,
        1 to ::parseNumberSeparator,
        2 to ::parseToUpperString,
        3 to ::parseToString,
        5 to ::parseNumberSeparator,
        -1 to ::fileProcessName,
        -2 to ::dateCreated
    )

    private fun dateCreated(@Suppress("UNUSED_PARAMETER") value: String?): Any = dateFile

    private fun fileProcessName(@Suppress("UNUSED_PARAMETER") value: String?): Any = fileProcess.name.uppercase(Locale.getDefault())

    private fun parseToUpperString(value: String?): Any =
        value?.trim()?.uppercase(Locale.getDefault()) ?: String::class.javaObjectType

    override val bodyQuery: String = INSERT_CLIENT

    private lateinit var dateFile: Timestamp

    override fun getTypeLine(fields: List<String>, order: Int): TypeLine {
        if (fields.isEmpty()) return TypeLine.NOTHING

        return when (fields[0].uppercase(Locale.getDefault())) {
            "START" -> {
                dateFile = fields[1].toDate().toTimestamp()
                TypeLine.NOTHING
            }
            "END" -> TypeLine.NOTHING
            else -> TypeLine.BODY
        }
    }
}

fun parseNumberSeparator(value :String?): Any {
    val length = value?.trim()?.length?:0

    if(length == 0) return Double::class.javaObjectType

    val numberOrder = value?.replace("\\D+".toRegex(), "")

    return numberOrder?.trim()?.toLong()?:0
}

fun String.toDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("ddMMyyyy"))

fun String.toDateXml(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

fun LocalDate.toTimestamp(): Timestamp = Timestamp(this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() )

private const val PATH_VT = "H:/Dep_Buh/Зарплатный проект ВТБ/Исходящие файлы/Зарплата"

private const val REGEXP_VT = "Z_0000311595_\\d\\d\\d\\d\\d\\d\\d\\d_\\d\\d_\\d\\d\\.txt"

const val SELECT_CHECK_FILE = "select od.PTKB_PLASTIC_TURNOUT.checkFileExistsZil( ? ) from dual"

const val INSERT_CLIENT =
    "insert into od.ptkb_zil_client (ID, CODE_ID, AMOUNT, CLIENT, CODE_ID2, AMOUNT_HOLD, FILE_NAME, CREATED) values (classified.nextval, ?, ?, ?, ?, ?, ?, ?)"

const val SEL_F = "{ ? = call od.PTKB_PLASTIC_TURNOUT.getExistsFiles }"

object CheckerXmFile {

    private val pathA = File(PATH_A)

    private val patternA = Pattern.compile(REGEXP_A, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)

    private val findFiles: MutableList<String> = ArrayList()

    fun initStart(files: List<String>) {
        findFiles.addAll(files)
    }

    fun findProcess() {

        if(findFiles.isEmpty()) return

        try {
            processNoError()
        } catch (_: Exception) {
        }
    }

    private fun processNoError() {
        val newFiles = pathA.listFiles { f ->
            (!f.isDirectory) &&
                    (patternA.isFind(f.name)) &&
                    (!findFiles.contains(f.name.uppercase(Locale.getDefault())))
        }

        if(newFiles.isNullOrEmpty()) return

        for(newFile in newFiles) {
            val isExists = (AfinaQuery.selectValue(SELECT_CHECK_FILE,
                arrayOf(newFile.name.uppercase(Locale.getDefault()))) as Number).toInt()

            if(isExists == 0) {
                addData(newFile)
            }

            findFiles.add(newFile.name.uppercase(Locale.getDefault()))
        }
    }

    private fun addData(file: File) {

        val parser = SAXParserFactory.newInstance().newSAXParser()

        val handler = HablerZp(file.name)

        parser.parse(file, handler)
    }
}

class HablerZp(val fileName: String) : DefaultHandler() {

    var date: Timestamp? = null

    var data: StringBuilder = StringBuilder()

    private var family: String = ""

    private var name: String = ""

    private var secondName: String = ""

    private var amount: Any? = null

    private var account: String = ""

    @Throws(SAXException::class)
    override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes?) {

        when(qName) {
            "СчетаПК" -> {
                date = attributes?.getValue("ДатаФормирования")?.toDateXml()?.toTimestamp()
            }
        }
        data = StringBuilder()
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {

        when (qName) {
            "Фамилия" -> { family = data.toString().uppercase(Locale.getDefault()) }

            "Имя" -> { name = data.toString().uppercase(Locale.getDefault()) }

            "Отчество" -> { secondName = data.toString().uppercase(Locale.getDefault()) }

            "Сумма" -> { amount = parseNumberSeparator(data.toString()) }

            "ЛицевойСчет" -> { account = data.toString() }

            "Сотрудник" -> {
                saveRecord()
            }
            else -> {}
        }
    }

    private fun saveRecord() {

        val fio = "$family $name $secondName"

        val params: Array<Any?> = arrayOf(account, amount, fio, fileName.uppercase(Locale.getDefault()), date)

        AfinaQuery.execute(INSERT_INFO, params)
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray?, start: Int, length: Int) {
        data.append(String(ch!!, start, length))
    }
}

const val INSERT_INFO =
    "insert into od.ptkb_zil_client (ID, CODE_ID, AMOUNT, CLIENT, FILE_NAME, CREATED) values (classified.nextval, ?, ?, ?, ?, ?)"

object CheckerXFile {

    private lateinit var newBook: Workbook

    private lateinit var sheet: Sheet

    private lateinit var fileName: String

    private lateinit var dateFile: Timestamp

    private val pathOt = File(PATH_OT)

    private val patternOt = Pattern.compile(REGEXP_OT, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)

    private val findFiles: MutableList<String> = ArrayList()

    fun initStart(files: List<String>) {
        findFiles.addAll(files)
    }

    fun findProcess() {

        if(findFiles.isEmpty()) return

        try {
            processNoError()
        } catch (_: Exception) {
        }
    }

    private fun processNoError() {
        val newFiles = pathOt.listFiles { f ->
            (!f.isDirectory) &&
                    (patternOt.isFind(f.name)) &&
                    (!findFiles.contains(f.name.uppercase(Locale.getDefault())))
        }

        if(newFiles.isNullOrEmpty()) return

        for(newFile in newFiles) {
            val isExists = (AfinaQuery.selectValue(SELECT_CHECK_FILE,
                arrayOf(newFile.name.uppercase(Locale.getDefault()))) as Number).toInt()

            if(isExists == 0) {
                addData(newFile)
            }

            findFiles.add(newFile.name.uppercase(Locale.getDefault()))
        }
    }

    private fun addData(file: File) {

        try {
            fileName = file.name.uppercase(Locale.getDefault())

            dateFile = fileName.substring(6..13).toDate().toTimestamp()

            newBook = createNewBook(file) ?: return

            sheet = newBook.getSheetAt(0)

            val rows = sheet.iterator()

            readRows(rows)
        } finally {
            newBook.close()
        }
    }

    private fun readRows(rows: MutableIterator<Row>) {
        while (rows.hasNext()) {
            val rowXls: Row = rows.next()
            if (rowXls.rowNum == 0) continue

            val lastName = rowXls.getCell(1)?.takeIf { it.cellType == CellType.STRING }?.stringCellValue?.trim()
                ?.uppercase(Locale.getDefault()) ?: return

            if (lastName.isEmpty()) return

            val firstName = rowXls.getCell(2).stringCellValue.trim().uppercase(Locale.getDefault())

            val secondName = rowXls.getCell(3).stringCellValue.trim().uppercase(Locale.getDefault())

            val codeId = rowXls.getCell(4).stringCellValue.trim()

            val amount = (rowXls.getCell(5).numericCellValue * 100).toLong()

            val amountHold = parseNumberSeparator(rowXls.getCell(7).stringCellValue)

            val fio = "$lastName $firstName $secondName".trim()

            val params: Array<Any?> = arrayOf(codeId, amount, fio, amountHold, fileName, dateFile)

            AfinaQuery.execute(INSERT_CLIENT_HOLD, params)
        }
    }
}

private fun createNewBook(templateFile: File): Workbook? {

    return try {
        XSSFWorkbook(templateFile)

    } catch (e: Exception) {
        null
    }
}

private const val REGEXP_OT = "\\d.*z\\.xml"

private const val REGEXP_A = "\\d.*z\\.xml"

private const val PATH_OT = "H:/Dep_Buh/Зарплатный проект Открытие/Исходящие файлы/Зарплата"

private const val PATH_A = "H:/Dep_Buh/Зарплатный проект АЛЬФА-БАНК/Реестры на выгрузку"

private const val INSERT_CLIENT_HOLD =
    "insert into od.ptkb_zil_client (ID, CODE_ID, AMOUNT, CLIENT, AMOUNT_HOLD, FILE_NAME, CREATED) values (classified.nextval, ?, ?, ?, ?, ?, ?)"