package ru.barabo.plastic.unnamed.gui.dialog

import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.unnamed.data.RowFieldInPath
import ru.barabo.plastic.unnamed.general.ClientOutData
import java.awt.*
import java.util.*
import javax.swing.*

import ru.barabo.plastic.unnamed.gui.client.*

private const val TITLE = "Выдача карты клиенту"

private fun parentWindow(component: Component): Frame? = SwingUtilities.getWindowAncestor(component) as? Frame

class OutCardToClient(component: Component, private val cardField: RowFieldInPath) :
    JDialog(parentWindow(component), TITLE, true) {

    private val clientOutData = ClientOutData()

    private val cardNumber = JTextField(cardField.getCardNumber()).apply {
        isEditable = false
    }

    private val cardProduct = JTextField(cardField.getTypeCard()).apply {
        isEditable = false
    }

    private val selectClient = JButton(clientOutData.toString()).apply {
        addActionListener { selectClient() }
    }

    private val phoneClient = JTextField(clientOutData.phone)

    private val codeWord = JTextField(clientOutData.codeWord)

    init {
        setIconImage(Objects.requireNonNull<ImageIcon>(ResourcesManager.getIcon("ok")).image)

        minimumSize = Dimension(
            Toolkit.getDefaultToolkit().screenSize.width / 5,
            Toolkit.getDefaultToolkit().screenSize.height / 20
        )

        setLocationRelativeTo(parentWindow(component))

        layout = GridBagLayout()

        add(JLabel("№ выдаваемой карты:"), labelConstraint(0))
        add(cardNumber, textConstraint(0))

        add(JLabel("Тип выдаваемой карты:"), labelConstraint(1))
        add(cardProduct, textConstraint(1))

        add(JLabel("Клиент выдаваемой карты:"), labelConstraint(2))
        add(selectClient, textConstraint(2))

        add(JLabel("Телефон клиента:"), labelConstraint(3))
        add(phoneClient, textConstraint(3))

        add(JLabel("Кодовое слово:"), labelConstraint(4))
        add(codeWord, textConstraint(4))

        add(JPanel().apply {

            layout = GridLayout(1, 4, 2, 2)

          // add(OrderDialog.buttonOk())

         //   add(OrderDialog.buttonCancel())

        }, textConstraint(2))

        OrderDialog.add(JLabel("Внимание:").apply { foreground = Color.BLUE }, labelConstraint(3))
        OrderDialog.add(JLabel("После нажатия Ок, заказ отправится в ПЦ").apply {
            foreground = Color.BLUE
        }, textConstraint(3))

        pack()

        selectClient.requestFocus()
    }

    private fun selectClient() {
        TODO()
    }
}
