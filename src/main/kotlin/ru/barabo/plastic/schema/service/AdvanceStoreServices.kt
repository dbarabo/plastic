package ru.barabo.plastic.schema.service

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.ClientType
import ru.barabo.plastic.schema.entity.Currency
import ru.barabo.plastic.schema.entity.Office
import ru.barabo.plastic.schema.entity.Terminal

object CurrencyService: StoreFilterService<Currency>(AfinaOrm, Currency::class.java)

object TerminalService : StoreFilterService<Terminal>(AfinaOrm,  Terminal::class.java)

object OfficeService : StoreFilterService<Office>(AfinaOrm,  Office::class.java)

object ClientTypeService : StoreFilterService<ClientType>(AfinaOrm,  ClientType::class.java)