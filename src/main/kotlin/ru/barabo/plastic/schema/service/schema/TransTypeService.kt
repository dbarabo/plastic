package ru.barabo.plastic.schema.service.schema

import ru.barabo.afina.AfinaOrm
import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.schema.entity.schema.HeaderTransactType
import ru.barabo.plastic.schema.entity.schema.TransType

object TransTypeService : StoreFilterService<TransType>(AfinaOrm, TransType::class.java), StoreListener<List<HeaderTransactType>> {
    init {
        HeaderTransactTypeService.addListener(this)
    }

    override fun refreshAll(elemRoot: List<HeaderTransactType>, refreshType: EditType) {

        if(refreshType in listOf(EditType.INIT, EditType.CHANGE_CURSOR, EditType.ALL)) {
            initData()
        }
    }
}