package ru.barabo.plastic.schema.entity.schema

import ru.barabo.db.ConverterValue
import ru.barabo.db.annotation.*
import ru.barabo.plastic.schema.service.schema.HeaderTransactTypeService
import ru.barabo.plastic.unnamed.general.parseLong


@SelectQuery("""
    select t.ID, t.NAME, t.HEADER_NAME, t.TRANS_TYPE, t.IS_OUT_TYPE, s.CONDITION, v.name CONDITION_NAME
      from od.ptkb_plastic_transact_type t,
      od.ptkb_transact_schema s,
      od.ptkb_transact_variable v
     where t.HEADER_NAME = ?
     and coalesce(t.IS_OUT_TYPE, 0) != -1
     and s.TRANSACT_TYPE(+) = t.TRANS_TYPE
     and v.id(+) = s.condition
     and (s.id is null or s.id = (select min(s2.id) from od.ptkb_transact_schema s2 where s2.transact_type = t.TRANS_TYPE))
     order by t.TRANS_TYPE
""")
data class TransType(
    @SequenceName("")
    @ColumnName("ID")
    var id: Long? = null,

    @ColumnName("NAME")
    var name: String? = null,

    @ColumnName("HEADER_NAME")
    var headerName: String? = null,

    @ColumnName("TRANS_TYPE")
    var transactType: String? = null,

    @ColumnName("IS_OUT_TYPE")
    var categoryTransact: Int? = null,

    /*
    @ColumnName("IS_OUT_TYPE")
    @Converter(BooleanAcquiringTypeConverter::class)
    var isEquaringType: Boolean = false,
*/
    @ColumnName("CONDITION")
    var condition: Long? = null,

    @ColumnName("CONDITION_NAME")
    var conditionName: String? = null
) : ParamsSelect {

    var isEquaringType: Boolean?
    get() = (categoryTransact == 2 || categoryTransact == 4)
    set(_ ) {}

    var categoryTransactName: String
    get() {
        return when (categoryTransact) {
            1 -> "Наличка"
            2-> "Безнал Эквайринг"
            3 -> "Безнал"
            4 -> "Возврат Эквайринг"
            5 -> "Возврат"
            -1 -> "В архиве"
            else -> ""
        }
    }
    set(_) {}

    override fun selectParams(): Array<Any?>? = arrayOf(HeaderTransactTypeService.selectedEntity()?.headerName?: String::class.java)
}

object BooleanAcquiringTypeConverter : ConverterValue {

    private val ACQUIRING_LIST = listOf(2, 4)

    override fun convertFromBase(value: Any, javaType: Class<*>): Any? =
        (value as? Number)?.toInt()?.let { it in ACQUIRING_LIST } ?: false

    override fun convertFromStringToJava(value: String, javaType: Class<*>): Any? =
        value.parseLong()?.toInt()?.let { it in ACQUIRING_LIST  } ?: false

    override fun convertToBase(value: Any): Any = if((value as? Boolean) == true)ACQUIRING_LIST[0] else 1
}