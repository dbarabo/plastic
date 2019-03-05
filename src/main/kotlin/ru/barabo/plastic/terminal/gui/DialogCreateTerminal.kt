package ru.barabo.plastic.terminal.gui

import org.jdesktop.swingx.JXDatePicker
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.account.labelConstraint
import ru.barabo.plastic.schema.gui.account.textConstraint
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import ru.barabo.plastic.schema.gui.schema.comboBox
import ru.barabo.plastic.schema.gui.selector.SelectAccountTab
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import ru.barabo.plastic.terminal.entity.PercentRateTerminal
import ru.barabo.plastic.terminal.service.PercentRateTerminalService
import ru.barabo.plastic.terminal.service.PosTerminalService
import java.awt.*
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JTextField

class DialogCreateTerminal(private val component: Component) : AbstractDialog(component, "Создать POS-Терминал") {

    private val terminal: JTextField

    private val accountCode: JButton

    private val accountLabel: JTextField

    private val rateList: JComboBox<PercentRateTerminal>

    private val datePicker: JXDatePicker

    private val address: JTextField

    private var selectAccount: SelectAccount? = null

    init {
        groupPanel("Выберите счет и задайте имя терминала и %% ставку", 0, 4, 0).apply {

            textFieldHorizontal("ID Терминала (J999999)", 0).apply { terminal = this }

            buttonHorisontal("Расчетный счет клиента", defaultSelectAccountLabel(),1, ::selectAccount).apply {
                accountCode = this
            }

            textFieldHorizontal("Наименование счета", 2).apply {
                accountLabel = this

                isEditable = false
                text = selectAccount?.name
            }

            comboBox("%% Ставка", 3, PercentRateTerminalService.elemRoot()).apply { rateList = this }

            datePickerHorisontal("Дата начала", 4).apply {datePicker = this }

            textFieldHorizontal("Адрес терминала", 5).apply { address = this }
        }

        createOkCancelButton(6)

        pack()
    }

    private fun defaultSelectAccountLabel(): String = selectAccount?.code ?: "Нажмите для выбора счета..."

    override fun okProcess() {

        PosTerminalService.createTerminal(terminal.text, selectAccount, rateList.selectedItem as? PercentRateTerminal,
            datePicker.date, address.text)
    }

    private fun selectAccount() {
        cancel()

        SelectAccountTab.selectTab(component) {
            selectAccount = it

            accountCode.text = defaultSelectAccountLabel()

            accountLabel.text = selectAccount?.name

            showDialogResultOk()
        }
    }
}

fun Container.buttonHorisontal(label: String, title: String, gridY: Int, clickListener: ()->Unit): JButton {

    add( JLabel(label), labelConstraint(gridY) )

    return JButton(title).apply {
        addActionListener { clickListener() }

        this@buttonHorisontal.add(this, textConstraint(gridY, gridX = 1) )
    }
}

fun Container.datePickerHorisontal(label: String, gridY: Int): JXDatePicker {
    add( JLabel(label), labelConstraint(gridY) )

    return JXDatePicker().apply {
        this@datePickerHorisontal.add(this, textConstraint(gridY, gridX = 1) )
    }
}