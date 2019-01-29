package ru.barabo.plastic.schema.gui.variable

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.schema.entity.variable.Variable
import ru.barabo.plastic.schema.service.variable.VariableService

object TableVariable : EntityTable<Variable>(columns, VariableService)

private val columns = listOf(
    ColumnTableModel("Имя", 50, Variable::name, true),
    ColumnTableModel("Функция", 50, Variable::calcFunc, true),
    ColumnTableModel("Тип", 50, Variable::typeVar, true)
)