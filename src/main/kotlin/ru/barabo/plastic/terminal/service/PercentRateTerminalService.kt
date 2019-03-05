package ru.barabo.plastic.terminal.service

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.terminal.entity.PercentRateTerminal

object PercentRateTerminalService : StoreFilterService<PercentRateTerminal>(AfinaOrm, PercentRateTerminal::class.java) {

    fun getItemById(id: Long?): PercentRateTerminal? = dataList.firstOrNull { it.id == id }
}