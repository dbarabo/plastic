package ru.barabo.plastic.packet.gui

import ru.barabo.plastic.release.packet.data.PlatinaCashIn
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.account.onlyButton
import ru.barabo.plastic.schema.gui.account.textAreaHorizontal
import ru.barabo.plastic.schema.gui.account.textConstraint
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import java.awt.Color
import java.awt.Component
import java.awt.Container
import java.awt.GridBagLayout
import java.text.DecimalFormat
import javax.swing.JDialog
import javax.swing.JTextArea
import javax.swing.SwingConstants

private const val TITLE = "ПКО комиссия за выдачу карты Visa Platina"

class CashInDialog(parentWindow: Component, private val platinaCashIn: PlatinaCashIn, processOk: (PlatinaCashIn)->Unit) :
    JDialog(ru.barabo.plastic.unnamed.gui.dialog.parentWindow(parentWindow), TITLE, true) {

    private val descriptionArea: JTextArea

    init {
        layout = GridBagLayout()

        textAreaOnly("Внимание: карту можно будет выдать, только после исполнения ПКО в кассе", 0).apply {

            this.background = Color.YELLOW
            this.foreground = Color.BLACK
        }

        textFieldHorizontal("№ ПКО", 2).apply {
            text = platinaCashIn.label
            horizontalAlignment = SwingConstants.RIGHT
            isEditable = false
        }

        textFieldHorizontal("Сумма комиссии", 3).apply {
            text = platinaCashIn.amount?.let { DecimalFormat("0.00").format(it) }?:""
            horizontalAlignment = SwingConstants.RIGHT
            isEditable = false
        }

        textFieldHorizontal("Касса", 4).apply {
            text = "${platinaCashIn.departmentLevel1} [${platinaCashIn.accountCodeCashBox}]"
            horizontalAlignment = SwingConstants.LEFT
            isEditable = false
        }

        textFieldHorizontal("Вноситель", 5).apply {
            text = platinaCashIn.customer
            horizontalAlignment = SwingConstants.LEFT
            isEditable = false
        }

        textAreaHorizontal("Назначение платежа", 6).apply {
            descriptionArea = this

            text = platinaCashIn.descriptionDefault

            isEditable = true
        }

        groupPanel("", 8, 1, 0, 2).apply {
            onlyButton("Отправить в кассу", 0, 0, "saveDB"){
                dispose()

                platinaCashIn.descriptionDefault = descriptionArea.text
                processOk(platinaCashIn)
            }

            onlyButton("Отменить (Как-нибудь потом)", 0, 1, "deleteDB"){ dispose() }
        }

        pack()

        setLocationRelativeTo(owner)
    }
}

fun Container.textAreaOnly(text: String, gridY: Int, width: Int = 2, height: Int = 2): JTextArea {

    return JTextArea().apply {

        this.text = text

        this.rows = height

        this.isEditable = false

        this@textAreaOnly.add(this, textConstraint(gridY, height, 0, width) )
    }
}
