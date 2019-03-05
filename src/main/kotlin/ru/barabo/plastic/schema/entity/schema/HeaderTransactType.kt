package ru.barabo.plastic.schema.entity.schema

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.Filtered
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.entity.selector.SqlFilterEntity
import ru.barabo.plastic.schema.service.schema.HeaderTransactTypeService

@SelectQuery("""
    select min(id) id, header_name
    from od.ptkb_plastic_transact_type
    where TRANS_TYPE like coalesce(upper(?), TRANS_TYPE)
    group by header_name
    order by 2 desc
""")
data class HeaderTransactType(
    @ColumnName("id")
    var id: Long? = null,

    @ColumnName("header_name")
    var headerName: String? = null,

    @ColumnName("TRANS_TYPE")
    @Filtered(0, 0)
    var transactType: String = ""
    ) : ParamsSelect {

    companion object {
        val filter = SqlFilterEntity( HeaderTransactType() )
    }

    override fun selectParams(): Array<Any?>? = filter.getSqlParams()
}