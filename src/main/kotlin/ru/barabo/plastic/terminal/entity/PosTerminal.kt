package ru.barabo.plastic.terminal.entity

import ru.barabo.db.ConverterValue
import ru.barabo.db.annotation.*
import ru.barabo.db.converter.BooleanConverter
import ru.barabo.db.converter.SqliteLocalDate
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@TableName("od.ptkb_poses")
@SelectQuery("""
WITH ACQ as (
select qr.terminal_id, max(qr.local_oper) pc_oper, coalesce(max(nullif(qr.merchant_category_code, '5999')), '5999') MERCHANT_CODE
  from od.ptkb_acq_record qr
group by qr.terminal_id
),
TURN as (
select t.ID_CLIENT, t.MONTH, trunc(t.TURN_EQUIRING/1000) TURN_EQUIRING, t.TURN_REVERSE TURN_REVERSE
  from od.PTKB_TERMINAL_TURN_MONTH t
  where t.MONTH in (select max(t2.MONTH)
                     from od.PTKB_TERMINAL_TURN_MONTH t2
                    where t2.id_client = t.ID_CLIENT)
                    
),
TURN_POS as (
  select sum(t.turn_equiring) last_sum, t.terminal_id
    from od.ptkb_pos_turn_month_byterminal t
where t.month = (
   select max(month) 
     from od.ptkb_pos_turn_month_byterminal tt
    where tt.terminal_id = t.terminal_id )
group by t.terminal_id
)
select p.CLASSIFIED, p.terminalid, p.INVENTORY_NUMBER, p.CLIENT, cl.LABEL,
       coalesce(v.account_ext_code, od.accountCode(v.ACCOUNT_VALUE)) ACCOUNT_CODE,
       case when v.ext_bank_id is null then 'НАШ БАНК'
       else od.getclienttrmsgattr(v.ext_bank_id, 2, 1)
       end BANK_NAME,
       p.LOCATION, p.VALIDFROM, p.VALIDTO,
       p.PERCENT_COMMISSION, p.COMPUE_RATE,
       ACQ.pc_oper MAX_OPER,
       cr.label RATE_NAME,
       sign(coalesce(p.TERMINAL_OWNER, 0)) TERMINAL_OWNER,
       p.SRC_COMMISSION,
       TURN.TURN_EQUIRING,
       TURN.TURN_REVERSE,
       TURN.MONTH,
       ACQ.MERCHANT_CODE,
       TURN_POS.LAST_SUM,
       p.MERCHANT_ID
  from od.ptkb_poses p
  left join ACQ on ACQ.terminal_id = p.terminalid 
  join od.client cl on cl.classified = p.client
  left join od.ptkb_transact_account_value v on v.terminal_id = p.terminalid and v.transact_account = 1
  left join od.computerate cr on cr.classified = p.compue_rate
  left join TURN on TURN.ID_CLIENT = p.client
  left join TURN_POS on TURN_POS.terminal_id = p.terminalid
 where ( coalesce(p.validto, max_date) > sysdate - 180 or
         coalesce(ACQ.pc_oper, min_date) > sysdate - 180 )
        
order by p.validfrom desc, p.CLASSIFIED desc
""")
data class PosTerminal(
    @ColumnName("CLASSIFIED")
    @ColumnType(java.sql.Types.BIGINT)
    @SequenceName("")
    var id: Long? = null,

    @ColumnName("TERMINALID")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var terminal: String = "",

    @ColumnName("INVENTORY_NUMBER")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var serialNumber: String = "",

    @ColumnName("CLIENT")
    @ColumnType(java.sql.Types.BIGINT)
    @ReadOnly
    var clientId: Long? = null,

    @ColumnName("LABEL")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var clientName: String = "",

    @ColumnName("ACCOUNT_CODE")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var accountCode: String = "",

    @ColumnName("BANK_NAME")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var bankName: String = "",

    @ColumnName("LOCATION")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var addressTerminal: String = "",

    @ColumnName("VALIDFROM")
    @ColumnType(java.sql.Types.DATE)
    @Converter(SqliteLocalDate::class)
    var pactStart: LocalDate? = null,

    @ColumnName("VALIDTO")
    @ColumnType(java.sql.Types.DATE)
    @Converter(SqliteLocalDate::class)
    var pactEnd: LocalDate? = null,

    @ColumnName("PERCENT_COMMISSION")
    @ColumnType(java.sql.Types.DECIMAL)
    var percentCommission: Double? = null,

    @ColumnName("COMPUE_RATE")
    @ColumnType(java.sql.Types.BIGINT)
    var percentId: Long? = null,

    @ColumnName("MAX_OPER")
    @ColumnType(java.sql.Types.DATE)
    @Converter(SqliteLocalDate::class)
    @ReadOnly
    var lastOper: LocalDate? = null,

    @ColumnName("RATE_NAME")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var rateName: String = "",

    @ColumnName("TERMINAL_OWNER")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(BooleanConverter::class)
    @ReadOnly
    var isOwnerTerminal: Boolean = false,

    @ColumnName("SRC_COMMISSION")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(SrcCommissionConverter::class)
    @ReadOnly
    var srcCommission: String = "",

    @ColumnName("TURN_EQUIRING")
    @ColumnType(java.sql.Types.INTEGER)
    @ReadOnly
    var turnAmount: Int = 0,

    @ColumnName("TURN_REVERSE")
    @ColumnType(java.sql.Types.INTEGER)
    @ReadOnly
    var turnReverse: Double = 0.0,

    @ColumnName("MONTH")
    @ColumnType(java.sql.Types.DATE)
    @Converter(SqliteLocalDate::class)
    @ReadOnly
    var monthTurn: LocalDate? = null,

    @ColumnName("MERCHANT_CODE")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var merchantCode: String = "",

    @ColumnName("LAST_SUM")
    @ColumnType(java.sql.Types.INTEGER)
    @ReadOnly
    var turnTerminal: Int = 0,

    @ColumnName("MERCHANT_ID")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var merchantId: String = ""
   ) {
    var pactStartFormat: String = ""
        get() = pactStart.formatDate()

    var pactEndFormat: String = ""
        get() = pactEnd.formatDate()

    var lastOperFormat: String = ""
        get() = lastOper.formatDate()

    var turnMonthInfo: String
    get() = monthTurn?.let { "${turnAmount.formated()} за ${it.shortFormatDate()}" } ?:""
    set(_) {}

    var typeTerminal: String
    get() = if(terminal.indexOf("J") == 0)"Карты" else "weechat"
    set(_) {}

    var turnTerminalInfo: String
    get() = turnTerminal.formated()
    set(_) {}
}

private fun LocalDate?.formatDate() = this?.let { DateTimeFormatter.ofPattern("dd.MM.yyyy").format(it) } ?: ""

private fun LocalDate.shortFormatDate() = DateTimeFormatter.ofPattern("dd.MM.yy").format(this)

private fun Number?.formated() = this?.let { DecimalFormat("0,000").format(it) } ?: ""


object SrcCommissionConverter : ConverterValue {
    override fun convertFromBase(value: Any, javaType: Class<*>): Any? =
        when((value as? Number)?.toInt()) {
        0 -> "С 30232"
        1 -> "С 40702"
        else -> "хз что это"
    }

    override fun convertFromStringToJava(value: String, javaType: Class<*>): Any? =
        when(value) {
        "0" -> "С 30232"
        "1" -> "С 40702"
        else -> "хз что это"
    }

    override fun convertToBase(value: Any): Any =
        when(value as? String) {
        "С 40702" -> 1
            else -> 0
        }
}