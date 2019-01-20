package ru.barabo.plastic.schema.gui.account

import ru.barabo.plastic.unnamed.gui.client.textConstraint
import java.awt.Container
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.border.TitledBorder

class DetailtAccountValue : JPanel() {

    private val accountSelectButton: JButton

    private val descAccount: JTextArea

    private val calcFormulaAccount: JComboBox<String>

    private val dependAccount: JComboBox<String>

    private val extCodeAccount: JTextField

    private val extBank: JButton

    private val extClient: JButton

    init {
        layout = GridBagLayout()

        groupPanel("Значение счета", 0, 5).apply {
            groupPanel("Счет", 0, 3).apply {

                button("Счет:", "Выберите счет...", 0) {}.apply { accountSelectButton = this }

                textArea("Описание счета", 2, 2).apply { descAccount = this }
            }

            groupPanel("Вычисляемый счет", 5, 4).apply {

                comboBox("Формула:", 0).apply { calcFormulaAccount = this }

                comboBox("Зависит от счета:", 2).apply { dependAccount = this }
            }

            groupPanel("Счет в другом банке", 9, 6).apply {

                textField("Код счета:",  0).apply { extCodeAccount = this }

                button("Банк счета", "Выберите банк...", 2) {}.apply { extBank = this }

                button("Клиент счета", "Выберите клиента...", 4) {}.apply { extClient = this }
            }
        }
    }
}

fun Container.comboBox(label: String, gridY: Int): JComboBox<String> {

    add( JLabel(label), labelConstraint(gridY) )

    return JComboBox<String>().apply {

        this@comboBox.add(this, textConstraint(gridY + 1) )
    }
}


fun Container.textField(label: String, gridY: Int): JTextField {

    add( JLabel(label), labelConstraint(gridY) )

    return JTextField().apply {

        this@textField.add(this, textConstraint(gridY + 1) )
    }
}

fun Container.textArea(label: String, gridY: Int, height: Int = 2): JTextArea {

    add( JLabel(label), labelConstraint(gridY) )

    return JTextArea().apply {

        this.rows = height

        this@textArea.add(this, textConstraint(gridY + 1, height) )
    }
}

fun Container.button(label: String, title: String, gridY: Int, clickListener: ()->Unit): JButton {

    add( JLabel(label), labelConstraint(gridY) )

    return JButton(title).apply {
        addActionListener { clickListener() }

        this@button.add(this, textConstraint(gridY + 1) )
    }
}

fun Container.groupPanel(title: String, gridY: Int, height: Int = 1): JPanel = JPanel().apply {
    border = TitledBorder(title)

    layout = GridBagLayout()

    this@groupPanel.add(this, textConstraint(gridY, height))
}

private fun textConstraint(gridY: Int, height: Int = 1) =
    GridBagConstraints(0, gridY, 1, height, 1.0, 1.0,
        GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL,
        Insets(5, 2, 5, 2), 0, 0)

private fun labelConstraint(gridY: Int) =
    GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
        GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL,
        Insets(5, 2, 5, 2), 0, 0)