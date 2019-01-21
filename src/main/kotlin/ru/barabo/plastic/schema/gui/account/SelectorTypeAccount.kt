package ru.barabo.plastic.schema.gui.account

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.schema.entity.Account
import ru.barabo.plastic.schema.service.AccountService
import javax.swing.*
import javax.swing.table.AbstractTableModel

class SelectorTypeAccount : JPanel(), StoreListener<List<Account>> {

    //private val logger = Logger.getLogger(SelectorTypeAccount::class.java.name)

    private val currencyScroll = JScrollPane(TableCurrency)

    private val terminalScroll = JScrollPane(TableTerminal)

    private val officeScroll = JScrollPane(TableOffice)

    private val clientTypeScroll = JScrollPane(TableClientType)

    init {

        layout = BoxLayout(this, BoxLayout.X_AXIS)

        AccountService.addListener(this)
    }

    override fun refreshAll(elemRoot: List<Account>, refreshType: EditType) {

        updateTablePanels()
    }

    private fun updateTablePanels() {

        val account = AccountService.selectedEntity()

        account?.let { updatePanels(it) } ?: removeAll()
    }

    private fun updatePanels(account: Account) {

        val isRemove = removeAbsent(account)

        val isAdd = addNew(account)

        if(isRemove || isAdd) {

            this.revalidate()

            this.invalidate()

            this.repaint()
        }
    }

    private fun addNew(account: Account): Boolean {

        val isAddCurrency = addCondition(currencyScroll, account.isCheckCurrency)

        val isAddTerminal = addCondition(terminalScroll, account.isCheckTerminal || account.isCheckBankomat)

        val isAddOffice = addCondition(officeScroll, account.isCheckOffice)

        val isAddClientType = addCondition(clientTypeScroll, account.isCheckClienttype)

        return isAddCurrency || isAddTerminal || isAddOffice || isAddClientType
    }

    private fun removeAbsent(account: Account): Boolean {
        val isDelCurrency = removeCondition(clientTypeScroll, account.isCheckClienttype)

        val isDelTerminal = removeCondition(officeScroll, account.isCheckOffice)

        val isDelOffice = removeCondition(terminalScroll, account.isCheckTerminal || account.isCheckBankomat)

        val isDelClientType = removeCondition(currencyScroll, account.isCheckCurrency)

        return isDelCurrency || isDelTerminal || isDelOffice || isDelClientType
    }

    private fun addCondition(comp: JComponent, isExistsCondition: Boolean): Boolean {
        return if(!components.contains(comp) && isExistsCondition) {
            add(comp)

            val modelTable = (comp.components[0] as? JTable)?.model as? AbstractTableModel

            modelTable?.fireTableStructureChanged()

            true
        } else false
    }

    private fun removeCondition(comp: JComponent, isExistsCondition:Boolean): Boolean =
        if(components.contains(comp) && !isExistsCondition) {
            remove(comp)

            true
        } else false
}