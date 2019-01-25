package ru.barabo.plastic.schema.entity.selector

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.Filtered
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.annotation.SelectQuery

const val DOCTYPE_JURIC = 1000133055L

const val DOCTYPE_PBOUL = 1002276339L

const val DOCTYPE_BANK = 1000000441L

@SelectQuery("""
select c.classified, c.label, c.description, inn_bik.code, c.doctype, c.doctype doctype2
from od.client c,
     od.doctree dt,
     od.BankCode inn_bik
where c.classified = dt.classified
  and dt.docstate = 1000000039
  and inn_bik.client = c.classified
  and inn_bik.validtodate is null
  and ( (c.doctype in ($DOCTYPE_JURIC, $DOCTYPE_PBOUL)
         and inn_bik.codesystem = 1000043388

         and exists(select 1
            from od.account a
            where a.client = c.classified
              and a.closed > sysdate)
        )
        or
        (c.doctype = $DOCTYPE_BANK and inn_bik.codesystem = 1000004994)
      )

  and c.classified = coalesce(?, c.classified)
  and upper(c.label) like upper(coalesce(?, c.label))

  and upper(c.description) like upper(coalesce(?, c.description))

  and inn_bik.code like coalesce(?, inn_bik.code)

  and (c.doctype in (?, ?) )
""")
data class SelectClient(
    @ColumnName("classified")
    @Filtered(0, 0)
    var id: Long? = null,

    @ColumnName("label")
    @Filtered(1, 1)
    var label: String? = null,

    @ColumnName("description")
    @Filtered(2, 2)
    var description: String? = null,

    @ColumnName("code")
    @Filtered(3, 3)
    var innOrBik: String? = null,

    @ColumnName("doctype")
    @Filtered(4, 4)
    var doctype: Long? = DOCTYPE_JURIC,

    @ColumnName("doctype2")
    @Filtered(5, 5)
    var doctype2: Long? = DOCTYPE_PBOUL
): ParamsSelect {

    companion object {
        val filter = SqlFilterEntity(SelectClient(innOrBik = "254%"))
    }

    override fun selectParams(): Array<Any?>? =  filter.getSqlParams()
}

