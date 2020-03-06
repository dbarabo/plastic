package ru.barabo.plastic.report.gui

import org.jdesktop.swingx.JXHyperlink
import org.jdesktop.swingx.JXTaskPane
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.total.report.rtf.RtfReport.getDefaultToDirectory
import ru.barabo.xls.ExcelSql
import ru.barabo.xls.Var
import java.awt.BorderLayout
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.*

class TabReport : JPanel()  {

    init {
        layout = BorderLayout()

        val paramPanel = JPanel()

        val resultPanel = JPanel()

        val paramResultVertSplit = JSplitPane(JSplitPane.VERTICAL_SPLIT, paramPanel, resultPanel).apply {
            resizeWeight = 0.4
        }

        val historyPanel = JPanel()

        val mainHorizontalSplit = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paramResultVertSplit, historyPanel).apply {
            resizeWeight = 0.7
        }

        add(ReportPane(paramPanel), BorderLayout.WEST)

        add(mainHorizontalSplit, BorderLayout.CENTER)
    }

    companion object {
        const val TITLE = "Отчеты"
    }
}

private class ReportPane(paramPanel: JPanel) : JToolBar(VERTICAL) {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        isFloatable = true

        add( JXTaskPane("Пластик").apply {

            add(JXTaskPane("Эквайринг").apply {

                add(JXHyperlink().apply {
                    text = "Выдача наличных в банкоматах"

                    addActionListener {  reportByTemplateXls("bankomat-cash-out", paramPanel) }
                })
            })
            add(JXTaskPane("Эмиссия") )
        })
    }
}

private fun reportByTemplateXls(templateXls: String, paramPanel: JPanel) {

    val template = xlsFileName(templateXls)

    val newFile = newFile(templateXls)

    val vars = ArrayList<Var>()

    val excelSql = ExcelSql(newFile, template, AfinaQuery)

    processShowError {
        excelSql.initRowData(vars)

        excelSql.requestParam(paramPanel) { file -> processShowError { Desktop.getDesktop().open(file) } }
    }
}

private fun newFile(templateXls: String): File = File("${getDefaultToDirectory()}/$templateXls-${dateTimeNow()}.xls")

private fun dateTimeNow(): String = DateTimeFormatter.ofPattern ("MM-dd-HH%mm%ss").format(LocalDateTime.now())

private fun xlsFileName(templateXls: String): File = File("$LIB_FOLDER/$templateXls.xls")

private val JAR_FOLDER: String = File(TabReport::class.java.protectionDomain.codeSource.location.path).parentFile.path

private val LIB_FOLDER: String = "$JAR_FOLDER/lib"