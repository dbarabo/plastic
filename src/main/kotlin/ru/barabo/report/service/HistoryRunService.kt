package ru.barabo.report.service

import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.report.entity.HistoryRun
import ru.barabo.report.entity.Report
import ru.barabo.report.entity.defaultDirectory
import java.io.File
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.util.*

object HistoryRunService : StoreFilterService<HistoryRun>(AfinaOrm, HistoryRun::class.java), ParamsSelect {

    var selectedReport: Report? = null
    private set

    override fun selectParams(): Array<Any?>? = arrayOf(selectedReport?.id)

    fun historyByReport(report: Report): List<HistoryRun> {
        selectedReport = report

        initData()

        return dataList.toList()
    }

    fun createHistoryRun(report: Report): HistoryRun {
        val now = Timestamp(Date().time)

        val fullFileName = fileByTemplate(report.templateFile!!, now)

        val historyRunNew = HistoryRun(report = report.id, versionId = report.versionId,
            runner = AfinaQuery.getUserDepartment().userName?:"", runned = now, fileName = fullFileName)

        save(historyRunNew)

        return historyRunNew
    }
}

private fun fileByTemplate(template: File, time: Timestamp)=
    "${defaultReportDirectory()}/${template.name}-${time.formatFile()}.xls"

private fun Timestamp.formatFile(): String = DateTimeFormatter.ofPattern ("MM-dd-HH%mm%ss").format(this.toLocalDateTime())

private fun defaultReportDirectory(): File = defaultDirectory("xls")