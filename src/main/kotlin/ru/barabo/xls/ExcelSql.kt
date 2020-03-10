package ru.barabo.xls

import jxl.write.*
import jxl.write.Number
import org.slf4j.LoggerFactory
import ru.barabo.db.Query
import java.awt.Container
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ExcelSql(private val template: File, query: Query, private val generateNewFile:(File)->File) {

    private val logger = LoggerFactory.getLogger(ExcelSql::class.java)

    private lateinit var newBook: WritableWorkbook

    private var newFile: File? = null

    private lateinit var sheet: WritableSheet

    private lateinit var rowData: List<Row>

    private lateinit var vars: MutableList<Var>

    private val parser: Parser = Parser(query)

    fun requestParam(container: Container, afterProcess: (File)->Unit = {}) {
        checkErrorByRow(0) {
            if(rowData.isEmpty() || rowData[0].tag !is ParamTag) throw Exception("не найдены параметры")

            buildRow(rowData[0], 0)

            val params = rowData[0].tag as ParamTag

            buildParams(container, params.params) {
                if(newFile == null) {
                    initNewBook()
                }

                processData(1)

                afterProcess(newFile!!)
                newFile = null
            }
        }
    }

    fun processData(startRowIndex: Int = 0) {

        executeData(startRowIndex)

        parser.rollbackAfterExec()

        for (columnIndex in 0 until DATA_COLUMN)  sheet.setColumnView(columnIndex, 0)
        newBook.save()
    }


    private fun initNewBook() {
        newFile = generateNewFile(template)

        newBook = createNewBook(newFile!!, template)

        sheet = newBook.getSheet(0)
    }

    fun initRowData(vars: MutableList<Var>) {
        initNewBook()

        this.vars =  vars

        val rowData = ArrayList<Row>()

        for (rowIndex in 0 until sheet.rows) {

            checkErrorByRow(rowIndex) {
                val expression = getExpression(rowIndex)

                val tag = getTagRow(rowIndex)

                val columns = getColumns(rowIndex)

                rowData += Row(tag, rowIndex, expression, columns)
            }
        }
        this.rowData = rowData
    }

    private fun executeData(startRowIndex: Int = 0) {
        if(startRowIndex >= rowData.size) return

        var diffRow = 0

        for(index in startRowIndex until rowData.size) {
            checkErrorByRow(index) {
                val row =  rowData[index]
                diffRow = buildRow(row, diffRow)
            }
        }
    }

    private fun checkErrorByRow(rowIndex: Int, process: ()->Unit ) {
        try {
            process()
        } catch (e: Exception) {
            logger.error("rowIndex=$rowIndex", e)

            parser.rollbackAfterExec()

            throw Exception("ROW ERROR = $rowIndex\n ${e.message}")
        }
    }

    private fun getColumns(rowIndex: Int): List<Col> {
        val columns = ArrayList<Col>()

        for (colIndex in DATA_COLUMN until sheet.columns) {

            val cell = sheet.getCell(colIndex, rowIndex)

            val format = cell.cellFormat?.let { WritableCellFormat(it) } ?: WritableCellFormat()

            val columnContent = parseColumnContent(cell?.contents)

            if(cell is Label) {
                cell.string = ""
            }

            columns += Col(colIndex, format, columnContent)
        }
        return columns
    }

    private fun parseColumnContent(content: String?): ColumnContent {
        if(content == null || content.isBlank()) return EmptyContent

        var openVar = content.indexOf(OPEN_VAR)

        if(openVar < 0) return StringContent(content)

        val varList = ArrayList<ReturnResult>()

        var index = 0

        while(openVar >= 0) {
            if(openVar > index) {
                val text = content.substring(index until openVar)
                varList += VarResult(VarType.VARCHAR, text)
            }

            val closeVar = content.indexOf(CLOSE_VAR, openVar)
            if(closeVar < 0) throw Exception("symbol ']' not found for content:$content index:$openVar")

            val varName = content.substring(openVar..closeVar)

            val expr = parser.parseExpression(varName, vars)

            if(expr.isNotEmpty() ) {
                varList += expr[expr.lastIndex]
            }
            index = closeVar + 1
            openVar = content.indexOf(OPEN_VAR, index)
        }

        if(index < content.length) {
            val text = content.substring(index)
            varList += VarResult(VarType.VARCHAR, text)
        }

        return if(varList.size > 1) {
            ComplexContent(varList)
        } else {
            VarContent(varList[0])
        }
    }

    private fun getExpression(rowIndex: Int): Expression {
        val cellContentTag = sheet.getCell(FORMULA_COLUMN, rowIndex)

        val content = cellContentTag.contents?.trim() ?: ""

        if(cellContentTag is Label) {
            cellContentTag.string = ""
        }

        return parser.parseExpression(content, vars)
    }

    private fun getTagRow(rowIndex: Int): Tag {
        val cellContentTag = sheet.getCell(TAG_COLUMN, rowIndex)

        val content = cellContentTag.contents?.trim() ?: ""

        if(cellContentTag is Label) {
            cellContentTag.string = ""
        }

        return getTagByName(content, rowIndex)
    }

    private fun buildRow(row: Row, diffRow: Int): Int {
        return  when(row.tag) {
            EmptyTag -> buildEmpty(row, diffRow)
            is LoopTag -> buildLoop(row.tag, row, diffRow)
            is IfTag -> buildIf(row.tag, row, diffRow)
            is ParamTag -> buildEmpty(row, diffRow)
        }
    }

    private fun buildLoop(loopTag: LoopTag, row: Row, diffRow: Int): Int {

        if(loopTag.cursor.isEmpty()) return removeRowIf(row, diffRow)

        var rowIndex = row.index + diffRow

        parser.execExpression(row.expr, false)
        do {
            buildDefaultRow(row, rowIndex)

            val isNext = loopTag.cursor.isNext()
            if(isNext) {
                sheet.newRowFromSource(rowIndex)
                rowIndex++

                parser.execExpression(row.expr, false)
            }
        } while( isNext )

        return rowIndex - row.index
    }

    private fun buildIf(ifTag: IfTag, row: Row, diffRow: Int): Int {

        val isExec = parser.execExpression(ifTag.exprIf, false).toBoolean()

        if(!isExec) return removeRowIf(row, diffRow)

        parser.execExpression(row.expr, false)

        return buildEmpty(row, diffRow)
    }

    private fun removeRowIf(row: Row, diffRow: Int): Int {
        sheet.removeRow(row.index + diffRow)

        return diffRow - 1
    }

    private fun buildEmpty(row: Row, diffRow: Int): Int {

        parser.execExpression(row.expr, false)

        buildDefaultRow(row, row.index + diffRow)

        return diffRow
    }

    private fun buildDefaultRow(row: Row, rowIndex: Int) {
        for (column in row.columns) {

            val writeCell = column.contentValue(rowIndex)

            sheet.addCell(writeCell)
        }
    }

    private fun getTagByName(name: String, rowIndex: Int): Tag {
        val tagName = name.substringBefore(' ').trim().toUpperCase()

        if(tagName.isBlank() || tagName == EmptyTag.nameTag) return EmptyTag

        return when(tagName) {
            LOOP -> LoopTag(findCursor(name) )
            IF -> IfTag(parseExpr(name))
            PARAM -> ParamTag( fillParams(rowIndex) )
            else -> throw Exception("TAG not found $name")
        }
    }

    private fun fillParams(rowIndex: Int): List<Param> {
        val cellContentTag = sheet.getCell(PARAM_COLUMN, rowIndex)

        val content = cellContentTag.contents?.trim() ?: ""

        if(cellContentTag is Label) {
            cellContentTag.string = ""
        }

        if(content.isBlank()) return emptyList()

        return parseParams(content, vars)
    }

    private fun parseExpr(name: String): Expression {
        val expression = name.substringAfter(' ').trim()
        return parser.parseExpression(expression, vars)
    }

    private fun findCursor(name: String): CursorData {
        val cursorName = name.substringAfter(' ').trim().toUpperCase()

        val cursor = vars.firstOrNull { it.name == cursorName } ?: throw Exception("for tag LOOP cursor not found: $cursorName")

        if(cursor.result.type != VarType.CURSOR) throw Exception("LOOP var is not cursor: $cursorName")

        return cursor.result.value as CursorData
    }
}

