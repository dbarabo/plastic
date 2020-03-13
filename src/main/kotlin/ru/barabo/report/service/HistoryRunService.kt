package ru.barabo.report.service

import ru.barabo.db.EditType
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.report.entity.HistoryRun
import ru.barabo.report.entity.Report
import ru.barabo.report.entity.defaultReportDirectory
import java.io.File
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.util.*

object HistoryRunService : StoreFilterService<HistoryRun>(AfinaOrm, HistoryRun::class.java),
    ParamsSelect, StoreListener<List<Report>> {

    override fun selectParams(): Array<Any?>? = arrayOf(ReportService?.selectedReport)

    init {
        ReportService.addListener(this)
    }

    override fun refreshAll(elemRoot: List<Report>, refreshType: EditType) {
        initData()
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
    "${defaultReportDirectory()}/${template.nameWithoutExtension}-${time.formatFile()}.xls"

private fun Timestamp.formatFile(): String = DateTimeFormatter.ofPattern ("MM-dd-HH%mm%ss").format(this.toLocalDateTime())

