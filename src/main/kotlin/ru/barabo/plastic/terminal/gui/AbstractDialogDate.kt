package ru.barabo.plastic.terminal.gui

import org.jdesktop.swingx.JXDatePicker
import ru.barabo.db.toSqlDate
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import java.awt.Component
import java.time.LocalDate

abstract class AbstractDialogDate(component: Component, title: String) : AbstractDialog(component, title) {

    protected val datePicker: JXDatePicker

    init {
        groupPanel("Проставьте дату", 0, 2, 0).apply {
            datePickerHorisontal(title, 0).apply {
                datePicker = this
            }
        }

        createOkCancelButton(2)

        pack()
    }

    fun showDialog(date: LocalDate?): Boolean {

        datePicker.date = date?.toSqlDate()

        return showDialogResultOk()
    }
}