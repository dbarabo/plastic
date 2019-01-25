package ru.barabo.plastic.schema.service.account

import org.apache.log4j.Logger
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.schema.entity.account.Account

object AccountService : StoreFilterService<Account>(AfinaOrm, Account::class.java) {

    private val logger = Logger.getLogger(AccountService::class.java.name)

    private val transactCalcFuncList = HashMap<String, List<String>>()

    fun getCalcFuncByChecked(): List<String>{
        val account = AccountService.selectedEntity() ?: return emptyList()

        if(!account.isCalc) return emptyList()

        val params = account.getParamCountFunc()

        val keyString = params.toStringLine()

        return transactCalcFuncList[keyString]?.let { it } ?: addToMap(
            keyString,
            params
        )
    }

    private fun addToMap(keyString: String, params: List<TypeParams>): List<String> {
        transactCalcFuncList[keyString] =
            initTransactCalcFunc(params)

        return transactCalcFuncList[keyString] ?: emptyList()
    }


    private fun List<TypeParams>.toStringLine() = this.joinToString("-")

    private fun Account.getParamCountFunc(): List<TypeParams> {

        val list = ArrayList<TypeParams>()

        if(isCheckCurrency) list += TypeParams.VARCHAR2

        if(isCheckBankomat || isCheckTerminal) list += TypeParams.VARCHAR2

        if(isCheckOffice) list += TypeParams.NUMBER

        if(isCheckClienttype) list += TypeParams.NUMBER

        if(list.isEmpty()) list += TypeParams.NUMBER

        return list
    }

    private fun initTransactCalcFunc(params: List<TypeParams>): List<String> = AfinaQuery.select(
        selectFuncParams(params)
    ).map { it[0] as String }

    private const val PACKAGE_FUNC = "PTKB_PLASTIC_TURNOUT"

    private fun selectFuncParams(params: List<TypeParams>): String {

        var ands = ""
        for((index, param) in params.withIndex()) {
            ands += String.format(AND_EXISTS_PARAMS, param.label, index+1)
        }


        return String.format(SELECT_FUNC_ACCOUNT_ANY_PARAMS, ands, params.size).apply { logger.error(this) }
    }

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
   and a.DATA_TYPE = 'NUMBER'
   and a.POSITION = 0
   and a.in_out = 'OUT'

   %s

   and not exists (select 1
    from sys.ALL_ARGUMENTS ax
   where ax.PACKAGE_NAME = a.object_name
     and ax.OWNER = a.owner
     and ax.OBJECT_NAME = a.OBJECT_NAME
     and ax.POSITION > %d)
    """
}

private enum class TypeParams(val label: String) {
    NUMBER("NUMBER"),
    VARCHAR2("VARCHAR2")
}