private const val DATA_COLUMN = 3

private const val TAG_COLUMN = 1

private const val PARAM_COLUMN = 2

private const val FORMULA_COLUMN = 0

data class Row(val tag: Tag,
               val index: Int,
               val expr: Expression,
               val columns: List<Col> = emptyList())

data class Col(val index: Int,
               val format: WritableCellFormat,
               val value: ColumnContent) : ColumnValue {

    override fun contentValue(rowIndex: Int): WritableCell {
        return when(value) {
            EmptyContent -> Blank(index, rowIndex, format)
            is StringContent -> Label(index, rowIndex, value.content, format)
            is NumberContent -> Number(index, rowIndex, value.number, format)
            is VarContent -> varByType(value.varResult, rowIndex)
            is ComplexContent -> complexType(value.varList, rowIndex)
        }
    }

    private fun complexType(varList: List<ReturnResult>, rowIndex: Int): WritableCell {
        if(varList.isEmpty()) return Blank(index, rowIndex, format)

        val text = varList.joinToString(separator = "") {it.getVar().value?.toString()?:"" }

        return Label(index, rowIndex, text, format)
    }

    private fun varByType(varResult: ReturnResult, rowIndex: Int): WritableCell {
        if(varResult.getVar().value == null) return Blank(index, rowIndex, format)

        return when(varResult.getVar().type) {
            VarType.UNDEFINED -> Blank(index, rowIndex, format)
            VarType.INT,
            VarType.NUMBER -> Number(index, rowIndex, (varResult.getVar().value as kotlin.Number).toDouble() , format)
            VarType.VARCHAR -> Label(index, rowIndex, (varResult.getVar().value).toString(), format)
            VarType.DATE -> Label(index, rowIndex, byFormatDate(varResult.getVar().value as Date), format)
            else -> Blank(index, rowIndex, format)
        }
    }

    private fun byFormatDate(date: Date): String {
        Int

        val localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

        val pattern = if(localDateTime.hour == 0 && localDateTime.minute == 0) PATTERN_DATE else PATTERN_DATE_TIME

        return pattern.format(localDateTime)
    }
}

