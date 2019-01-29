package ru.barabo.plastic.schema.service.variable

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.variable.Variable
import ru.barabo.plastic.schema.entity.variable.VariableType

object VariableService : StoreFilterService<Variable>(AfinaOrm, Variable::class.java) {

  fun getVarByType(type: VariableType) = dataList.filter { it.typeVar == type }
}