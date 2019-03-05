package ru.barabo.plastic.schema.gui.selector

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.entity.selector.SelectClient
import ru.barabo.plastic.schema.service.selector.SelectAccountService
import ru.barabo.plastic.schema.service.selector.SelectClientService

private val accountColumns = listOf(
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

object SelectAccountTable : EntityTable<SelectAccount>(accountColumns, SelectAccountService)

private val clientColumns = listOf(
    ColumnTableModel("id клиента", 30, SelectClient::id, false),
    ColumnTableModel("Наименование клиента", 100, SelectClient::label, false),
    ColumnTableModel("Описание клиента", 180, SelectClient::description, false),
    ColumnTableModel("ИНН(БИК) клиента(банка)", 50, SelectClient::innOrBik, false)
)

object SelectClientTable : EntityTable<SelectClient>(clientColumns, SelectClientService)
