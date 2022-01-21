package ru.barabo.plastic.schema.gui.account

import ru.barabo.gui.swing.ButtonKarkas
import ru.barabo.gui.swing.ResourcesManager
import ru.barabo.gui.swing.table.saveEntityShowError
import ru.barabo.plastic.schema.entity.account.Account
import ru.barabo.plastic.schema.service.account.AccountService
import ru.barabo.total.gui.any.AbstractTopToolBar
import java.awt.Container
import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.JTable

class ToolBarAccount(table: JTable) : AbstractTopToolBar(table) {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Создать счет") { createAccount() },
        ButtonKarkas(null, null, null, null)
    )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons


    init {
        initButton()

        onOffButton("Только чтение", TableAccount.isReadOnly) {
            TableAccount.isReadOnly = !TableAccount.isReadOnly
        }
    }

    private fun createAccount() {

        val accountName = JOptionPane.showInputDialog("Название счета") ?: return

        if(accountName.trim().isEmpty()) return

        val newAccount = Account(name = accountName)

        AccountService.saveEntityShowError(newAccount)
    }
}

fun Container.onOffButton(title: String, isSelected: Boolean = false, clickListener: ()->Unit): JCheckBox {
    return JCheckBox(title, ResourcesManager.getIcon("off"), isSelected).apply {
        selectedIcon = ResourcesManager.getIcon("on")

        addActionListener { clickListener() }

        this@onOffButton.add(this)
    }
}