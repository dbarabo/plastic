package ru.barabo.plastic.schema.entity

import ru.barabo.db.annotation.*

@TableName("OD.PTKB_TRANSACT_ACCOUNT_VALUE")
@SelectQuery("""select v.id, v.TRANSACT_ACCOUNT, v.CURRENCY, v.TERMINAL_ID, v.OFFICE, v.CLIENTTYPE,
    v.value, ac.description desc_account_val, cl.label int_client_label, v.calc_formula,
    v.depend_account, depend.name depend_account_name,
    v.ext_code, v.ext_bank, bank.label ext_bank_name, v.ext_client, extcl.label ext_client_label
    from OD.PTKB_TRANSACT_ACCOUNT_VALUE v, od.account ac, od.client cl,
        OD.PTKB_TRANSACT_ACCOUNT depend
        od.client bank
     where v.TRANSACT_ACCOUNT = ?
       and COALESCE(v.CURRENCY, -1) = COALESCE(?, -1)
       and COALESCE(v.TERMINAL_ID, -1) = COALESCE(?, -1)
       and COALESCE(v.OFFICE, -1) = COALESCE(?, -1)
       and COALESCE(v.CLIENTTYPE, -1) = COALESCE(?, -1)

       and v.value(+) = ac.classified
       and ac.client(+) = cl.classified

       and v.depend_account(+) = depend.id

       and v.ext_bank(+) = bank.classified
       and v.ext_client(+) = extcl.classified
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
    @ColumnType(java.sql.Types.BIGINT)
    var currency: Long? = null,

    @ColumnName("TERMINAL_ID")
    @ColumnType(java.sql.Types.VARCHAR)
    var terminalId: String? = null,

    @ColumnName("OFFICE")
    @ColumnType(java.sql.Types.BIGINT)
    var office: Long? = null,

    @ColumnName("CLIENTTYPE")
    @ColumnType(java.sql.Types.BIGINT)
    var clientType: Long? = null,

    @ColumnName("value")
    @ColumnType(java.sql.Types.BIGINT)
    var valueAccount: Long? = null,

    @ColumnName("desc_account_val")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var descriptionAccount: String? = null,

    @ColumnName("int_client_label")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var clientAccount: String? = null,

    @ColumnName("calc_formula")
    @ColumnType(java.sql.Types.VARCHAR)
    var calcFormula: String? = null,

    @ColumnName("depend_account")
    @ColumnType(java.sql.Types.BIGINT)
    var dependAccount: Long? = null,

    @ColumnName("depend_account_name")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var dependAccountName: String? = null,

    @ColumnName("ext_code")
    @ColumnType(java.sql.Types.VARCHAR)
    var extCodeAccount: String? = null,

    @ColumnName("ext_bank")
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
    var extClientLabel: String? = null

) : ParamsSelect {

    override fun selectParams(): Array<Any?>? = arrayOf(
        Account.selectedAccount?.id?:Int::class.java,
        Currency.selectedCurrency?.code?:Int::class.java,
        Terminal.selectedTerminal?.terminalId?:String::class.java,
        Office.selectedOffice?.id?:Int::class.java,
        ClientType.selectedClientType?.id?:Int::class.java)
}
