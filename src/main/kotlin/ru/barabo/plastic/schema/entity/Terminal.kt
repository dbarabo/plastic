package ru.barabo.plastic.schema.entity

import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.service.AccountValueService

@SelectQuery("""
select p.terminalid, cl.label
  from od.ptkb_poses p
     , client cl
where cl.classified = p.client
 and coalesce(p.validto, sysdate + 1) > sysdate
 order by 2
""")
data class Terminal(val terminalId: String, val name: String?) {
    companion object {
        var selectedTerminal: Terminal? = null
            set(value) {

                val oldValue = field

                field = value

                if(oldValue !== value) {
                    AccountValueService.initData()
                }
            }
    }
}

@SelectQuery("""
select c.terminal_id, min(c.merchant_name)
  from od.ptkb_transact_ctl_mtl c
 where c.terminal_id not in (select p.terminalid from ptkb_poses p)
   and c.merchant_id like 'PTC%' or c.merchant_id like '0226%'
group by c.terminal_id
order by 2
""")
data class Bankomat(val terminalId: String, val name: String?) {
    companion object {
        var selectedBankomat: Bankomat? = null
            set(value) {

                val oldValue = field

                field = value

                if(oldValue !== value) {
                    AccountValueService.initData()
                }
            }
    }
}
