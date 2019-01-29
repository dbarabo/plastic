package ru.barabo.plastic.schema.service.schema

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.schema.entity.account.Account
import ru.barabo.plastic.schema.entity.schema.ConditionVariant
import ru.barabo.plastic.schema.entity.schema.Schema
import ru.barabo.plastic.schema.entity.variable.Variable
import java.lang.Exception

object SchemaService : StoreFilterService<Schema>(AfinaOrm, Schema::class.java), StoreListener<List<ConditionVariant>> {

    init {
        ConditionVariantService.addListener(this)
    }

    override fun refreshAll(elemRoot: List<ConditionVariant>, refreshType: EditType) {
        if(refreshType in listOf(EditType.INIT, EditType.CHANGE_CURSOR, EditType.ALL)) {
            initData()
        }
    }

    fun createByDebetCredit(debet: Account?, credit: Account?) {
        val deb = debet ?: throw Exception("Счет дебета не может быть пустым")

        val cred = credit ?: throw Exception("Счет кредита не может быть пустым")

        val newSchema = Schema(debetAccount = deb.id, creditAccount = cred.id,
            transType = TransTypeService.selectedEntity()?.transactType,
            rowOrder = dataList.size + 1)

        save(newSchema)
    }

    fun addConditionVariant(condition: Variable?, variant: String?) {
        val condVariant = checkConditions(condition, variant)

        addCondition(condVariant)
    }

    fun updateConditionVariant(condition: Variable?, variant: String?) {
        val condVariant = checkConditions(condition, variant)

        updateConditionVariant(condVariant)
    }

    private fun checkConditions(condition: Variable?, variant: String?): CondVariant {
        if(variant.isNullOrEmpty()) throw Exception("при заданном условии вариант значения не может быть пустым")

        if(condition?.id == null && selectedEntity()?.condition == null) throw Exception("Не задано ключево условие")

        if(selectedEntity()?.transType == null) throw Exception("Сначала создайте схему для типа транзакции, а потом можно будет создать условие для схемы")

        return CondVariant(condition?.id ?: selectedEntity()?.condition!!, variant)
    }
    private fun updateConditionVariant(condVariant: CondVariant) {
        val oldCondition = selectedEntity()?.condition ?: return

        val params = arrayOf(condVariant.conditionId, condVariant.variant, selectedEntity()?.transType, oldCondition)

        AfinaQuery.execute(EXEC_UPDATE_CONDITION, params)

        TransTypeService.reselectRow()
    }

    private fun addCondition(condVariant: CondVariant) {

        val params = arrayOf(condVariant.conditionId, condVariant.variant, selectedEntity()?.transType)

        AfinaQuery.execute(EXEC_ADD_CONDITION, params)

        TransTypeService.reselectRow()
    }

    private const val EXEC_ADD_CONDITION = """
        update OD.PTKB_TRANSACT_SCHEMA
           set CONDITION = ?,
               CONDITION_VARIANT = ?
         where TRANSACT_TYPE = ?
    """

    private const val EXEC_UPDATE_CONDITION = """
        update OD.PTKB_TRANSACT_SCHEMA
           set CONDITION = ?,
               CONDITION_VARIANT = ?
         where TRANSACT_TYPE = ?
           and CONDITION = ?
    """
}

private data class CondVariant(val conditionId: Long, val variant: String)