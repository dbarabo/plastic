package ru.barabo.plastic.schema.gui.variable

import ru.barabo.plastic.schema.entity.variable.Variable
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.schema.service.variable.VariableService
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import java.lang.Exception
import javax.swing.JTable

class ToolBarVariable(table: JTable) : AbstractTopToolBar(table) {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Создать функцию", { createVariable() },	null),
        ButtonKarkas("application", "Правка функции", { editVariable() },	null),
        ButtonKarkas(null, null, null, null)
    )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    init {
        initButton()
    }

    private fun createVariable() {
        DialogCreateVariable(Variable(),this).showDialogResultOk()
    }

    private fun editVariable() {

        processShowError {
            val editVar = VariableService.selectedEntity() ?: throw Exception("Не выбрана текущая функция для правки")

            DialogCreateVariable(editVar,this).showDialogResultOk()
        }
    }
}