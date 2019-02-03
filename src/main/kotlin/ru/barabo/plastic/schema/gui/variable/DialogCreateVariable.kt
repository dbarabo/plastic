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
import javax.swing.JTextField

class DialogCreateVariable(private var variable: Variable, component: Component) :
    AbstractDialog(component, "Правка/Создание функции/условия") {

    private val nameVar: JTextField

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
                }
            }

            comboBox("Функция", 2, VariableService.getFuncListByType(variable.typeVar)).apply {
                selectedItem = variable.calcFunc

                addActionListener {

                    (selectedItem as? String)?.let { variable.calcFunc = it }
                }
            }
        }

        createOkCancelButton(3)

        pack()
    }

    override fun okProcess() {

        processShowError {
            variable.name = nameVar.text?.trim()

            VariableService.save(variable)
        }
    }
}