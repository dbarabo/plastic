package ru.barabo.plastic.schema.entity.variable

import ru.barabo.db.annotation.*
import ru.barabo.db.converter.EnumConverter
import ru.barabo.plastic.schema.entity.account.SEQ_CLASSIFIED

@TableName("od.ptkb_transact_variable")
@SelectQuery("""
select v.ID, v.TYPE_VAR, v.NAME, v.CALC_FUNC
  from od.ptkb_transact_variable v
order by v.type_var, v.name
""")
data class Variable(
    @SequenceName(SEQ_CLASSIFIED)
    @ColumnName("ID")
    @ColumnType(java.sql.Types.BIGINT)
    var id: Long? = null,

    @ColumnName("TYPE_VAR")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(EnumConverter::class)
    var typeVar: VariableType = VariableType.PRIOR_CONDITION,

    @ColumnName("NAME")
    @ColumnType(java.sql.Types.VARCHAR)
    var name: String? = null,

    @ColumnName("CALC_FUNC")
    @ColumnType(java.sql.Types.VARCHAR)
    var calcFunc: String? = null
) {
    override fun toString(): String = name?:""
}