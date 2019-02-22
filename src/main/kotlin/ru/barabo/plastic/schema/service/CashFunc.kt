package ru.barabo.plastic.schema.service

import ru.barabo.plastic.afina.AfinaQuery

interface CashFunc {

    val cashedParamsFuncList: MutableMap<String, List<String>>

    fun getFuncListByParams(resultAndParams: List<TypeParams>): List<String> {

        val keyString = resultAndParams.toStringLine()

        return cashedParamsFuncList[keyString]?.let { it } ?: addToMap(
            keyString,
            resultAndParams
        )
    }

    private fun addToMap(keyString: String, resultAndParams: List<TypeParams>): List<String> {
        cashedParamsFuncList[keyString] = initTransactCalcFunc(resultAndParams)

        return cashedParamsFuncList[keyString] ?: emptyList()
    }

    private fun initTransactCalcFunc(resultAndParams: List<TypeParams>): List<String> =
        AfinaQuery.select(selectFuncParams(resultAndParams[0], resultAndParams.drop(1) ) ).map { it[0] as String }

    private fun selectFuncParams(result: TypeParams, params: List<TypeParams>): String {

        var ands = ""
        for((index, param) in params.withIndex()) {
            ands += String.format(AND_EXISTS_PARAMS, param.label, index+1)
        }

        return String.format(SELECT_FUNC_ACCOUNT_ANY_PARAMS, result.label, ands, params.size)
    }
}

fun List<TypeParams>.toStringLine() = this.joinToString("-")

const val PACKAGE_FUNC = "PTKB_TRANSACT_FUNC"

private const val AND_EXISTS_PARAMS = """
   and exists (select 1
    from sys.ALL_ARGUMENTS a0
   where a0.PACKAGE_NAME = a.PACKAGE_NAME
     and a0.OWNER = a.owner
     and a0.OBJECT_NAME = a.OBJECT_NAME
     and a0.ARGUMENT_NAME is not null
     and a0.DATA_TYPE = '%s'
     and a0.POSITION = %d
     and a0.in_out = 'IN')
    """

private const val SELECT_FUNC_ACCOUNT_ANY_PARAMS = """
select a.OBJECT_NAME
  from sys.ALL_ARGUMENTS a
 where a.OWNER = 'OD'
   and a.PACKAGE_NAME = '$PACKAGE_FUNC'
   and a.ARGUMENT_NAME is null
   and a.DATA_TYPE = '%s'
   and a.POSITION = 0
   and a.in_out = 'OUT'

   %s

   and not exists (select 1
    from sys.ALL_ARGUMENTS ax
   where ax.PACKAGE_NAME = a.PACKAGE_NAME
     and ax.OWNER = a.owner
     and ax.OBJECT_NAME = a.OBJECT_NAME
     and ax.POSITION > %d)
    """