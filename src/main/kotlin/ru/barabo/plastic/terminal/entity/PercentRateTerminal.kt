package ru.barabo.plastic.terminal.entity

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ColumnType
import ru.barabo.db.annotation.SelectQuery

@SelectQuery("""
select r.CLASSIFIED, r.LABEL
from od.computerate r
where upper(r.label) like 'ЭКВАЙРИНГ %'
  and r.boundcurrency = 1000001275
order by r.label desc
""")
data class PercentRateTerminal(
    @ColumnName("CLASSIFIED")
    @ColumnType(java.sql.Types.BIGINT)
    var id: Long? = null,

    @ColumnName("LABEL")
    @ColumnType(java.sql.Types.VARCHAR)
    var name: String = ""
) {
    override fun toString(): String = name
}