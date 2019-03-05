package ru.barabo.plastic.terminal.gui

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.terminal.entity.PosTerminal
import ru.barabo.plastic.terminal.service.PosTerminalService

object TablePosTerminal : EntityTable<PosTerminal>(columns, PosTerminalService)

private val columns = listOf(
    ColumnTableModel("Терминал", 30, PosTerminal::terminal),
    ColumnTableModel("Клиент", 100, PosTerminal::clientName),
    ColumnTableModel("Начал", 40, PosTerminal::pactStartFormat),
    ColumnTableModel("Конец", 40, PosTerminal::pactEndFormat),
    ColumnTableModel("Последняя", 40, PosTerminal::lastOperFormat),
    ColumnTableModel("%% ставка", 20, PosTerminal::percentCommission),
    ColumnTableModel("Название ставки", 70, PosTerminal::rateName),
    ColumnTableModel("Счет", 60, PosTerminal::accountCode),
    ColumnTableModel("Банк", 60, PosTerminal::bankName),
    ColumnTableModel("Адрес терминала", 100, PosTerminal::addressTerminal),
    ColumnTableModel("Выкуплен", 20, PosTerminal::isOwnerTerminal)
    )
