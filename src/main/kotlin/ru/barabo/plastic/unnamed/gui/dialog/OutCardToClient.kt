package ru.barabo.plastic.unnamed.gui.dialog

import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.unnamed.data.RowFieldClient
import ru.barabo.plastic.unnamed.data.RowFieldInPath
import ru.barabo.plastic.unnamed.general.ClientOutData
import ru.barabo.plastic.unnamed.general.ResultOutClient
import ru.barabo.plastic.unnamed.general.toDigitsOnly
import ru.barabo.plastic.unnamed.gui.client.ClientSelect
import ru.barabo.plastic.unnamed.gui.client.labelConstraint
import ru.barabo.plastic.unnamed.gui.client.textConstraint
import ru.barabo.plastic.unnamed.gui.errorMessage
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.*
import javax.swing.*

private const val TITLE = "Выдача карты клиенту"

fun parentWindow(component: Component): Frame? = SwingUtilities.getWindowAncestor(component) as? Frame

class OutCardToClient(private val component: Component, private val cardField: RowFieldInPath) :
    JDialog(parentWindow(component), TITLE, true) {

    private val clientOutData = ClientOutData.createByCardField(cardField)

    private val cardNumber = JTextField(cardField.getCardNumber()).apply {
        isEditable = false
    }

    private val cardProduct = JTextField(cardField.getTypeCard()).apply {
        isEditable = false
    }

    private val selectClient = JButton(clientOutData.toString()).apply {
        addActionListener { selectClient() }
    }

    private val phoneClient = JTextField(clientOutData.phone).apply {

        addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent?) {
                val textField = (e?.source as? JTextField) ?: return

                clientOutData.phone = textField.text?.trim() ?: ""
            }

            override fun focusGained(e: FocusEvent?) {}
        })
    }

    private val codeWord = JTextField(clientOutData.codeWord).apply {

        addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent?) {
                val textField = (e?.source as? JTextField) ?: return

                clientOutData.codeWord = textField.text?.trim() ?: ""
            }

            override fun focusGained(e: FocusEvent?) {}
        })
    }

    private var resultOk = false

    private lateinit var processResultOutClient: (ResultOutClient) -> Unit

    private fun buttonOk() = JButton("Ok").apply { addActionListener { selectedOk() } }

    private fun buttonCancel() = JButton("Отмена").apply { addActionListener { selectedCancel() } }

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

        add(JLabel(), labelConstraint(5))
        add(JPanel().apply {

            layout = GridLayout(1, 4, 2, 2)

            add( JLabel() )
            add( JLabel() )

            add( buttonOk() )

            add( buttonCancel() )

        }, textConstraint(5))

        pack()

        selectClient.requestFocus()
    }

    fun showResultDialog(processResultOutClient: (ResultOutClient) -> Unit): Boolean {

        resultOk = false

        if(cardField.id == null) {
            return errorMessage(ERROR_CARD_IS_EMPTY)
        }

        this.processResultOutClient = processResultOutClient

        isVisible = true

        return true
    }

    private fun createResultOutClient(): ResultOutClient =
        ResultOutClient(clientId = clientOutData.clientField.id,
            contentId = cardField.id,
            phone = phoneClient.text.trim(),
            codeWord = codeWord.text.trim())


    private fun selectClient() {
        dispose()

        ClientSelect.selectClient(component, ::processResultFromClient)
    }

    private fun processResultFromClient(rowFieldClient: RowFieldClient?) {

        rowFieldClient?.let {
            clientOutData.clientField = it

            selectClient.text = clientOutData.toString()

            if(!it.phone().isNullOrEmpty()) {
                clientOutData.phone = it.phone()

                phoneClient.text = clientOutData.phone
            }

            pack()
        }

        isVisible = true
    }

    private fun selectedOk() {
        resultOk = checkResult()

        if(resultOk) {
            dispose()

            processResultOutClient( createResultOutClient() )
        }
    }

    private fun checkResult(): Boolean = clientOutData.checkClientId() && clientOutData.phone.checkMobilePhone()

    private fun selectedCancel() {
        resultOk = false

        dispose()
    }

    companion object {
        private const val ERROR_CARD_IS_EMPTY = "Не выбрана карта для клиента"
    }
}

fun String.checkMobilePhone(): Boolean = toDigitsOnly().run {
   when {
       length < 10 -> errorMessage(INCORRECT_PHONE)
       length == 10 -> true
       length == 11 -> if(substring(0..0) !in listOf("7", "8")) errorMessage(INCORRECT_PHONE) else true
       else -> errorMessage(TO_LONG_PHONE)
   }
}

private const val  INCORRECT_PHONE = "Некорректный номер мобильного телефона"

private const val  TO_LONG_PHONE = "Слишком длинный номер мобильного телефона"
