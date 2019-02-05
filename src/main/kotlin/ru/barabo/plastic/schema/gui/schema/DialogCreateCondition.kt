package ru.barabo.plastic.schema.gui.schema

import ru.barabo.plastic.schema.entity.variable.Variable
import ru.barabo.plastic.schema.entity.variable.VariableType
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import ru.barabo.plastic.schema.service.schema.SchemaService
import ru.barabo.plastic.schema.service.schema.TransTypeService
import ru.barabo.plastic.schema.service.variable.VariableService
import java.awt.Component
import javax.swing.JComboBox
import javax.swing.JTextField

open class DialogCreateCondition(component: Component) : AbstractDialog(component, "Создать условие") {

    private val condition: JComboBox<Variable>

    private val variant: JTextField

    init {

        val typeCondition =
            if (TransTypeService.selectedEntity()?.isEquaringType == true) VariableType.PRIOR_CONDITION
            else VariableType.CONDITION

        groupPanel("Выберите условие и задайте 1 вариант значения", 0, 2, 0).apply {

            comboBox("Условие", 0, VariableService.getVarByType(typeCondition)).apply { condition = this }

            textFieldHorizontal("Вариант значения", 1).apply { variant = this }
        }

        createOkCancelButton(2)

        pack()
    }

    override fun okProcess() {
        SchemaService.addCondition(condition.selectedItem as? Variable, variant.text?.trim())
    }
}
