package ru.barabo.plastic.unnamed.data

import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.Type
import ru.barabo.total.db.impl.AbstractRowFields
import ru.barabo.total.db.impl.Field
import ru.barabo.total.db.impl.formatter.CardFormat
import java.text.SimpleDateFormat

class RowFieldOutClient: AbstractRowFields() {

    private fun fields() =
        mutableListOf<FieldItem>(
            Field("#id", true, Type.LONG, null, "ID", 1, 0, true),
            Field("#PLASTIC_PACK", false, Type.LONG, null, "PLASTIC_PACK", 1, 1, true),
            Field("Держатель", true, Type.STRING, null, null, 250, 2, true),
            Field("Телефон-СМС", true, Type.STRING, null, null, 130, 3, true),
            Field("Тип карты", true, Type.STRING, null, null, 170, 4, true),
            Field("№ карты", true, Type.STRING, null, null, 150, 5, true, null,  CardFormat()),
            Field("Выдана", true, Type.DATE, null, null, 80, 6, true, SimpleDateFormat("dd.MM.yyyy")),
            Field("Истекает", true, Type.DATE, null, "CREATED", 80, 7, true, SimpleDateFormat("dd.MM.yyyy")),
            Field("Автор", true, Type.STRING, null, "CREATOR", 70, 8, true)
        )

    override fun createFields(): MutableList<FieldItem> = fields()
}