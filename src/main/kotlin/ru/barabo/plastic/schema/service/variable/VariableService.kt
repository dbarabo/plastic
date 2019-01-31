package ru.barabo.plastic.schema.service.variable

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.variable.Variable
import ru.barabo.plastic.schema.entity.variable.VariableType
import ru.barabo.plastic.schema.service.CashFunc

object VariableService : StoreFilterService<Variable>(AfinaOrm, Variable::class.java), CashFunc {

  override val cashedParamsFuncList: MutableMap<String, List<String>> = HashMap()

  fun getVarByType(type: VariableType) = dataList.filter { it.typeVar == type }
}

private fun VariableType.getFuncParams():  List<String> = resultAndParams.map { it.label }