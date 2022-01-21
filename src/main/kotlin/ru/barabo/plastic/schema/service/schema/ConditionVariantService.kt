package ru.barabo.plastic.schema.service.schema

import ru.barabo.afina.AfinaOrm
import ru.barabo.afina.AfinaQuery
import ru.barabo.db.EditType
import ru.barabo.db.SessionException
import ru.barabo.db.SessionSetting
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.schema.entity.schema.ConditionVariant
import ru.barabo.plastic.schema.entity.schema.TransType

object ConditionVariantService : StoreFilterService<ConditionVariant>(AfinaOrm, ConditionVariant::class.java),
    StoreListener<List<TransType>> {

    init {
        TransTypeService.addListener(this)
    }

    override fun refreshAll(elemRoot: List<TransType>, refreshType: EditType) {
        if(refreshType in listOf(EditType.INIT, EditType.CHANGE_CURSOR, EditType.ALL)) {
            initData()
        }
    }

    /*
    @Throws(SessionException::class)
    override fun save(item: ConditionVariant, sessionSetting: SessionSetting = SessionSetting(false)): ConditionVariant {
    */

    @Throws(SessionException::class)
    override fun save(item: ConditionVariant, sessionSetting: SessionSetting): ConditionVariant {

        if(item.condition != null && (item.conditionVariant.isNullOrEmpty() || item.conditionVariant.isNullOrEmpty()))
            throw SessionException("При заданном условии вариант значения не может быть пустым")

        val params = arrayOf<Any?>(item.conditionVariant, item.transactType, item.oldVariant)

        AfinaQuery.execute(UPDATE_VARIANT, params, sessionSetting)

        item.oldVariant = item.conditionVariant

        return item
    }

    private const val UPDATE_VARIANT = """
update od.ptkb_transact_schema
   set CONDITION_VARIANT = ?
 where TRANSACT_TYPE = ?
   and CONDITION_VARIANT = ?"""

}