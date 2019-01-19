package ru.barabo.plastic.schema.entity

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ColumnType
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.service.AccountValueService

@SelectQuery("""
  select d.classified, d.label from od.department d
    where d.level = 1
""")
data class Office(
    @ColumnName("CLASSIFIED")
    @ColumnType(java.sql.Types.BIGINT)
    var id: Long? = null,

    @ColumnName("LABEL")
    @ColumnType(java.sql.Types.VARCHAR)
    var name: String? = null) {

    companion object {
        var selectedOffice: Office? = null
            set(value) {

                val oldValue = field

                field = value

                if(oldValue !== value) {
                    AccountValueService.initData()
                }
            }
    }
}