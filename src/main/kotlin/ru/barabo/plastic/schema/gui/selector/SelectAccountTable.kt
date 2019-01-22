package ru.barabo.plastic.schema.gui.selector

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.service.selector.SelectAccountService

object SelectAccountTable : EntityTable<SelectAccount>(columns, SelectAccountService)

private val columns = listOf(
    ColumnTableModel("id счета", 30, SelectAccount::id, false),
    ColumnTableModel("Код счета", 80, SelectAccount::code, false),
    ColumnTableModel("Наименование счета", 130, SelectAccount::name, false),
    ColumnTableModel("Описание счета", 180, SelectAccount::description, false),
    ColumnTableModel("Открыт", 30, SelectAccount::opened, false),
    ColumnTableModel("id клиента", 30, SelectAccount::clientId, false),
    ColumnTableModel("Наименование клиента", 200, SelectAccount::clientName, false),
    ColumnTableModel("Описание клиента", 30, SelectAccount::clientDescription, false),
    ColumnTableModel("ИНН клиента", 30, SelectAccount::clientInn, false)
)
