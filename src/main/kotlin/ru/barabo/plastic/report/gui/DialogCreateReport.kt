package ru.barabo.plastic.report.gui

import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import ru.barabo.plastic.terminal.gui.buttonHorisontal
import ru.barabo.report.entity.Report
import ru.barabo.report.service.DirectoryService
import ru.barabo.report.service.ReportService
import java.awt.Component
import java.io.File
import java.lang.Exception
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JTextField
import javax.swing.filechooser.FileNameExtensionFilter

class DialogCreateReport(private val report: Report?, component: Component) : AbstractDialog(component, "Создать отчет") {

    private var nameReport: JTextField

    private var selectedXls: JButton

    private var selectedFile: File? = null

    init {
        textFieldHorizontal("Папка-владелец", 0).apply {
            isEditable = false
            text = report?.owner?.name ?: DirectoryService.selectedDirectory?.directory?.name
        }

        textFieldHorizontal("Название отчета", 1).apply {
            nameReport = this

            text = report?.name
        }

        buttonHorisontal("xls-шаблон", "...", 2, ::selectXlsTemplateFile).apply {
            selectedXls = this

            text =  if(report?.fileName.isNullOrBlank() ) "..." else report?.fileName
        }

        createOkCancelButton(3, 1)

        packWithLocation()
    }

    override fun okProcess() {

        if(selectedFile == null || nameReport.text.isNullOrBlank()) throw Exception("Название отчета и xls-файл шаблона должны быть заполнены")

        if(report?.id == null) {
            ReportService.createNewReport(nameReport.text, selectedFile!!)
        } else {

            ReportService.updateReport(report, nameReport.text, selectedFile!!)
        }
    }

    private fun selectXlsTemplateFile() {

        val fileChooser = JFileChooser().apply {
            isMultiSelectionEnabled = false

            fileFilter = FileNameExtensionFilter("файл шаблона (.xls)", "xls")

            fileSelectionMode = JFileChooser.FILES_ONLY
        }

        val selected = (if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            fileChooser.selectedFile else null) ?: return

        selectedFile = selected
        selectedXls.text = "...${selectedFile?.name}"
    }
}