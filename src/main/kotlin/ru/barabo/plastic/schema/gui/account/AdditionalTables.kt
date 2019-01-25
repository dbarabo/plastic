package ru.barabo.plastic.schema.gui.account

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.schema.entity.account.ClientType
import ru.barabo.plastic.schema.entity.account.Currency
import ru.barabo.plastic.schema.entity.account.Office
import ru.barabo.plastic.schema.entity.account.Terminal
import ru.barabo.plastic.schema.service.account.ClientTypeService
import ru.barabo.plastic.schema.service.account.CurrencyService
import ru.barabo.plastic.schema.service.account.OfficeService
import ru.barabo.plastic.schema.service.account.TerminalService

object TableCurrency : EntityTable<Currency>(currencyColumns, CurrencyService)

object TableTerminal : EntityTable<Terminal>(terminalColumns, TerminalService)

object TableOffice : EntityTable<Office>(officeColumns, OfficeService)

object TableClientType : EntityTable<ClientType>(clientTypeColumns,
    ClientTypeService
)

private val currencyColumns = listOf(
    ColumnTableModel("Код валюты", 20, Currency::code, false),
    ColumnTableModel("Валюта", 20, Currency::name, false)
)

private val terminalColumns = listOf(
    ColumnTableModel("Id Терминала", 30, Terminal::terminalId, false),
    ColumnTableModel("Терминал", 50, Terminal::name, false)
)

private val officeColumns = listOf(
    ColumnTableModel("Офис", 50, Office::name, false)
)

private val clientTypeColumns = listOf(
    ColumnTableModel("Тип клиента", 50, ClientType::name, false)
)


