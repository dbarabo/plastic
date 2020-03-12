package ru.barabo.report.service

import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.report.entity.Report
import ru.barabo.report.entity.defaultTemplateDirectory
import ru.barabo.xls.ExcelSql
import java.io.File
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

object ReportService : StoreFilterService<Report>(AfinaOrm, Report::class.java), ParamsSelect {

    private var directoryId: Long? = null

    override fun selectParams(): Array<Any?>? = arrayOf(directoryId)

    fun reportsByDirectory(directoryId: Long?): List<Report> {
        this.directoryId = directoryId

        initData()

        return dataList.toList()
    }

    fun createNewReport(reportName: String, template: File) {

        val directory = DirectoryService.selectedDirectory?.directory

        val newReport = Report(directory = directory?.id, name = reportName, fileName = template.name,
            creator = AfinaQuery.getUserDepartment().userName!!,
            updater = AfinaQuery.getUserDepartment().userName!!,
            templateFile = template, owner = directory)

        val report = save(newReport)

        report.templateFile = template

        report.uploadFile()
    }

    fun updateReport(report: Report, reportName: String, template: File) {

        report.name = reportName
        report.updater = AfinaQuery.getUserDepartment().userName!!
        report.updated = Timestamp(Date().time)
        report.templateFile = template

        val saveReport = save(report)
        saveReport.templateFile = template
        report.uploadFile()
    }

    fun prepareRun(report: Report): ExcelSql{

        val template = report.getTemplate()

        HistoryRunService.historyByReport(report)

        return ExcelSql(template, AfinaQuery, ::generateNewFile)
    }

    fun compileReport(report: Report) {
        val template = report.getTemplate()

        val tempFile = File("${defaultTemplateDirectory()}/3478${template.name}")
        if(tempFile.exists()) tempFile.delete()

        val excelSql = ExcelSql(template, AfinaQuery) {
            tempFile
        }
        excelSql.initRowData( ArrayList() )

        tempFile.delete()
    }

    private fun generateNewFile(template: File): File {

        val report = HistoryRunService.selectedReport ?: throw Exception("selected report is not found")

        val historyRunNew = HistoryRunService.createHistoryRun(report)

        return File(historyRunNew.fileName)
    }
}

