package ru.barabo.plastic.terminal.gui

import org.jdesktop.swingx.JXDatePicker
import ru.barabo.plastic.schema.entity.selector.*
import ru.barabo.plastic.schema.gui.account.*
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import ru.barabo.plastic.schema.gui.schema.comboBox
import ru.barabo.plastic.schema.gui.selector.SelectAccountTab
import ru.barabo.plastic.schema.gui.selector.SelectClientTab
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import ru.barabo.plastic.terminal.entity.PercentRateTerminal
import ru.barabo.plastic.terminal.entity.PosTerminal
import ru.barabo.plastic.terminal.service.PercentRateTerminalService
import ru.barabo.plastic.terminal.service.PosTerminalService
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JTextField
import kotlin.math.max

class DialogCreateTerminal(private val component: Component, private val editEntity: PosTerminal? = null)
    : AbstractDialog(component, "Создать POS-Терминал") {

    private val terminal: JTextField

    private val serialNumber: JTextField

    private val accountCode: JButton

    private val accountLabel: JTextField

    private val rateList: JComboBox<PercentRateTerminal>

    private val datePicker: JXDatePicker

    private val address: JTextField

    private var selectAccount: SelectAccount? = null

    private val extCodeAccount: JTextField

    private val extBank: JButton

    private val extClient: JButton

    private var selectClient: SelectClient? = null

    private var selectBank: SelectClient? = null

    init {
        groupPanel("Выберите счет и задайте имя терминала и %% ставку", 0, 4, 0).apply {

            textFieldHorizontal("ID Терминала (J999999)", 0).apply {
                terminal = this

                this.text = editEntity?.terminal
            }

            textFieldHorizontal("Инвентарный номер", 1).apply {
                serialNumber = this

                this.text = editEntity?.serialNumber
            }

            groupPanel("Счет в НАШЕМ банке", 2, 2, 0, 2).apply {
                buttonHorisontal("Расчетный счет клиента", defaultSelectAccountLabel(),0, ::selectAccount).apply {
                    accountCode = this

                    accountCode.text = editEntity?.accountCode
                }

                textFieldHorizontal("Наименование счета", 1).apply {
                    accountLabel = this

                    isEditable = false
                    text = selectAccount?.name
                }
            }

            groupPanel("Счет в ДРУГОМ банке", 4, 3, 0, 2).apply {
                textFieldHorizontal("Код счета",  0).apply { extCodeAccount = this }

                button("", DetailAccountValue.SELECT_BANK, 1, 2, ::selectBank).apply { extBank = this }

                button("", DetailAccountValue.SELECT_CLIENT, 2, 2, ::selectClient).apply { extClient = this }
            }

            comboBox("%% Ставка", 7, PercentRateTerminalService.elemRoot()).apply {

                rateList = this
            }

            datePickerHorisontal("Дата начала", 8).apply {datePicker = this }

            textFieldHorizontal("Адрес терминала", 9).apply { address = this }
        }

        createOkCancelButton(10)

        packWithLocation()
    }

    private fun defaultSelectAccountLabel(): String =
        if(selectAccount?.code?.isNotBlank() == true) selectAccount?.code!! else "Нажмите для выбора счета..."

    override fun okProcess() {

        selectAccount?.let { createInternalTerminal(it) } ?: createExternalTerminal()
    }

    private fun createInternalTerminal(selectAccount: SelectAccount) {

        /*if(editEntity != null) {

            PosTerminalService.editAccountTerminal(editEntity.terminal, selectAccount)

        } else {*/
            PosTerminalService.createTerminal(terminal.text, selectAccount, rateList.selectedItem as? PercentRateTerminal,
                datePicker.date, address.text, serialNumber.text)
        //}
    }

    private fun createExternalTerminal() {
        PosTerminalService.createExternalTerminal(terminal.text, rateList.selectedItem as? PercentRateTerminal,
            address.text, selectBank, selectClient, extCodeAccount.text, serialNumber.text)
    }

    private fun selectAccount() {
        cancel()

        SelectAccountTab.selectTab(component) {
            selectAccount = it

            accountCode.text = defaultSelectAccountLabel()

            accountLabel.text = selectAccount?.name

            clearExtBankAccount()

            showDialogResultOk()
        }
    }

    private fun selectBank() {
        cancel()

        SelectClient.filter.filterEntity.doctype = DOCTYPE_BANK
        SelectClient.filter.filterEntity.doctype2 = DOCTYPE_BANK
        SelectClient.filter.applyFilter()

        SelectClientTab.selectTab(component) {
            selectBank = it

            extBank.text = it?.label

            clearInternalAccount()

            showDialogResultOk()
        }
    }

    private fun selectClient() {
        cancel()

        SelectClient.filter.filterEntity.doctype = DOCTYPE_JURIC
        SelectClient.filter.filterEntity.doctype2 = DOCTYPE_PBOUL
        SelectClient.filter.applyFilter()

        SelectClientTab.selectTab(component) {

            selectClient = it

            extClient.text = it?.label

            clearInternalAccount()

            showDialogResultOk()
        }
    }

    private fun clearExtBankAccount() {
        selectBank = null
        selectClient = null

        extBank.text = null
        extClient.text = null
        extCodeAccount.text = null
    }

    private fun clearInternalAccount() {
        selectAccount = null

        accountCode.text = null

        accountLabel.text = null
    }
}

fun Container.buttonHorisontal(label: String, title: String, gridY: Int, clickListener: ()->Unit): JButton {

    val label = JLabel(label)
    add( label, labelConstraint(gridY) )

    return JButton(title).apply {
        addActionListener { clickListener() }

        this.preferredSize = Dimension(this.preferredSize.width, max(this.preferredSize.height, label.height))

        this@buttonHorisontal.add(this, textConstraint(gridY, gridX = 1) )
    }
}

fun Container.datePickerHorisontal(label: String, gridY: Int): JXDatePicker {
    add( JLabel(label), labelConstraint(gridY) )

    return JXDatePicker().apply {
        this@datePickerHorisontal.add(this, textConstraint(gridY, gridX = 1) )
    }
}