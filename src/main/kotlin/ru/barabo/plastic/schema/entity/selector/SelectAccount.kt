package ru.barabo.plastic.schema.entity.selector

import ru.barabo.db.annotation.*
import ru.barabo.plastic.schema.service.account.AccountService
import ru.barabo.plastic.schema.service.account.CurrencyService
import ru.barabo.plastic.schema.service.account.OfficeService

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

  and a.doc = coalesce(?, a.doc)

  and a.code like coalesce(?, a.code)
  and upper(a.label) like upper(coalesce(?, a.label))

  and (? is null or upper(c.label) like upper(?) )
  and (? is null or upper(c.description) like upper(?) )

  and (binn.code is null or binn.code like coalesce(?, binn.code) )

  and (a.currency = coalesce(?, a.currency) )

  and coalesce(od.PTKB_PRECEPT.getDepartmentByLevel(dta.operobj), 1007118612) = coalesce(?, coalesce(od.PTKB_PRECEPT.getDepartmentByLevel(dta.operobj), 1007118612) )
""")
data class SelectAccount(
    @ColumnName("DOC")
    @Filtered(0, 0)
    var id: Long? = null,

    @ColumnName("CODE")
    @Filtered(1, 1)
    var code: String? = null,

    @ColumnName("LABEL")
    @Filtered(2, 2)
    var name: String? = null,

    @ColumnName("DESCRIPTION")
    var description: String? = null,

    @ColumnName("OPENED")
    @ColumnType(java.sql.Types.DATE)
    var opened: String? = null,

    @ColumnName("CLIENT")
    var clientId: Long? = null,

    @ColumnName("CLIENT_NAME")
    @Filtered(3, 4)
    var clientName: String? = null,

    @ColumnName("CLIENT_DESC")
    @Filtered(5, 6)
    var clientDescription: String? = null,

    @ColumnName("CLIENT_INN")
    @Filtered(7, 7)
    var clientInn: String? = null) : ParamsSelect {

    companion object {
        val filter = SqlFilterEntity(SelectAccount(code="30232%") )
    }

    override fun selectParams(): Array<Any?>? {
        val list = filter.getSqlParams().toMutableList()

        val account = AccountService.selectedEntity()

        val currency = if(account?.isCheckCurrency == true) CurrencyService.selectedEntity()?.id
            ?: Long::class.javaObjectType else Long::class.javaObjectType

        val office = if(account?.isCheckOffice == true) OfficeService.selectedEntity()?.id
            ?: Long::class.javaObjectType else Long::class.javaObjectType

        list.add(currency)
        list.add(office)

        return list.toTypedArray()
    }
}