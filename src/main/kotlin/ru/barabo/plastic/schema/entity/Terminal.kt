package ru.barabo.plastic.schema.entity

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

 select c.terminal_id, min(c.merchant_name) label, 0 is_pos
  from od.ptkb_transact_ctl_mtl c
 where c.terminal_id not in (select p.terminalid from ptkb_poses p)
   and (c.merchant_id like 'PTC%' or c.merchant_id like '0226%')
group by c.terminal_id

 order by 2
""")
data class Terminal(
    @ColumnName("terminalid")
    var terminalId: String? = null,

    @ColumnName("label")
    var name: String? = null,

    @ColumnName("is_pos")
    @Converter(BooleanConverter::class)
    var isPos: Boolean = false)
