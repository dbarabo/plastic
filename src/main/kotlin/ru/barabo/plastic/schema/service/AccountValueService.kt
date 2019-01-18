package ru.barabo.plastic.schema.service

import ru.barabo.db.service.StoreService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.AccountValue

object AccountValueService : StoreService<AccountValue, List<AccountValue>>(AfinaOrm, AccountValue::class.java) {

    override fun elemRoot(): List<AccountValue> = dataList
}

