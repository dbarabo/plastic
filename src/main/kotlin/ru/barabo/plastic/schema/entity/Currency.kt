package ru.barabo.plastic.schema.entity

import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.schema.service.AccountValueService

@SelectQuery("""
    select 810, 'RUR' from dual union
    select 840, 'USD' from dual union
    select 970, 'EUR' from dual union
""")
data class Currency(val code: Int, val name: String) {
    companion object {
        var selectedCurrency: Currency? = null
            set(value) {

                val oldValue = field

                field = value

                if(oldValue !== value) {
                    AccountValueService.initData()
                }
            }
    }
}