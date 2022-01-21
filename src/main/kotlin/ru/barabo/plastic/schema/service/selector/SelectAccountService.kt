package ru.barabo.plastic.schema.service.selector

import ru.barabo.afina.AfinaOrm
import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.schema.entity.account.AccountValue
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.service.account.AccountValueService

object SelectAccountService : StoreFilterService<SelectAccount>(AfinaOrm, SelectAccount::class.java),
    StoreListener<List<AccountValue>> {

    init {
        AccountValueService.addListener(this)
    }

    override fun refreshAll(elemRoot: List<AccountValue>, refreshType: EditType) {
        clearAllData()
    }

    private fun clearAllData() {
        synchronized(dataList) {
            dataList.clear()
        }
    }
}