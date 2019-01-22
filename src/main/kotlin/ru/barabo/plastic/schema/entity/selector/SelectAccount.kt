package ru.barabo.plastic.schema.entity.selector

import ru.barabo.db.annotation.*

@SelectQuery("""
select a.doc, a.code, a.label, a.description, a.opened, a.client, c.label CLIENT_NAME,
      c.description CLIENT_DESC, binn.code CLIENT_INN
from od.account a,
     od.client c,
     od.doctree dta,
     od.BankCode binn
where a.sysfilial = 1
  and a.doc = dta.classified
  and dta.docstate = 1000000039
  and a.foldaccount is null
  and a.closed > sysdate
  and a.client = c.classified(+)
  and (coalesce(c.doctype, 1000133055) in (1000133055, 1002276339)
       or a.client in (15184, 12658)
      )

  and a.client = binn.client(+)
  and binn.CodeSystem(+) = 1000043388
  and binn.validtodate(+) is null

  and a.code like coalesce(?, a.code)
  and upper(a.label) like upper(coalesce(?, a.label))
  and upper(a.description) like upper(coalesce(?, a.description))

  and (? is null or a.client = ?)

  and (? is null or upper(c.label) like ?)
  and (? is null or upper(c.description) like ?)
""")
data class SelectAccount(
    @ColumnName("DOC")
    var id: Long? = null,

    @ColumnName("CODE")
    @Filtered(0, 0)
    var code: String? = null,

    @ColumnName("LABEL")
    @Filtered(1, 1)
    var name: String? = null,

    @ColumnName("DESCRIPTION")
    @Filtered(2, 2)
    var description: String? = null,

    @ColumnName("OPENED")
    @ColumnType(java.sql.Types.DATE)
    var opened: String? = null,

    @ColumnName("CLIENT")
    @Filtered(3, 4)
    var clientId: Long? = null,

    @ColumnName("CLIENT_NAME")
    @Filtered(5, 6)
    var clientName: String? = null,

    @ColumnName("CLIENT_DESC")
    @Filtered(7, 8)
    var clientDescription: String? = null,

    @ColumnName("CLIENT_INN")
    var clientInn: String? = null) : ParamsSelect {

    companion object {
        val filter = SqlFilterEntity(SelectAccount(code="30232%") )
    }

    override fun selectParams(): Array<Any?>? = filter.getSqlParams()
}