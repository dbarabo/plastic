package ru.barabo.plastic.terminal.gui

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.terminal.entity.PosTerminal
import ru.barabo.plastic.terminal.service.PosTerminalService

object TablePosTerminal : EntityTable<PosTerminal>(columns, PosTerminalService)

private val columns = listOf(
    ColumnTableModel("Терминал", 20, PosTerminal::terminal),
    ColumnTableModel("Клиент", 100, PosTerminal::clientName),
    ColumnTableModel("Начал", 20, PosTerminal::pactStartFormat),
    ColumnTableModel("Конец", 20, PosTerminal::pactEndFormat),
    ColumnTableModel("Последняя", 20, PosTerminal::lastOperFormat),
    ColumnTableModel("%% ставка", 15, PosTerminal::percentCommission),
    ColumnTableModel("Оборот т.р.", 20, PosTerminal::turnMonthInfo),
    ColumnTableModel("Возврат руб.", 20, PosTerminal::turnReverse),
    ColumnTableModel("Название ставки", 70, PosTerminal::rateName),
    ColumnTableModel("MCC", 10, PosTerminal::merchantCode),
    ColumnTableModel("Счет", 70, PosTerminal::accountCode),
    ColumnTableModel("Банк", 40, PosTerminal::bankName),
    ColumnTableModel("Адрес терминала", 80, PosTerminal::addressTerminal),
    ColumnTableModel("Комиссия", 15, PosTerminal::srcCommission),
    ColumnTableModel("Выкуплен", 10, PosTerminal::isOwnerTerminal),
    ColumnTableModel("Оборот терминала", 10, PosTerminal::turnTerminalInfo)
    //ColumnTableModel("Тип", 10, PosTerminal::typeTerminal)
    )
