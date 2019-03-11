package ru.barabo.plastic.schema.gui.schema

import ru.barabo.plastic.schema.entity.account.Account
import ru.barabo.plastic.schema.entity.schema.Schema
import ru.barabo.plastic.schema.entity.variable.Variable
import ru.barabo.plastic.schema.entity.variable.VariableType
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.account.labelConstraint
import ru.barabo.plastic.schema.gui.account.textConstraint
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import ru.barabo.plastic.schema.service.account.AccountService
import ru.barabo.plastic.schema.service.schema.SchemaService
import ru.barabo.plastic.schema.service.variable.VariableService
import java.awt.Component
import java.awt.Container
import java.util.*
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JTextField

class DialogCreateSchema(private val schema: Schema, component: Component) :
    AbstractDialog(component, "Создание/Правка схемы проводки") {

    private val variant: JTextField

    init {
         groupPanel("Выберите счета дебета и кредита", 0, 2, 0).apply {

            comboBox("Дебет", 0, AccountService.elemRoot()).apply {

                schema.debetAccount?.let {
                    selectedItem = AccountService.getAccountById(it)
                }

                addActionListener {
                    schema.debetAccount = (selectedItem as? Account)?.id
                }
             }

            comboBox("Кредит", 1, AccountService.elemRoot()).apply {

                schema.creditAccount?.let {
                    selectedItem = AccountService.getAccountById(it)
                }

                addActionListener {
                    schema.creditAccount = (selectedItem as? Account)?.id
                }
            }

            textFieldHorizontal("Вариант условия", 3).apply {variant = this}

            comboBox("Рассчет.сумма:", 4,
                VariableService.getVarByType(VariableType.AMOUNT_VAR).addEmptyVal()).apply {

                schema.calcAmount?.let {
                    selectedItem = VariableService.getVariableById(it)
                }

                addActionListener {
                    schema.calcAmount = (selectedItem as? Variable)?.id
                }
            }

            comboBox("Рассчет.назначение:", 5,
                 VariableService.getVarByType(VariableType.DESCRIPTION_VAR).addEmptyVal()).apply {

                schema.calcDescription?.let {
                    selectedItem = VariableService.getVariableById(it)
                }

                addActionListener {
                     schema.calcDescription = (selectedItem as? Variable)?.id
                }
            }
        }

        createOkCancelButton(6)

        packWithLocation()
    }

    override fun okProcess() {

        if(!variant.text?.trim().isNullOrEmpty()) {
            schema.conditionVariant = variant.text?.trim()
        }
        SchemaService.save(schema)
    }
}

inline fun <reified T> List<T>.addEmptyVal(): List<T> {
    val mutableList = ArrayList<T>()

    val empty = T::class.java.newInstance()

    mutableList.add(empty)

    mutableList.addAll(this)

    return mutableList
}

fun <T> Container.comboBox(label: String, gridY: Int, list: List<T>? = null): JComboBox<T> {

    add( JLabel(label), labelConstraint(gridY) )

    val items = list?.let { Vector<T>(it) }

    val combo = items?.let { JComboBox<T>(it) } ?: JComboBox()

    add(combo, textConstraint(gridY = gridY, gridX = 1) )

    return combo
}