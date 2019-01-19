package ru.barabo.plastic.schema.entity

import ru.barabo.db.annotation.*

@TableName("OD.PTKB_TRANSACT_ACCOUNT_VALUE")
@SelectQuery("""
    select * from OD.PTKB_TRANSACT_ACCOUNT_VALUE v
     where v.TRANSACT_ACCOUNT = ?
       and COALESCE(v.CURRENCY, -1) = COALESCE(?, -1)
       and COALESCE(v.TERMINAL_ID, -1) = COALESCE(?, -1)
       and COALESCE(v.OFFICE, -1) = COALESCE(?, -1)
       and COALESCE(v.CLIENTTYPE, -1) = COALESCE(?, -1)
""")
data class AccountValue(
    @ColumnName("ID")
    @ColumnType(java.sql.Types.BIGINT)
    @SequenceName(SEQ_CLASSIFIED)
    var id: Long? = null
) : ParamsSelect {

    override fun selectParams(): Array<Any?>? = arrayOf(
        Account.selectedAccount?.id?:Int::class.java,
        Currency.selectedCurrency?.code?:Int::class.java,
        Terminal.selectedTerminal?.terminalId?:String::class.java,
        Office.selectedOffice?.id?:Int::class.java,
        ClientType.selectedClientType?.id?:Int::class.java)
}
