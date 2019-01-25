package ru.barabo.plastic.schema.gui.account

import ru.barabo.gui.swing.table.saveEntityShowError
import ru.barabo.plastic.schema.entity.account.Account
import ru.barabo.plastic.schema.service.account.AccountService
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JOptionPane
import javax.swing.JTable

class ToolBarAccount(table: JTable) : AbstractTopToolBar(table) {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Создать счет", { createAccount() },	null),
        ButtonKarkas(null, null, null, null)
    )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    init {
        initButton()
    }

    private fun createAccount() {

        val accountName = JOptionPane.showInputDialog("Название счета") ?: return

        if(accountName.trim().isEmpty()) return

        val newAccount = Account(name = accountName)

        AccountService.saveEntityShowError(newAccount)
    }
}