private val PATTERN_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

private val PATTERN_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy")

sealed class Tag(val nameTag: String)

object EmptyTag : Tag(EMPTY)

data class LoopTag(val cursor: CursorData) : Tag(LOOP)

data class IfTag(val exprIf: Expression) : Tag(IF)

data class ParamTag(val params: List<Param>) : Tag(PARAM)

private const val EMPTY = "EMPTY"

private const val LOOP = "LOOP"

private const val IF = "IF"

private const val PARAM = "PARAM"

interface ColumnValue {
    fun contentValue(rowIndex: Int): WritableCell
}

sealed class ColumnContent

object EmptyContent : ColumnContent()

data class StringContent(val content: String) : ColumnContent()

data class NumberContent(val number: Double) : ColumnContent()

data class VarContent(val varResult: ReturnResult) : ColumnContent()

data class ComplexContent(val varList: List<ReturnResult>) : ColumnContent()

/**
 * вставляет новую строку - строка вставляется вверх, а не вниз,
 * поэтому с нижней строки (там были осходные данные копируем их наверх
 * если нужно потом в нижней строке зачищаем данные
 */
fun WritableSheet.newRowFromSource(srcRowIndex: Int, isClearCopyData: Boolean = false) {

    this.insertRow(srcRowIndex)

    val newSourceIndex = srcRowIndex + 1

    for (col in 1 until columns) {
        val readCell = getWritableCell(col, newSourceIndex)

        val newCell = readCell.copyTo(col, srcRowIndex)

        readCell.cellFormat?.let {

            newCell.cellFormat = WritableCellFormat(it)
        }

        if(isClearCopyData) {
            if(readCell is Label) {
                readCell.string = ""
            }
        }

        this.addCell(newCell)
    }
}