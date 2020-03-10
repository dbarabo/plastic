package ru.barabo.plastic.report.gui

import ru.barabo.plastic.card.gui.menuItem
import ru.barabo.plastic.card.gui.popupButton
import javax.swing.JToolBar

class ToolBarReport() : JToolBar() {
    init {

        popupButton("Создать ➧", "newFile") {
            menuItem("Корневую папку", "folder") {  }

            menuItem("Дочернюю папку", "subfolder") {  }

            menuItem("Отчет", "exportXLS") { DialogCreateReport(this).showDialogResultOk() }
        }
    }
}