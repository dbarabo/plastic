package ru.barabo.plastic.schema.gui.account

import org.apache.log4j.Logger
import ru.barabo.db.EditType
import ru.barabo.db.service.StoreListener
import ru.barabo.gui.swing.table.saveEntityShowError
import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.schema.entity.account.AccountValue
import ru.barabo.plastic.schema.entity.selector.DOCTYPE_BANK
import ru.barabo.plastic.schema.entity.selector.DOCTYPE_JURIC
import ru.barabo.plastic.schema.entity.selector.DOCTYPE_PBOUL
import ru.barabo.plastic.schema.entity.selector.SelectClient
import ru.barabo.plastic.schema.gui.selector.FilterKeyLister
import ru.barabo.plastic.schema.gui.selector.SelectAccountTab
import ru.barabo.plastic.schema.gui.selector.SelectClientTab
import ru.barabo.plastic.schema.service.account.*
import ru.barabo.plastic.unnamed.gui.errorMessage
import java.awt.Container
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.*
import javax.swing.*
import javax.swing.border.TitledBorder

class DetailAccountValue : JPanel(), StoreListener<List<AccountValue>> {

    private val logger = Logger.getLogger(DetailAccountValue::class.java.name)

    private val accountSelectButton: JButton

    private val labelAccount: JTextArea

    private val calcFormulaAccount: JComboBox<String>

    private val dependAccount: JComboBox<String>

    private val extCodeAccount: JTextField

    private val extBank: JButton

    private val extClient: JButton

    private val save: JButton

    private val delete: JButton

    private var accountValue: AccountValue? = null

    companion object {
        private const val SELECT_ACCOUNT = "Выберите счет..."

        private const val SELECT_BANK = "Выберите банк..."

        private const val SELECT_CLIENT = "Выберите клиента..."
    }

    init {
        layout = GridBagLayout()

        groupPanel("Значение счета", 0, 5).apply {
            groupPanel("Счет", 0, 3).apply {

                button("Счет:", SELECT_ACCOUNT, 0) { selectAccount() }.apply { accountSelectButton = this }

                textArea("Описание счета", 2, 2).apply {
                    this.lineWrap = true
                    labelAccount = this
                }
            }

            groupPanel("Вычисляемый счет", 5, 4).apply {

                comboBox("Формула:", 0).apply {
                    calcFormulaAccount = this

                    addActionListener {

                        checkAccountValueShowError()

                        logger.error("CHECK_LISTENER=$selectedItem")

                        accountValue?.calcFormula = selectedItem as? String
                    }
                }

                comboBox("Зависит от счета:", 2).apply { dependAccount = this }
            }

            groupPanel("Счет в другом банке", 9, 6).apply {

                textFieldVertical("Код счета:",  0).apply {
                    extCodeAccount = this
                    addKeyListener( FilterKeyLister {
                        accountValue?.extCodeAccount = text?.trim()
                    })
                }

                button("Банк счета", SELECT_BANK, 2) { selectBank()  }.apply { extBank = this }

                button("Клиент счета", SELECT_CLIENT, 4) { selectClient() }.apply { extClient = this }
            }

            groupPanel("", 15, 2).apply {

                onlyButton("Сохранить", 0, 0, "saveDB"){ saveAccountValue() }.apply { save = this }

                onlyButton("Очистить", 0, 1, "deleteDB"){}.apply { delete = this }
            }
        }

        AccountValueService.addListener(this)
    }

    private fun selectBank() {

        SelectClient.filter.filterEntity.doctype = DOCTYPE_BANK
        SelectClient.filter.filterEntity.doctype2 = DOCTYPE_BANK
        SelectClient.filter.applyFilter()

        SelectClientTab.selectTab(extBank) {
            checkAccountValueShowError()

            accountValue?.extBankId = it?.id
            accountValue?.extBankName = it?.label

            clearInternalAccount()
        }
    }

