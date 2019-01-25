package ru.barabo.plastic.schema.entity.schema

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.SelectQuery

@SelectQuery("select min(id) id, header_name from od.ptkb_plastic_transact_type group by header_name order by 2 desc")
data class HeaderTransactType(
    @ColumnName("id")
    var id: Long? = null,

    @ColumnName("header_name")
    var headerName: String? = null)