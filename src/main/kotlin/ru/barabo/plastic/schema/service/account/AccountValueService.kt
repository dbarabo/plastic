package ru.barabo.plastic.schema.service.account

import ru.barabo.afina.AfinaOrm
import ru.barabo.afina.AfinaQuery
import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.schema.entity.account.AccountValue
import ru.barabo.plastic.schema.service.CashAccountValueByFunc

object AccountValueService : StoreFilterService<AccountValue>(AfinaOrm, AccountValue::class.java),
    CashAccountValueByFunc {

    override val cashedAccountParamsFuncList: MutableMap<String, Long> = HashMap()

    //private val logger = Logger.getLogger(AccountValueService::class.java.name)

    private var isCheckInit: Boolean = false

    init {
        AccountService.addListener(updateAccountValue())

        CurrencyService.addListener(updateAccountValue())

        TerminalService.addListener(updateAccountValue())

        OfficeService.addListener(updateAccountValue())

        ClientTypeService.addListener(updateAccountValue())
    }

    override fun afterSelectInit() {

        if(!isCheckInit) {
            isCheckInit = true
            return
        }

        val account = AccountService.selectedEntity() ?: return

        if((!account.isCalc) || dataList.size < 1) return

        val item = dataList[0]

        val idAccountValue = item.calcFormula?.let { calcValueAccountByFunc(it, item.paramsByCalcFunc()) } ?: return

        if(idAccountValue == 0L) {
            processNullAccountValue(item)
            return
        }

        val accountValues = AfinaQuery.select(SELECT_ACCOUNT, arrayOf(idAccountValue))

        val accountRow = if(accountValues.isNotEmpty()) accountValues[0] else return

        item.code = accountRow[0] as String

        item.labelAccount = accountRow[1] as String

        item.department = accountRow[2] as? String
    }

    private fun processNullAccountValue(item: AccountValue) {
        item.code = "0000000000000000000000"

        item.labelAccount = "Нуль-счет для транзакций без проводок"

        item.department = ""
    }

    private const val SELECT_ACCOUNT = """
        select a.code, a.label, d.label
          from od.account a,
               od.doctree dt,
               od.department d
         where a.doc = ?
           and a.doc = dt.classified(+)
          and dt.operobj = d.classified(+)
"""
}

inline fun <reified T> updateAccountValue(): StoreListener<List<T>> =
    object : StoreListener<List<T>> {
        override fun refreshAll(elemRoot: List<T>, refreshType: EditType) {
            AccountValueService.initData()
        }
    }
