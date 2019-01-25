package ru.barabo.plastic.schema.gui.schema

import ru.barabo.plastic.schema.service.schema.SchemaService
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JTable

class ToolBarSchema(table: JTable) : AbstractTopToolBar(table) {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Создать Схему проводки", { createSchema() },	null),
        ButtonKarkas(null, null, null, null)
    )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    init {
        initButton()
    }

    private fun createSchema() {
        DialogCreateSchema(this).showDialogResultOk()
    }
}