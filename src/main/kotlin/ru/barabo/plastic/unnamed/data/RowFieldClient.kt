package ru.barabo.plastic.unnamed.data

import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.Type
import ru.barabo.total.db.impl.AbstractRowFields
import ru.barabo.total.db.impl.Field
import java.text.SimpleDateFormat


class RowFieldClient : AbstractRowFields() {

    private fun initFields() =  mutableListOf<FieldItem>(
        Field("id", Type.LONG, 20, 0),
        Field("ФИО клиента", Type.STRING, 150, 1),
        Field("Адрес проживания", Type.STRING, 450, 2),
        Field("Дата рождения", Type.DATE, 20, 3, SimpleDateFormat("dd.MM.yyyy")),
        Field("Паспорт РФ", Type.STRING, 80, 4),
        Field("Телефон", Type.STRING, 70, 5)
    )

    fun birthday() = getFieldByLabel("Дата рождения").valueField

    fun phone() = getFieldByLabel("Телефон").valueField

    override fun createFields(): MutableList<FieldItem> = initFields()
}