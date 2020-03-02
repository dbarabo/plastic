package ru.barabo.plastic.terminal.gui.scheduler

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.card.gui.toolButton
import ru.barabo.plastic.card.service.StoreCardService
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import ru.barabo.plastic.terminal.entity.Scheduler
import ru.barabo.plastic.terminal.service.StoreSchedulerService
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*

class DialogScheduler(component: Component) : AbstractDialog(component, "Расписание отправки транзакций") {

    init {

        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        layout = BorderLayout()

        add( ToolBarScheduler { dispose() }, BorderLayout.NORTH)

        add(JScrollPane(TableScheduler), BorderLayout.CENTER)

        packWithLocation()
    }

    override fun okProcess() {}
}

object TableScheduler : EntityTable<Scheduler>(schedulerColumns, StoreSchedulerService)

private val schedulerColumns = listOf(
    ColumnTableModel("Конец рабочей смены", 50, Scheduler::isWorkEndTime, true),
    ColumnTableModel("Время окончания", 20, Scheduler::endTimeDate, true),
    ColumnTableModel("Статус", 20, Scheduler::status, false)
)

class ToolBarScheduler(disposeDialog: ()->Unit) : JToolBar() {
    init {
        toolButton("endFilter", "Закрыть") { processShowError {
            if(StoreSchedulerService.isCanClose()) disposeDialog() }
        }

        toolButton("insertDB", "Новая запись") { processShowError { StoreSchedulerService.newRecord() } }

        toolButton("deleteDB", "Удалить запись") { processShowError { StoreSchedulerService.removeRecord() } }
    }
}
