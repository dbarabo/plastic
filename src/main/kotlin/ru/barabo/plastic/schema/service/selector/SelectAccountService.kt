package ru.barabo.plastic.schema.service.selector

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.selector.SelectAccount

object SelectAccountService : StoreFilterService<SelectAccount>(AfinaOrm, SelectAccount::class.java) {


}