package ru.barabo.plastic.schema.entity

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ColumnType
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.service.AccountValueService

@SelectQuery("""
select to_number(c.CODEISONUMB) CODE, c.CODEISOALPH
from od.currency c
where c.CODEISONUMB in ('810', '840', '978')
order by 1
""")
data class Currency(
    @ColumnName("CODE")
    @ColumnType(java.sql.Types.INTEGER)
    var code: Int? = null,

    @ColumnName("CODEISOALPH")
    @ColumnType(java.sql.Types.VARCHAR)
    var name: String? = null
)