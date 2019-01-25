package ru.barabo.plastic.schema.gui.schema

import ru.barabo.plastic.schema.entity.account.Account
import ru.barabo.plastic.schema.gui.account.*
import ru.barabo.plastic.schema.service.account.AccountService
import ru.barabo.plastic.schema.service.schema.SchemaService
import ru.barabo.plastic.unnamed.gui.dialog.parentWindow
import java.awt.Component
import java.awt.Container
import java.awt.GridBagLayout
import java.util.*
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JLabel

private const val TITLE = "Создание схемы проводки"

class DialogCreateSchema(private val component: Component) : JDialog(parentWindow(component),
    TITLE, true) {

    @Volatile private var resultOk = true

    private val debet: JComboBox<Account>

    private val credit: JComboBox<Account>

    init {
        layout = GridBagLayout()

        groupPanel("Выберите счета дебета и кредита", 0, 2, 0).apply {

            comboBox("Дебет", 0, AccountService.elemRoot()).apply { debet = this }

            comboBox("Кредит", 1, AccountService.elemRoot()).apply { credit = this }
        }

        groupPanel("", 3, 2, 0).apply {
            onlyButton("Создать", 0, 0, "saveDB"){ createSchema() }

            onlyButton("Отменить", 0, 1, "deleteDB"){ cancel() }
        }

        pack()
    }

    private fun createSchema() {
        resultOk = true

        processShowError {
            SchemaService.createByDebetCredit(
                debet.selectedItem as? Account,
                credit.selectedItem as? Account
            )
        }

        dispose()
    }

    private fun cancel() {

        resultOk = false

        dispose()
    }

    fun showDialogResultOk(): Boolean {
        resultOk = false

        isVisible = true

        return resultOk
    }
}

fun <T> Container.comboBox(label: String, gridY: Int, list: List<T>? = null): JComboBox<T> {

    add( JLabel(label), labelConstraint(gridY) )

    val items = list?.let { Vector<T>(it) }

    val combo = items?.let { JComboBox<T>(it) } ?: JComboBox()

    add(combo, textConstraint(gridY = gridY, gridX = 1) )

    return combo
}