package ru.barabo.plastic.schema.gui.account

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.schema.entity.Account
import ru.barabo.plastic.schema.service.AccountService

object TableAccount : EntityTable<Account>(columns, AccountService)

private val columns = listOf(
    ColumnTableModel("Счет", 50, Account::name, true),
    ColumnTableModel("От валюты", 20, Account::isCheckCurrency, true),
    ColumnTableModel("От pos-терминал", 20, Account::isCheckTerminal, true) ,
    ColumnTableModel("От банкомата", 20, Account::isCheckBankomat, true) ,
    ColumnTableModel("От офиса", 20, Account::isCheckOffice, true),
    ColumnTableModel("От клиента", 20, Account::isCheckClienttype, true),
    ColumnTableModel("Вычисляемое", 20, Account::isCalc, true),
    ColumnTableModel("Внешние счета", 20, Account::isExternSupport, true),
    ColumnTableModel("Ремарка", 90, Account::remark, true)
)