package ru.barabo.plastic.unnamed.gui.dialog

import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.general.KeyValue
import ru.barabo.plastic.unnamed.general.TotalCardInfo
import java.awt.*
import java.lang.Exception
import java.util.*
import javax.swing.*

private const val TITLE = "Заказ неименных карт"

@Suppress("CAST_NEVER_SUCCEEDS")
object OrderDialog : JDialog(null as? JFrame, TITLE, true) {

    private val totalCardInfo: TotalCardInfo = DBStorePlastic.getInstance().totalCardInfo

    private var countCard: Int = 1

    private var resultOk = false

    private val numberText = javax.swing.JFormattedTextField().apply {
        formatterFactory = javax.swing.text.DefaultFormatterFactory(javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance()) )

        horizontalAlignment = SwingConstants.RIGHT

        text = countCard.toString()

    }

    private val productCombo = JComboBox<KeyValue>()

    init {
        setIconImage(Objects.requireNonNull<ImageIcon>(ResourcesManager.getIcon("order")).image)

        minimumSize = Dimension(
            Toolkit.getDefaultToolkit().screenSize.width / 5,
            Toolkit.getDefaultToolkit().screenSize.height / 20
        )
        setLocationRelativeTo(null)

        layout = GridBagLayout()

        add( JLabel("Кол-во карт:"), labelConstraint(0) )
        add( numberText, textConstraint(0) )

        add( JLabel("Продукт карты:"), labelConstraint(1) )
        add( productCombo, textConstraint(1) )

        add( JLabel(), labelConstraint(2))

        add( JPanel().apply {

            layout = GridLayout(1, 2, 2, 2)

            add(buttonOk())

            add(buttonCancel())

        }, textConstraint(2) )

        add( JLabel("Внимание:").apply { foreground = Color.BLUE },  labelConstraint(3))
        add( JLabel("После нажатия Ок, заказ отправится в ПЦ").apply {
            foreground = Color.BLUE },  textConstraint(3))

        pack()

        numberText.requestFocus()
    }

    fun showResultDialog(): ResultOrder {

        resultOk = false
        countCard = 1
        numberText.text = countCard.toString()
        productCombo.checkUpdate()

        isVisible = true

        return ResultOrder(if(resultOk) countCard else 0, getProductCardId() )
    }

    private fun getProductCardId(): Long = (productCombo.selectedItem as? KeyValue)?.key ?: 0L

    private fun buttonOk() = JButton("Ok").apply { addActionListener { selectedOk()  } }

    private fun buttonCancel() = JButton("Отмена").apply { addActionListener { selectedCancel()  } }

    private fun selectedOk() {

        countCard = try {
            numberText.text.toInt()
        } catch (e: Exception) {
            0
        }

        resultOk = countCard > 0

        isVisible = false
    }

    private fun selectedCancel() {
        countCard = 0

        resultOk = false

        isVisible = false
    }

    private fun JComboBox<KeyValue>.checkUpdate() {
        this.removeAllItems()

        totalCardInfo.getInstantIssueProducts().forEach { this.addItem(it) }

        if(itemCount == 1) {
            selectedIndex = 0
        }
        if(itemCount <= 1) {
            isEnabled = false
        }
    }
}

private fun textConstraint(gridY: Int) =
    GridBagConstraints(1, gridY, 1, 1, 1.0, 1.0,
        GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL,
        Insets(5, 2, 5, 2), 0, 0)

private fun labelConstraint(gridY: Int) =
    GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
        GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL,
        Insets(5, 2, 5, 2), 0, 0)

data class ResultOrder(val countCard: Int, val productCardId: Long)