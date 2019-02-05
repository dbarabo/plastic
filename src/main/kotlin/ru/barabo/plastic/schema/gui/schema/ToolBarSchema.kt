package ru.barabo.plastic.schema.gui.schema

import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.schema.service.schema.SchemaService
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import java.lang.Exception
import javax.swing.JTable

class ToolBarSchema(table: JTable) : AbstractTopToolBar(table) {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Создать Схему проводки", { createSchema() },	null),
        ButtonKarkas("application", "Правка Схемы", { editSchema() },	null),
        ButtonKarkas("condition", "Задать условие", { createCondition() },	null),
        ButtonKarkas(null, null, null, null)
    )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    init {
        initButton()
    }

    private fun createSchema() {
        DialogCreateSchema(SchemaService.createDefaultSchema(), this).showDialogResultOk()
    }

    private fun editSchema() {

        processShowError {
            val schema = SchemaService.selectedEntity() ?: throw Exception("Нет текущей схемы проводки")

            DialogCreateSchema(schema, this).showDialogResultOk()
        }
    }

    private fun createCondition() {
        DialogCreateCondition(this).showDialogResultOk()
    }
}