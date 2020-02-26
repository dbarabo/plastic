package ru.barabo.plastic.terminal.gui.scheduler

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import ru.barabo.plastic.terminal.entity.Scheduler
import ru.barabo.plastic.terminal.service.StoreSchedulerService
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane

open class DialogScheduler(component: Component) : AbstractDialog(component, "Расписание отправки транзакций") {

    init {

        layout = BorderLayout()

        add(JScrollPane(TableScheduler), BorderLayout.CENTER)

        add(JPanel().apply {
            layout = BorderLayout()
            add(JButton("Ok").apply { addActionListener { dispose() } }, BorderLayout.WEST  )

            add(JButton("Отмена").apply { addActionListener { dispose() } },  BorderLayout.EAST)
        } ,  BorderLayout.SOUTH)

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
