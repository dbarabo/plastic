package ru.barabo.plastic.schema.entity.account

import ru.barabo.db.annotation.*
import ru.barabo.plastic.schema.service.account.*
import kotlin.reflect.KClass

@TableName("OD.PTKB_TRANSACT_ACCOUNT_VALUE")
@SelectQuery("""
    select v.id, v.TRANSACT_ACCOUNT, v.CURRENCY, v.TERMINAL_ID, v.OFFICE, v.CLIENTTYPE,
    v.ACCOUNT_VALUE, ac.code, ac.label account_label, cl.label int_client_label, v.CALC_FUNC,
    v.depend_account, depend.name depend_account_name,
    v.ACCOUNT_EXT_CODE, v.EXT_BANK_ID, bank.label ext_bank_name, v.ext_client, extcl.label ext_client_label,
    d.label DEP_LABEL
    from OD.PTKB_TRANSACT_ACCOUNT_VALUE v,
         od.account ac, od.client cl,
        OD.PTKB_TRANSACT_ACCOUNT depend,
        od.client bank, od.client extcl,
        od.doctree dt,
        od.department d

     where v.TRANSACT_ACCOUNT = ?
       and COALESCE(v.CURRENCY, -1) = COALESCE(?, COALESCE(v.CURRENCY, -1))
       and COALESCE(v.TERMINAL_ID, '0') = COALESCE(?, COALESCE(v.TERMINAL_ID, '0') )
       and COALESCE(v.OFFICE, -1) = COALESCE(?, COALESCE(v.OFFICE, -1))
       and COALESCE(v.CLIENTTYPE, -1) = COALESCE(?, COALESCE(v.CLIENTTYPE, -1))

       and v.ACCOUNT_VALUE = ac.classified(+)
       and ac.client = cl.classified(+)

       and v.depend_account = depend.id(+)

       and v.EXT_BANK_ID = bank.classified(+)
       and v.ext_client = extcl.classified(+)

       and ac.doc = dt.classified(+)
       and dt.operobj = d.classified(+)
""")
data class AccountValue(
    @ColumnName("ID")
    @ColumnType(java.sql.Types.BIGINT)
    @SequenceName(SEQ_CLASSIFIED)
    var id: Long? = null,

    @ColumnName("TRANSACT_ACCOUNT")
    @ColumnType(java.sql.Types.BIGINT)
    var transactAccount: Long? = null,

    @ColumnName("CURRENCY")
    @ColumnType(java.sql.Types.NUMERIC)
    var currency: Int? = null,

    @ColumnName("TERMINAL_ID")
    @ColumnType(java.sql.Types.VARCHAR)
    var terminalId: String? = null,

    @ColumnName("OFFICE")
    @ColumnType(java.sql.Types.BIGINT)
    var office: Long? = null,

    @ColumnName("CLIENTTYPE")
    @ColumnType(java.sql.Types.BIGINT)
    var clientType: Long? = null,

    @ColumnName("ACCOUNT_VALUE")
    @ColumnType(java.sql.Types.BIGINT)
    var valueAccount: Long? = null,

    @ColumnName("code")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var code: String? = null,

    @ColumnName("account_label")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var labelAccount: String? = null,

    @ColumnName("int_client_label")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var clientAccount: String? = null,

    @ColumnName("CALC_FUNC")
    @ColumnType(java.sql.Types.VARCHAR)
    var calcFormula: String? = null,

    @ColumnName("depend_account")
    @ColumnType(java.sql.Types.BIGINT)
    var dependAccount: Long? = null,

    @ColumnName("depend_account_name")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var dependAccountName: String? = null,

    @ColumnName("ACCOUNT_EXT_CODE")
    @ColumnType(java.sql.Types.VARCHAR)
    var extCodeAccount: String? = null,

    @ColumnName("EXT_BANK_ID")
    @ColumnType(java.sql.Types.BIGINT)
    var extBankId: Long? = null,

    @ColumnName("ext_bank_name")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var extBankName: String? = null,

    @ColumnName("ext_client")
    @ColumnType(java.sql.Types.BIGINT)
    var extClient: Long? = null,

    @ColumnName("ext_client_label")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var extClientLabel: String? = null,

    @ColumnName("DEP_LABEL")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var department: String? = null

) : ParamsSelect {
    override fun selectParams(): Array<Any?>? = paramsInSelect()

    private fun paramsInSelect(): Array<Any?>? {
        val account = AccountService.selectedEntity() ?: return null

        return if(account.isCalc) arrayOf(account.id,
            Int::class.javaObjectType,
            "",
            Long::class.javaObjectType,
            Long::class.javaObjectType)
        else paramsByCheck()
    }

    private fun paramsByCheck(): Array<Any?> {
        val account = AccountService.selectedEntity()!!

        return arrayOf(account.id!!,
            ifCheckConditionClass(account.isCheckCurrency, CurrencyService.selectedEntity()?.code, Int::class),
            ifCheckConditionClass(account.isCheckTerminal || account.isCheckBankomat,
                TerminalService.selectedEntity()?.terminalId?:"", String::class),

            ifCheckConditionClass(account.isCheckOffice, OfficeService.selectedEntity()?.id, Long::class),
            ifCheckConditionClass(account.isCheckClienttype, ClientTypeService.selectedEntity()?.id, Long::class)
        )
    }

    internal fun paramsByCalcFunc(): Array<Any?> {

        val account = AccountService.selectedEntity()!!

        val list = ArrayList<Any>()

        if (account.isCheckCurrency) list +=
            ifCheckConditionClass(true, CurrencyService.selectedEntity()?.code, Int::class)

        if (account.isCheckBankomat || account.isCheckTerminal) list +=
            ifCheckConditionClass(true,TerminalService.selectedEntity()?.terminalId?:"", String::class)

        if (account.isCheckOffice) list +=
            ifCheckConditionClass(true, OfficeService.selectedEntity()?.id, Long::class)

        if (account.isCheckClienttype) list +=
            ifCheckConditionClass(true, ClientTypeService.selectedEntity()?.id, Long::class)

        if (list.isEmpty()) list += Long::class.javaObjectType

        return list.toArray()
    }

    private fun ifCheckConditionClass(isCondition: Boolean, trueResult: Any?, nullClass: KClass<*>): Any =
        if(isCondition && trueResult != null) trueResult else nullClass.javaObjectType

}
