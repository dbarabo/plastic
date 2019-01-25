package ru.barabo.plastic.schema.entity.account

import ru.barabo.db.annotation.*
import ru.barabo.db.converter.BooleanConverter

const val SEQ_CLASSIFIED = "select classified.nextval from dual"

@TableName("OD.PTKB_TRANSACT_ACCOUNT")
@SelectQuery("select * from OD.PTKB_TRANSACT_ACCOUNT order by NAME")
data class Account(
    @ColumnName("ID")
    @ColumnType(java.sql.Types.BIGINT)
    @SequenceName(SEQ_CLASSIFIED)
    var id: Long? = null,

    @ColumnName("NAME")
    @ColumnType(java.sql.Types.VARCHAR)
    var name: String? = null,

    @ColumnName("REMARK")
    @ColumnType(java.sql.Types.VARCHAR)
    var remark: String? = null,

    @ColumnName("check_currency")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(BooleanConverter::class)
    var isCheckCurrency: Boolean = false,

    @ColumnName("check_terminal")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(BooleanConverter::class)
    var isCheckTerminal: Boolean = false,

    @ColumnName("check_bankomat")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(BooleanConverter::class)
    var isCheckBankomat: Boolean = false,

    @ColumnName("check_office")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(BooleanConverter::class)
    var isCheckOffice: Boolean = false,

    @ColumnName("check_clienttype")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(BooleanConverter::class)
    var isCheckClienttype: Boolean = false,

    @ColumnName("is_calc")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(BooleanConverter::class)
    var isCalc: Boolean = false,

    @ColumnName("is_extern_support")
    @ColumnType(java.sql.Types.INTEGER)
    @Converter(BooleanConverter::class)
    var isExternSupport: Boolean = false
)