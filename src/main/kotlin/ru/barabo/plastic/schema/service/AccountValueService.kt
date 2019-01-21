package ru.barabo.plastic.schema.service

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.schema.entity.AccountValue

object AccountValueService : StoreFilterService<AccountValue>(AfinaOrm, AccountValue::class.java) {

    val calcFuncList = initCalcFunc()

    init {
        AccountService.addListener( updateAccountValue())

        CurrencyService.addListener( updateAccountValue())

        TerminalService.addListener( updateAccountValue())

        OfficeService.addListener( updateAccountValue())

        ClientTypeService.addListener( updateAccountValue())
    }

    private fun initCalcFunc(): List<String>  = AfinaQuery.select(SELECT_FUNC_CALC_ACCOUNT).map { it[0] as String }

    private const val PACKAGE_FUNC = "PTKB_PLASTIC_TURNOUT"

    private const val SELECT_FUNC_CALC_ACCOUNT = """
select a.OBJECT_NAME
  from sys.ALL_ARGUMENTS a
 where a.OWNER = 'OD'
   and a.PACKAGE_NAME = '$PACKAGE_FUNC'
   and a.ARGUMENT_NAME is null
   and a.DATA_TYPE = 'NUMBER'
   and a.POSITION = 0
   and a.in_out = 'OUT'

   and exists (select 1
    from sys.ALL_ARGUMENTS a0
   where a0.PACKAGE_NAME = a.PACKAGE_NAME
     and a0.OWNER = a.owner
     and a0.OBJECT_NAME = a.OBJECT_NAME
     and a0.ARGUMENT_NAME is not null
     and a0.DATA_TYPE = 'NUMBER'
     and a0.POSITION = 1
     and a0.in_out = 'IN')

   and not exists (select 1
    from sys.ALL_ARGUMENTS ax
   where ax.PACKAGE_NAME = a.object_name
     and ax.OWNER = a.owner
     and ax.OBJECT_NAME = a.OBJECT_NAME
     and ax.POSITION > 1)
    """
}

inline fun <reified T> updateAccountValue(crossinline updater: ()->Unit = {AccountValueService.initData()} ): StoreListener<List<T>> =
    object : StoreListener<List<T>> {
        override fun refreshAll(elemRoot: List<T>, refreshType: EditType) {
            AccountValueService.initData()
        }
    }
