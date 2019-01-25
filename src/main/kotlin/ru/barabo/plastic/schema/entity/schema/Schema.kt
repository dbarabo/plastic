package ru.barabo.plastic.schema.entity.schema

import ru.barabo.db.ConverterValue
import ru.barabo.db.annotation.*
import ru.barabo.db.converter.BooleanConverter
import ru.barabo.plastic.schema.entity.account.SEQ_CLASSIFIED
import ru.barabo.plastic.schema.service.schema.TransTypeService
import ru.barabo.plastic.unnamed.general.parseLong

@TableName("OD.PTKB_TRANSACT_SCHEMA")
@SelectQuery("""
select s.id, s.transact_type, s.debet_account, s.credit_account, ad.name debet_name, ac.name credit_name,
s.INDICATOR_DEBET, s.reverse_indicator,s.DOCUMENT_TYPE,
s.calc_amount, s.calc_description, va.name amount_name, vd.name description_name,
s.condition, s.CONDITION_VARIANT,
s.row_order
from od.ptkb_transact_schema s
   , od.ptkb_transact_account ad
   , od.ptkb_transact_account ac
   , od.PTKB_TRANSACT_VARIABLE va
   , od.PTKB_TRANSACT_VARIABLE vd
where s.debet_account = ad.id
  and s.credit_account = ac.id
  and s.transact_type = ?
  and (s.CONDITION_VARIANT is null or s.CONDITION_VARIANT = ?)

  and s.calc_amount = va.id(+)
  and s.calc_description = vd.id(+)
order by s.row_order
""")
data class Schema(
    @ColumnName("id")
    @SequenceName(SEQ_CLASSIFIED)
    @ColumnType(java.sql.Types.BIGINT)
    var id: Long? = null,

    @ColumnName("transact_type")
    @ColumnType(java.sql.Types.VARCHAR)
    var transType: String? = null,

    @ColumnName("debet_account")
    @ColumnType(java.sql.Types.BIGINT)
    var debetAccount: Long? = null,

    @ColumnName("credit_account")
    @ColumnType(java.sql.Types.BIGINT)
    var creditAccount: Long? = null,

    @ColumnName("debet_name")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var debetName: String? = null,

    @ColumnName("credit_name")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var creditName: String? = null,

    @ColumnName("INDICATOR_DEBET")
    @Converter(BooleanDebetIndicatorConverter::class)
    @ColumnType(java.sql.Types.VARCHAR)
    var isDebetIndicator: Boolean = true,

    @ColumnName("reverse_indicator")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(ReverseIndicatorConverter::class)
    var reverseIndicator: String? = "Ошибка индикатора",

    @ColumnName("DOCUMENT_TYPE")
    @Converter(BooleanConverter::class)
    @ColumnType(java.sql.Types.INTEGER)
    var isInternalTransfer: Boolean = true,

    @ColumnName("calc_amount")
    @ColumnType(java.sql.Types.BIGINT)
    var calcAmount: Long? = null,

    @ColumnName("calc_description")
    @ColumnType(java.sql.Types.BIGINT)
    var calcDescription: Long? = null,

    @ColumnName("amount_name")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var amountName: String? = null,

    @ColumnName("description_name")
    @ColumnType(java.sql.Types.VARCHAR)
    @ReadOnly
    var descriptionName: String? = null,

    @ColumnName("condition")
    @ColumnType(java.sql.Types.BIGINT)
    var condition: Long? = null,

    @ColumnName("CONDITION_VARIANT")
    @ColumnType(java.sql.Types.VARCHAR)
    var conditionVariant: String? = null,

    @ColumnName("row_order")
    @ColumnType(java.sql.Types.INTEGER)
    var rowOrder: Int? = 1
) : ParamsSelect {

    override fun selectParams(): Array<Any?>? = arrayOf(
        TransTypeService.selectedEntity()?.transactType?: String::class.javaObjectType,
        String::class.javaObjectType)

}

object ReverseIndicatorConverter : ConverterValue {
    override fun convertFromBase(value: Any, javaType: Class<*>): Any? =
        (value as? Number)?.toInt()?.let { intToString(it) }

    override fun convertFromStringToJava(value: String, javaType: Class<*>): Any? =
        value.parseLong()?.toInt()?.let { intToString(it) }

    override fun convertToBase(value: Any): Any = (value as? String)?.let { stringToInt(it) } ?: "Ахтунг!!!"

    private fun intToString(value: Int) =
        when (value) {
            0 -> "Ошибка индикатора"
            1 -> "Реверс дебет<->кредит"
            2 -> "Не меняет дебет с кредит"
            else -> "Ахтунг!!!"
        }

    private fun stringToInt(value: String) =
        when (value) {
            "Ошибка индикатора" -> 0
            "Реверс дебет<->кредит" -> 1
            "Не меняет дебет с кредит" -> 2
            else -> null
        }
}

object BooleanDebetIndicatorConverter : ConverterValue {
    override fun convertFromBase(value: Any, javaType: Class<*>): Any? = (value as? String) == "D"

    override fun convertFromStringToJava(value: String, javaType: Class<*>): Any? = value == "D"

    override fun convertToBase(value: Any): Any = (value as? Boolean)?.let { if(it) "D" else "C" } ?: "D"
}