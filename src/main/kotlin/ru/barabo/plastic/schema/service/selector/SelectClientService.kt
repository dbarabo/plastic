package ru.barabo.plastic.schema.service.selector

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.account.AccountValue
import ru.barabo.plastic.schema.entity.selector.SelectClient
import ru.barabo.plastic.schema.service.account.AccountValueService

object SelectClientService : StoreFilterService<SelectClient>(AfinaOrm, SelectClient::class.java),
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