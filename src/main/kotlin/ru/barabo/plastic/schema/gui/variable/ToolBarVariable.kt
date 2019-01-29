package ru.barabo.plastic.schema.gui.variable

import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.JTable

class ToolBarVariable(table: JTable) : AbstractTopToolBar(table) {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Создать функцию", { createVariable() },	null),
        ButtonKarkas(null, null, null, null)
    )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    init {
        initButton()
    }

    private fun createVariable() {
        DialogCreateVariable(this).showDialogResultOk()
    }
}