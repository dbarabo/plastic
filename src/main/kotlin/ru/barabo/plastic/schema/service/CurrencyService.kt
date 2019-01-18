package ru.barabo.plastic.schema.service

import ru.barabo.db.service.StoreService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.Currency
import ru.barabo.plastic.schema.entity.Terminal

object CurrencyService: StoreService<Currency, List<Currency>>(AfinaOrm, Currency::class.java) {

    override fun elemRoot(): List<Currency> = dataList
}

object TerminalValueService : StoreService<Terminal, List< Terminal>>(AfinaOrm,  Terminal::class.java) {

    override fun elemRoot(): List< Terminal> = dataList
}

