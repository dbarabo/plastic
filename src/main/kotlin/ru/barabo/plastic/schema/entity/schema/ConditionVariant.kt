package ru.barabo.plastic.schema.entity.schema

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.service.schema.TransTypeService

@SelectQuery("""
    select s.TRANSACT_TYPE, s.CONDITION, s.CONDITION_VARIANT, s.CONDITION_VARIANT OLD_VARIANT
      from od.ptkb_transact_schema s
     where s.TRANSACT_TYPE = ?
     group by s.TRANSACT_TYPE, s.CONDITION, s.CONDITION_VARIANT
     order by min(s.id)
""")
data class ConditionVariant (
    @ColumnName("TRANSACT_TYPE")
    var transactType: String? = null,

    @ColumnName("CONDITION")
    var condition: Long? = null,

    @ColumnName("CONDITION_VARIANT")
    var conditionVariant: String? = null,

    @ColumnName("OLD_VARIANT")
    var oldVariant: String? = null

) : ParamsSelect {

    override fun selectParams(): Array<Any?>? = arrayOf(TransTypeService.selectedEntity()?.transactType ?: String::class.java)
}