package ru.barabo.plastic.terminal.gui

import ru.barabo.plastic.terminal.service.PosTerminalService
import java.awt.Component

class DialogOpenPact(component: Component) : AbstractDialogDate(component, "Дата открытия договора") {
    override fun okProcess() {
        PosTerminalService.setOpenPact(datePicker.date)
    }
}

class DialogClosePact(component: Component) : AbstractDialogDate(component, "Дата закрытия договора") {
    override fun okProcess() {
        PosTerminalService.setClosePact(datePicker.date)
    }
}