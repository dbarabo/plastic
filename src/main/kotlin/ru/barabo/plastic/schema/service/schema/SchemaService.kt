package ru.barabo.plastic.schema.service.schema

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.schema.entity.schema.ConditionVariant
import ru.barabo.plastic.schema.entity.schema.Schema
import ru.barabo.plastic.schema.entity.variable.Variable

object SchemaService : StoreFilterService<Schema>(AfinaOrm, Schema::class.java), StoreListener<List<ConditionVariant>> {

    init {
        ConditionVariantService.addListener(this)
    }

    override fun refreshAll(elemRoot: List<ConditionVariant>, refreshType: EditType) {
        if(refreshType in listOf(EditType.INIT, EditType.CHANGE_CURSOR, EditType.ALL)) {
            initData()
        }
    }

    fun createDefaultSchema() = Schema(
        debetAccount = null,
        creditAccount = null,
        transType = TransTypeService.selectedEntity()?.transactType,
        condition = ConditionVariantService.selectedEntity()?.condition,
        conditionVariant = ConditionVariantService.selectedEntity()?.conditionVariant,
        rowOrder = dataList.size + 1)

    fun addCondition(condition: Variable?, variant: String?) {
        val condVariant = checkConditions(condition, variant)

        addCondition(condVariant)
    }

    private fun checkConditions(condition: Variable?, variant: String?): CondVariant {
        if(variant.isNullOrEmpty()) throw Exception("при заданном условии вариант значения не может быть пустым")

        if(condition?.id == null && selectedEntity()?.condition == null) throw Exception("Не задано ключево условие")

        if(selectedEntity()?.transType == null) throw Exception("Сначала создайте схему для типа транзакции, а потом можно будет создать условие для схемы")

        return CondVariant(condition?.id ?: selectedEntity()?.condition!!, variant)
    }

    private fun addCondition(condVariant: CondVariant) {

        val params = arrayOf<Any?>(condVariant.conditionId, condVariant.variant, selectedEntity()?.transType)

        AfinaQuery.execute(EXEC_ADD_CONDITION, params)

        TransTypeService.reselectRow()
    }

    private const val EXEC_ADD_CONDITION = """
        update OD.PTKB_TRANSACT_SCHEMA
           set CONDITION = ?,
               CONDITION_VARIANT = ?
         where TRANSACT_TYPE = ?
    """
}

private data class CondVariant(val conditionId: Long, val variant: String)