    private fun selectClient() {

        SelectClient.filter.filterEntity.doctype = DOCTYPE_JURIC
        SelectClient.filter.filterEntity.doctype2 = DOCTYPE_PBOUL
        SelectClient.filter.applyFilter()

        SelectClientTab.selectTab(extClient) {
            checkAccountValueShowError()

            accountValue?.extClient = it?.id
            accountValue?.extClientLabel = it?.label

            clearInternalAccount()
        }
    }

    private fun clearInternalAccount() {
        accountValue?.valueAccount = null
        accountValue?.code = null
        accountValue?.labelAccount = null

        updateAccount(true)

        updateOtherBank(true)
    }

    private fun selectAccount() {
        SelectAccountTab.selectTab(accountSelectButton) {
            checkAccountValueShowError()

            accountValue?.valueAccount = it?.id
            accountValue?.code = it?.code
            accountValue?.labelAccount = it?.name

            accountValue?.extBankId = null
            accountValue?.extBankName = null
            accountValue?.extClient = null
            accountValue?.extClientLabel = null
            accountValue?.extCodeAccount = null

            updateAccount(true)

            updateOtherBank(AccountService.selectedEntity()?.isExternSupport == true)
        }
    }

    private fun checkAccountValueShowError() {
        processShowError(::checkAccountValue)
    }

    private fun checkAccountValue() {
        if(accountValue == null) {
            accountValue = AccountValue()
        }

        val account = AccountService.selectedEntity() ?: throw Exception("Не выбран счет для значения!")

        if(accountValue?.transactAccount == null) {
            accountValue?.transactAccount = account.id ?: throw Exception("Не выбран счет для значения!")
        }

        if(account.isCheckCurrency) {
            accountValue?.currency = CurrencyService.selectedEntity()?.code ?: throw Exception("Не выбрана валюта для значения!")
        }

        if(account.isCheckTerminal || account.isCheckBankomat) {
            accountValue?.terminalId = TerminalService.selectedEntity()?.terminalId ?: throw Exception("Не выбран терминал для значения!")
        }

        if(account.isCheckOffice) {
            accountValue?.office = OfficeService.selectedEntity()?.id ?: throw Exception("Не выбран офис для значения!")
        }

        if(account.isCheckClienttype) {
            accountValue?.clientType = ClientTypeService.selectedEntity()?.id ?: throw Exception("Не выбран тип клиента для значения!")
        }
    }

    override fun refreshAll(elemRoot: List<AccountValue>, refreshType: EditType) {
        accountValue = AccountValueService.selectedEntity()

        updateComponents()
    }

    private fun saveAccountValue() {

        accountValue?.let { AccountValueService.saveEntityShowError(it) }
    }


    private fun updateComponents() {

        val parentAccount = AccountService.selectedEntity()

        updateAccount(parentAccount?.isCalc != true)

        updateCalcFunc(parentAccount?.isCalc == true)

        updateOtherBank(parentAccount?.isExternSupport == true)
    }

    private fun updateAccount(isEnabledAccount: Boolean ) {

        with(accountSelectButton) {
            text = accountValue?.code ?: SELECT_ACCOUNT

            isEnabled = isEnabledAccount
        }

        with(labelAccount) {
            text = accountValue?.labelAccount

            isEnabled = isEnabledAccount
        }
    }

    private fun updateCalcFunc(isEnabledCalc: Boolean) {

        with(calcFormulaAccount) {

            calcFormulaAccount.checkAddNewList(AccountService.getCalcFuncByChecked())

            logger.error("calcFormula=${accountValue?.calcFormula}")

            logger.error("selectedItem=$selectedItem")

            selectedItem = accountValue?.calcFormula

            isEnabled = isEnabledCalc
        }

        with(dependAccount) {
            selectedItem = accountValue?.dependAccountName

            isEnabled = isEnabledCalc
        }
    }

