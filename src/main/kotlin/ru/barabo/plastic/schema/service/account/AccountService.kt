package ru.barabo.plastic.schema.service.account

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.account.Account
import ru.barabo.plastic.schema.service.CashFunc
import ru.barabo.plastic.schema.service.TypeParams

object AccountService : StoreFilterService<Account>(AfinaOrm, Account::class.java), CashFunc {

    override val cashedParamsFuncList: MutableMap<String, List<String>> = HashMap<String, List<String>>()

    fun getAccountById(id: Long) = dataList.firstOrNull { it.id == id }

    fun getCalcFuncByChecked(): List<String> {
        val account = AccountService.selectedEntity() ?: return emptyList()

        if (!account.isCalc) return emptyList()

        val params = account.getResultAndParamCountFunc()

        return getFuncListByParams(params)
    }

    private fun Account.getResultAndParamCountFunc(): List<TypeParams> {

        val list = ArrayList<TypeParams>()

        list += TypeParams.NUMBER

        if (isCheckCurrency) list += TypeParams.VARCHAR2

        if (isCheckBankomat || isCheckTerminal) list += TypeParams.VARCHAR2

        if (isCheckOffice) list += TypeParams.NUMBER

        if (isCheckClienttype) list += TypeParams.NUMBER

        if (list.size == 1) list += TypeParams.NUMBER

        return list
    }
}

