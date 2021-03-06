package ru.barabo.plastic.unnamed.data

import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.Type
import ru.barabo.total.db.impl.AbstractRowFields
import ru.barabo.total.db.impl.Field
import ru.barabo.total.db.impl.formatter.CardFormat
import java.text.SimpleDateFormat

class RowFieldInPath : AbstractRowFields() {

    private fun fields() =
        mutableListOf<FieldItem>(
        Field("#id", true, Type.LONG, null, "ID", 40, 0, true),
        Field("#PLASTIC_PACK", false, Type.LONG, null, "PLASTIC_PACK", 1, 1, true),
        Field("Статус", true, Type.LONG, statePlasticPacketLabels(), "STATE", 90, 2, true, statePlasticPacketIndexes() ),
        Field("Заказана", true, Type.DATE, null, "CREATED", 50, 3, true, SimpleDateFormat("dd.MM.yyyy") ),
        Field("Тип карты", true, Type.STRING, null, null, 170, 4, true),
        Field("№ карты", true, Type.STRING, null, null, 150, 5, true, null,  CardFormat()),
        Field("Истекает", true, Type.DATE, null, "CREATED", 50, 6, true, SimpleDateFormat("dd.MM.yyyy")),
        Field("Автор", true, Type.STRING, null, "CREATOR", 50, 7, true),
        Field("Счет", true, Type.STRING, null, null, 90, 8, true),
        Field("#APP_CARD", false, Type.LONG, null, "APP_CARD", 1, 9, true),
        Field("#CLASS_CARD", false, Type.LONG, null, null, 1, 10, true),
        Field("#PERSON_ID", false, Type.LONG, null, null, 1, 11, true),
        Field("#IS_UNNAMED_PERSON", false, Type.LONG, null, null, 1, 12, true)
    )

    override fun createFields(): MutableList<FieldItem> = fields()

    fun getApplicationId(): Number? = getFieldByLabel("#APP_CARD").`val` as? Number

    fun getCardNumber(): String? = getFieldByLabel("№ карты").valueField

    fun getTypeCard(): String? = getFieldByLabel("Тип карты").valueField

    fun getPacketId(): Number? = getFieldByLabel("#PLASTIC_PACK").`val` as? Number

    fun getClassCard(): Int = (getFieldByLabel("#CLASS_CARD").`val` as? Number)?.toInt() ?: 0

    fun getState(): Int? = (getFieldByLabel("Статус").`val` as? Number)?.toInt()

    fun getPersonId(): Long? = (getFieldByLabel("#PERSON_ID").`val` as? Number)?.toLong()

    fun isUnnamedPerson(): Boolean = (getFieldByLabel("#IS_UNNAMED_PERSON").`val` as? Number)?.toInt() != 0
}

fun statePlasticPacketLabels() = StatePlasticPacket.values().map { it.label }.toTypedArray()

fun statePlasticPacketIndexes() =  StatePlasticPacket.values().map {it.dbValue}.toTypedArray<Int>();// toIntArray()