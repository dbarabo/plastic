package ru.barabo.plastic.schema.service

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.Account

object AccountService : StoreFilterService<Account>(AfinaOrm, Account::class.java)