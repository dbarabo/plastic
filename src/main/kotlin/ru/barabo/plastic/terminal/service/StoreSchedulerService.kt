package ru.barabo.plastic.terminal.service

import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.terminal.entity.Scheduler

object StoreSchedulerService :  StoreFilterService<Scheduler>(AfinaOrm, Scheduler::class.java), ParamsSelect {

    override fun selectParams(): Array<Any?>? = arrayOf(PosTerminalService.selectedEntity()?.terminal)
}