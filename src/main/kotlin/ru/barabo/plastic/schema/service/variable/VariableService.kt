package ru.barabo.plastic.schema.service.variable

import ru.barabo.afina.AfinaOrm
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.schema.entity.variable.Variable
import ru.barabo.plastic.schema.entity.variable.VariableType
import ru.barabo.plastic.schema.service.CashFunc

object VariableService : StoreFilterService<Variable>(AfinaOrm, Variable::class.java), CashFunc {

  override val cashedParamsFuncList: MutableMap<String, List<String>> = HashMap()

  fun getVariableById(id: Long) = dataList.firstOrNull { it.id == id }

  /**
   * список уже созданных переменных-вычислиний по их типу
   */
  fun getVarByType(type: VariableType) = dataList.filter { it.typeVar == type }

  /**
   * список доступных функций в пакете по ее типу
   */
  fun getFuncListByType(type: VariableType) = getFuncListByParams(type.resultAndParams)
}