    private fun JComboBox<String>.checkAddNewList(list: List<String>) {

        logger.error("list=$list")

        if(list.isEmpty()) {
            removeAllItems()
            return
        }

        if(isNoNeedRemoveOldList(list)) return

        updateItemsList(list)
    }

    private fun <T> JComboBox<T>.updateItemsList(list: List<T>) {

        val listeners = actionListeners

        listeners.forEach { removeActionListener(it) }

        removeAllItems()

        for(item in list) {
            addItem(item)
        }

        listeners.forEach { addActionListener(it) }
    }

    private fun JComboBox<String>.isNoNeedRemoveOldList(list: List<String>): Boolean {
        if(itemCount != list.size) return false

        for(index in 0 until itemCount) {
            if(!list.contains(getItemAt(index))) return false
        }

        return true
    }


    private fun updateOtherBank(isExternSupport: Boolean) {

        with(extCodeAccount) {
            text = accountValue?.extCodeAccount

            isEnabled = isExternSupport
        }

        with(extBank) {
            text = accountValue?.extBankName ?: SELECT_BANK

            isEnabled = isExternSupport
        }

        with(extClient) {
            text = accountValue?.extClientLabel ?: SELECT_CLIENT

            isEnabled = isExternSupport
        }
    }
}

fun processShowError(process: ()->Unit) {
    try {
        process()
    } catch (e: Exception) {
        errorMessage(e.message)
    }
}

fun Container.comboBox(label: String, gridY: Int, list: List<String>? = null): JComboBox<String> {

    add( JLabel(label), labelConstraint(gridY) )

    val items = list?.let { Vector<String>(it) }

    val combo = items?.let { JComboBox<String>(it) } ?: JComboBox()

    add(combo, textConstraint(gridY + 1) )

    return combo
}

fun Container.textFieldVertical(label: String, gridY: Int): JTextField {

    add( JLabel(label), labelConstraint(gridY) )

    return JTextField().apply {

        this@textFieldVertical.add(this, textConstraint(gridY + 1) )
    }
}

fun Container.textArea(label: String, gridY: Int, height: Int = 2): JTextArea {

    add( JLabel(label), labelConstraint(gridY) )

    return JTextArea().apply {

        this.rows = height

        this.isEditable = false

        this@textArea.add(this, textConstraint(gridY + 1, height) )
    }
}

fun Container.onlyButton(title: String, gridY: Int, gridX: Int = 0, ico: String? = null, clickListener: ()->Unit): JButton =
    JButton(title).apply {
        ico?.let { this.icon = ResourcesManager.getIcon(it) }

        addActionListener { clickListener() }

        this@onlyButton.add(this, textConstraint(gridY, 1, gridX) )
    }

fun Container.button(label: String, title: String, gridY: Int, clickListener: ()->Unit): JButton {

    add( JLabel(label), labelConstraint(gridY) )

    return JButton(title).apply {
        addActionListener { clickListener() }

        this@button.add(this, textConstraint(gridY + 1) )
    }
}

fun Container.liteGroup(title: String, gridY: Int, gridX: Int = 0): JPanel = JPanel().apply {
    border = TitledBorder(title)

    layout = GridBagLayout()

    this@liteGroup.add(this, labelConstraint(gridY, gridX))
}

fun Container.groupPanel(title: String, gridY: Int, height: Int = 1, gridX: Int = 0): JPanel = JPanel().apply {
    border = TitledBorder(title)

    layout = GridBagLayout()

    this@groupPanel.add(this, textConstraint(gridY, height, gridX))
}

internal fun textConstraint(gridY: Int, height: Int = 1, gridX: Int = 0, width: Int = 1) =
    GridBagConstraints(gridX, gridY, width, height, 1.0, 0.6,
        GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL,
        Insets(5, 2, 5, 2), 0, 0)

internal fun labelConstraint(gridY: Int, gridX: Int = 0) =
    GridBagConstraints(gridX, gridY, 1, 1, 0.0, 0.0,
        GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL,
        Insets(5, 2, 5, 2), 0, 0)