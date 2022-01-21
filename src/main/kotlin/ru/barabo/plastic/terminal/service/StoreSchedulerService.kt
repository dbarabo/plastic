package ru.barabo.plastic.terminal.service

import ru.barabo.afina.AfinaOrm
import ru.barabo.db.SessionSetting
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.terminal.entity.Scheduler
import java.time.LocalTime

object StoreSchedulerService :  StoreFilterService<Scheduler>(AfinaOrm, Scheduler::class.java), ParamsSelect, SchedulerService {

    override fun selectParams(): Array<Any?>? = arrayOf(PosTerminalService.selectedEntity()?.terminal)

    override fun save(item: Scheduler, sessionSetting: SessionSetting): Scheduler {

        item.checkEndTime()

        if(item.waitTime != 0L && (28_200L > item.endTime + item.waitTime )) {
            item.waitTime = 28_200L - item.endTime
        }

        item.id?.let {
            item.state = if(item.endTime.toInt() - LocalTime.now().toSecondOfDay() > HOUR_2) 0L else 1L
        }
        return super.save(item, sessionSetting)
    }

    private const val HOUR_2: Long = 2*60*60

    override fun newRecord() {
        val newScheduler = Scheduler()

        newScheduler.endTime = generateEndTimeByNow()

        newScheduler.terminalId = PosTerminalService.selectedEntity()!!.terminal

        save(newScheduler)
    }

    private fun generateEndTimeByNow(): Long {
        val hourNext = LocalTime.now().hour + 1 + if(LocalTime.now().minute >= 30 && LocalTime.now().hour < 22) 1 else 0

        return LocalTime.of(hourNext, 0).toSecondOfDay().toLong()
    }

    override fun removeRecord() {
        dataList.firstOrNull { it != selectedEntity() && it.waitTime != 0L} ?:
        throw Exception("Как минимум должна быть одна рабочая смена")

        if(dataListCount() < 2) throw Exception("Как минимум должна быть одна рабочая смена")
        delete( selectedEntity()!! )
    }

    override fun isCanClose(): Boolean {
        dataList.firstOrNull { it.waitTime != 0L} ?:
        throw Exception("Как минимум должна быть одна рабочая смена")

        return true
    }
}

interface SchedulerService {
    fun newRecord()

    fun removeRecord()

    fun isCanClose(): Boolean
}
