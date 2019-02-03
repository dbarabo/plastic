package ru.barabo.plastic.schema.gui.schema

import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JTable

class ToolBarSchema(table: JTable) : AbstractTopToolBar(table) {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Создать Схему проводки", { createSchema() },	null),
        ButtonKarkas("condition", "Создать условие", { createConditon() },	null),
        ButtonKarkas("condition", "Правка варианта усл.", { updateConditon() },	null),
        ButtonKarkas(null, null, null, null)
    )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    init {
        initButton()
    }

    private fun createSchema() {
        DialogCreateSchema(this).showDialogResultOk()
    }

    private fun createConditon() {
        DialogCreateCondition(this).showDialogResultOk()
    }

    private fun updateConditon() {
        DialogUpdateCondition(this).showDialogResultOk()
    }
}