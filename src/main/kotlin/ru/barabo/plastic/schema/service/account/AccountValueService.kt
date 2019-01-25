package ru.barabo.plastic.schema.service.account

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.account.AccountValue

object AccountValueService : StoreFilterService<AccountValue>(AfinaOrm, AccountValue::class.java) {

    init {
        AccountService.addListener(updateAccountValue())

        CurrencyService.addListener(updateAccountValue())

        TerminalService.addListener(updateAccountValue())

        OfficeService.addListener(updateAccountValue())

        ClientTypeService.addListener(updateAccountValue())
    }
}

inline fun <reified T> updateAccountValue(): StoreListener<List<T>> =
    object : StoreListener<List<T>> {
        override fun refreshAll(elemRoot: List<T>, refreshType: EditType) {
            AccountValueService.initData()
        }
    }
