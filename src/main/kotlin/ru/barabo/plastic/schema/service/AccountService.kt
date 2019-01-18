package ru.barabo.plastic.schema.service

import ru.barabo.db.service.StoreService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.Account

object AccountService : StoreService<Account, List<Account>>(AfinaOrm, Account::class.java) {

    override fun elemRoot(): List<Account> = dataList
}