package ru.barabo.plastic.schema.entity

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ColumnType
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.service.AccountValueService

@SelectQuery("""
  select d.classified, d.label from od.doctype d
   where d.classified in (1000133055, 1000133080, 1002276339)
order by 1
""")
data class ClientType(
@ColumnName("CLASSIFIED")
@ColumnType(java.sql.Types.BIGINT)
var id: Long? = null,

@ColumnName("LABEL")
@ColumnType(java.sql.Types.VARCHAR)
var name: String? = null)