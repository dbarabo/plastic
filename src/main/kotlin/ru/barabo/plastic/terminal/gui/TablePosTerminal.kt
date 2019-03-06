package ru.barabo.plastic.terminal.gui

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.terminal.entity.PosTerminal
import ru.barabo.plastic.terminal.service.PosTerminalService

object TablePosTerminal : EntityTable<PosTerminal>(columns, PosTerminalService)

private val columns = listOf(
    ColumnTableModel("Терминал", 30, PosTerminal::terminal),
    ColumnTableModel("Клиент", 100, PosTerminal::clientName),
    ColumnTableModel("Начал", 30, PosTerminal::pactStartFormat),
    ColumnTableModel("Конец", 30, PosTerminal::pactEndFormat),
    ColumnTableModel("Последняя", 30, PosTerminal::lastOperFormat),
    ColumnTableModel("%% ставка", 20, PosTerminal::percentCommission),
    ColumnTableModel("Название ставки", 70, PosTerminal::rateName),
    ColumnTableModel("Счет", 70, PosTerminal::accountCode),
    ColumnTableModel("Банк", 40, PosTerminal::bankName),
    ColumnTableModel("Адрес терминала", 80, PosTerminal::addressTerminal),
    ColumnTableModel("Комиссия", 30, PosTerminal::srcCommission),
    ColumnTableModel("Выкуплен", 20, PosTerminal::isOwnerTerminal)
    )
