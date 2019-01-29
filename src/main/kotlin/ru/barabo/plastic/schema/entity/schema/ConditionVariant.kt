package ru.barabo.plastic.schema.entity.schema

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.service.schema.TransTypeService

@SelectQuery("""
    select distinct s.TRANSACT_TYPE, s.CONDITION, s.CONDITION_VARIANT
      from od.ptkb_transact_schema s
     where s.condition = ?
     order by 2
""")
data class ConditionVariant (
    @ColumnName("TRANSACT_TYPE")
    var transactType: String? = null,

    @ColumnName("CONDITION")
    var condition: Long? = null,

    @ColumnName("CONDITION_VARIANT")
    var conditionVariant: String? = null
) : ParamsSelect {

    override fun selectParams(): Array<Any?>? = arrayOf(TransTypeService.selectedEntity()?.condition ?: String::class.java)
}