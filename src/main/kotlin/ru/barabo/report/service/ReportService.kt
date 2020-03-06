package ru.barabo.report.service

import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.report.entity.Report

object ReportService : StoreFilterService<Report>(AfinaOrm, Report::class.java), ParamsSelect {

    private var directoryId: Long? = null

    override fun selectParams(): Array<Any?>? = arrayOf(directoryId)

    fun reportsByDirectory(directoryId: Long?): List<Report> {
        this.directoryId = directoryId

        initData()

        return dataList.toList()
    }
}