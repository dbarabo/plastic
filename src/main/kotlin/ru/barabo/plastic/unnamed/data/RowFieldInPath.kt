package ru.barabo.plastic.unnamed.data

import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.Type
import ru.barabo.total.db.impl.AbstractRowFields
import ru.barabo.total.db.impl.Field
import ru.barabo.total.db.impl.formatter.CardFormat
import java.text.SimpleDateFormat

class RowFieldInPath : AbstractRowFields() {

    private val fields = fields()

    private fun fields() =
        mutableListOf<FieldItem>(
        Field("#id", true, Type.LONG, null, "ID", 1, 0, true),
        Field("#PLASTIC_PACK", false, Type.LONG, null, "PLASTIC_PACK", 1, 1, true),
        Field("Статус", true, Type.LONG, statePlasticPacketLabels(), "STATE", 90, 2, true, statePlasticPacketIndexes() ),
        Field("Заказана", true, Type.DATE, null, "CREATED", 80, 3, true, SimpleDateFormat("dd.MM.yyyy") ),
        Field("Тип карты", true, Type.STRING, null, null, 170, 4, true),
        Field("№ карта", true, Type.STRING, null, null, 150, 5, true, null,  CardFormat()),
        Field("Истекает", true, Type.DATE, null, "CREATED", 80, 6, true, SimpleDateFormat("dd.MM.yyyy")),
        Field("Автор", true, Type.STRING, null, "CREATOR", 70, 7, true)
    )

    override fun createFields(): MutableList<FieldItem> = fields?:fields()
}

fun statePlasticPacketLabels() = StatePlasticPacket.values().map { it.label }.toTypedArray()

fun statePlasticPacketIndexes() =  StatePlasticPacket.values().map {it.ordinal}.toIntArray()