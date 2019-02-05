package ru.barabo.plastic.schema.gui.variable

import ru.barabo.plastic.schema.entity.variable.Variable
import ru.barabo.plastic.schema.entity.variable.VariableType
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import ru.barabo.plastic.schema.gui.schema.comboBox
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import ru.barabo.plastic.schema.service.variable.VariableService
import java.awt.Component
import javax.swing.JComboBox
import javax.swing.JTextField

class DialogCreateVariable(private var variable: Variable, component: Component) :
    AbstractDialog(component, "Правка/Создание функции/условия") {

    private val nameVar: JTextField

    private val funcList: JComboBox<String>

    init {

        groupPanel("Задайте значения для создаваемой функции", 0, 2, 0).apply {

            textFieldHorizontal("Имя функции/условия", 0).apply {
                nameVar = this

                text = variable.name
            }

            comboBox("Тип функции", 1, VariableType.values().toList()).apply {

                selectedItem = variable.typeVar

                addActionListener {

                    (selectedItem as? VariableType)?.let { variable.typeVar = it }

                    updateFuncList()
                }
            }

            comboBox("Функция", 2, VariableService.getFuncListByType(variable.typeVar)).apply {
                funcList = this

                selectedItem = variable.calcFunc

                addActionListener {

                    (selectedItem as? String)?.let { variable.calcFunc = it }
                }
            }
        }

        createOkCancelButton(3)

        pack()
    }

    private fun updateFuncList() {
        val list = VariableService.getFuncListByType(variable.typeVar)

        funcList.removeAllItems()

        list.forEach { funcList.addItem(it) }
    }

    override fun okProcess() {

        processShowError {
            variable.name = nameVar.text?.trim()

            VariableService.save(variable)
        }
    }
}