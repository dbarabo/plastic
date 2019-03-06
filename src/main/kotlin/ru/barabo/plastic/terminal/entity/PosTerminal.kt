package ru.barabo.plastic.terminal.entity

import ru.barabo.db.ConverterValue
import ru.barabo.db.annotation.*
import ru.barabo.db.converter.BooleanConverter
import ru.barabo.db.converter.SqliteLocalDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@TableName("od.ptkb_poses")
@SelectQuery("""
   select p.CLASSIFIED, p.TERMINALID, p.CLIENT, c.LABEL,
   coalesce(v.account_ext_code, od.accountCode(p.account40)) ACCOUNT_CODE,
   coalesce(replace(od.PTKB_440P.getBankName(v.ext_bank_id), chr(13)||chr(10), ''), 'НАШ БАНК') BANK_NAME,
   p.LOCATION, p.VALIDFROM, p.VALIDTO,
   p.PERCENT_COMMISSION, p.COMPUE_RATE,
   coalesce(tc.transact_local_oper, tc.transact_pc_oper + 7/24) MAX_OPER,
   cr.label RATE_NAME,
   sign(coalesce(p.TERMINAL_OWNER, 0)) TERMINAL_OWNER,
   p.SRC_COMMISSION

from od.ptkb_poses p
   , od.client c
   , od.computerate cr
   , od.ptkb_transact_account_value v
   , od.ptkb_transact_ctl_mtl tc
where c.classified = p.client
  and p.compue_rate = cr.classified(+)
  and v.transact_account = 1
  and p.terminalid = v.terminal_id(+)
  and p.terminalid = tc.terminal_id(+)
  and (tc.id is null or tc.id in (select max(c.id) from od.ptkb_transact_ctl_mtl c where c.terminal_id = tc.terminal_id))
  and (coalesce(p.validto, max_date) > sysdate - 180 or
       coalesce(tc.transact_pc_oper, min_date) > sysdate - 180)
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
    var srcCommission: String = ""
   ) {
    var pactStartFormat: String = ""
        get() = pactStart.formatDate()

    var pactEndFormat: String = ""
        get() = pactEnd.formatDate()

    var lastOperFormat: String = ""
        get() = lastOper.formatDate()
}

private fun LocalDate?.formatDate() = this?.let { DateTimeFormatter.ofPattern("dd.MM.yyyy").format(it) } ?: ""


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