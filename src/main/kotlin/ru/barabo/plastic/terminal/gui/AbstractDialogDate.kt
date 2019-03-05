package ru.barabo.plastic.terminal.gui

import org.jdesktop.swingx.JXDatePicker
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import java.awt.Component

abstract class AbstractDialogDate(component: Component, title: String) : AbstractDialog(component, title) {

    protected val datePicker: JXDatePicker

    init {
        groupPanel("Проставьте дату", 0, 2, 0).apply {
            datePickerHorisontal(title, 0).apply {datePicker = this }
        }

        createOkCancelButton(2)

        pack()
    }
}