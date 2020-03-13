package ru.barabo.report.service

import ru.barabo.db.EditType
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.report.entity.Remark
import ru.barabo.report.entity.Report

object RemarkService : StoreFilterService<Remark>(AfinaOrm, Remark::class.java),
    ParamsSelect, StoreListener<List<Report>> {

    override fun selectParams(): Array<Any?>? = arrayOf(ReportService?.selectedReport?.id ?: Long::class.javaObjectType)

    override fun refreshAll(elemRoot: List<Report>, refreshType: EditType) {
        initData()
    }
}