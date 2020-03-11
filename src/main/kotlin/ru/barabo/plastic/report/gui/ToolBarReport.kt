package ru.barabo.plastic.report.gui

import ru.barabo.plastic.card.gui.menuItem
import ru.barabo.plastic.card.gui.popupButton
import ru.barabo.report.service.HistoryRunService
import javax.swing.JToolBar

class ToolBarReport() : JToolBar() {
    init {

        popupButton("Создать ➧", "newFile") {
            menuItem("Корневую папку", "folder") {  }

            menuItem("Дочернюю папку", "subfolder") {  }

            menuItem("Отчет", "exportXLS") { DialogCreateReport(null,this).showDialogResultOk() }
        }

        popupButton("Правка ➧", "application") {
            menuItem("Папки", "folder") {  }

            menuItem("Отчета", "exportXLS") { DialogCreateReport(HistoryRunService.selectedReport,this).showDialogResultOk() }
        }
    }
}