package ru.barabo.plastic.unnamed.entity

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ColumnType
import ru.barabo.db.annotation.SelectQuery

@SelectQuery("""
select classified, label
from department
where lev = 1
and closed > sysdate
order by label
""")
data class Department(
@ColumnName("CLASSIFIED")
@ColumnType(java.sql.Types.BIGINT)
var id: Long? = null,

@ColumnName("LABEL")
@ColumnType(java.sql.Types.VARCHAR)
var name: String = ""
) {
    override fun toString(): String = name
}