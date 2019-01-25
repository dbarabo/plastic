package ru.barabo.plastic.schema.entity.account

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.Converter
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.db.converter.BooleanConverter

@SelectQuery("""
select p.terminalid, cl.label, 1 is_pos
  from od.ptkb_poses p
     , client cl
where cl.classified = p.client
 and coalesce(p.validto, sysdate + 1) > sysdate

 union

 select d.code terminalid, d.label, 0 is_pos
from od.department d
where d.closed > sysdate
  and d.category = 14

 order by 3, 2
""")
data class Terminal(
    @ColumnName("terminalid")
    var terminalId: String? = null,

    @ColumnName("label")
    var name: String? = null,

    @ColumnName("is_pos")
    @Converter(BooleanConverter::class)
    var isPos: Boolean = false)
