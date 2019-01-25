package ru.barabo.plastic.schema.entity.schema

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.service.schema.HeaderTransactTypeService

@SelectQuery("""
    select t.id, t.NAME, t.HEADER_NAME, t.TRANS_TYPE
      from od.ptkb_plastic_transact_type t where t.HEADER_NAME = ? order by t.id
""")
data class TransType(
    @ColumnName("id")
    var id: Long? = null,

    @ColumnName("NAME")
    var name: String? = null,

    @ColumnName("HEADER_NAME")
    var headerName: String? = null,

    @ColumnName("TRANS_TYPE")
    var transactType: String? = null
) : ParamsSelect {
    override fun selectParams(): Array<Any?>? = arrayOf(HeaderTransactTypeService.selectedEntity()?.headerName?: String::class.java)
}