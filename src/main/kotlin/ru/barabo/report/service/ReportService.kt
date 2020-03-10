package ru.barabo.report.service

import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.report.entity.Report
import ru.barabo.xls.ExcelSql
import ru.barabo.xls.Var
import java.io.File

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

    fun prepareRun(report: Report): ExcelSql{

        HistoryRunService.historyByReport(report)

        val template = report.getTemplate()

        val vars = ArrayList<Var>()

        val excelSql = ExcelSql(template, AfinaQuery, ::generateNewFile)

        excelSql.initRowData(vars)

        return excelSql
    }

    fun generateNewFile(template: File): File {

        val report = HistoryRunService.selectedReport ?: throw Exception("selected report is not found")

        val historyRunNew = HistoryRunService.createHistoryRun(report)

        return File(historyRunNew.fileName)
    }
}

