package ru.barabo.plastic.report.gui

import org.jdesktop.swingx.JXHyperlink
import org.jdesktop.swingx.JXTaskPane
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.terminal.gui.TablePosTerminal
import ru.barabo.plastic.unnamed.gui.errorMessage
import ru.barabo.total.report.rtf.RtfReport
import ru.barabo.total.report.rtf.RtfReport.getDefaultToDirectory
import ru.barabo.xls.ExcelSql
import ru.barabo.xls.Var
import java.awt.BorderLayout
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JToolBar

class TabReport : JPanel()  {

    init {
        layout = BorderLayout()

        add(ReportPane, BorderLayout.WEST)

        add(JScrollPane(TablePosTerminal), BorderLayout.CENTER)
    }

    companion object {
        const val TITLE = "Отчеты"
    }
}

private object ReportPane : JToolBar(VERTICAL) {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        isFloatable = true

        add( JXTaskPane("Пластик").apply {
            alignmentX = 0.0f

            add(JXTaskPane("Эквайринг").apply {
                alignmentX = 0.0f

                add(JXHyperlink().apply {
                    text = "Выдача наличных в банкоматах"

                    addActionListener {  reportByTemplateXls("bankomat-cash-out") }
                })
            })
            add(JXTaskPane("Эмиссия") )
        })
    }
}

private fun reportByTemplateXls(templateXls: String) {

    val template = xlsFileName(templateXls)

    val newFile = newFile(templateXls)

    val vars = ArrayList<Var>()

    val excelSql = ExcelSql(newFile, template, AfinaQuery)

    excelSql.initRowData(vars)

}

private fun newFile(templateXls: String): File = File("${getDefaultToDirectory()}/$templateXls-${dateTimeNow()}.xls")

private fun dateTimeNow(): String = DateTimeFormatter.ofPattern ("yy-MM-dd-HH-mm-ss").format(LocalDateTime.now())

private fun xlsFileName(templateXls: String): File = File("$LIB_FOLDER/$templateXls.xls")

private val JAR_FOLDER: String = File(TabReport::class.java.protectionDomain.codeSource.location.path).parentFile.path

private val LIB_FOLDER: String = "$JAR_FOLDER/lib"