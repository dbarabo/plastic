package ru.barabo.plastic.schema.service.schema

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.schema.Schema
import ru.barabo.plastic.schema.entity.schema.TransType

object SchemaService : StoreFilterService<Schema>(AfinaOrm, Schema::class.java), StoreListener<List< TransType>> {

    init {
        TransTypeService.addListener(this)
    }

    override fun refreshAll(elemRoot: List<TransType>, refreshType: EditType) {
        if(refreshType in listOf(EditType.INIT, EditType.CHANGE_CURSOR, EditType.ALL)) {
            initData()
        }
    }
}