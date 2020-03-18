package ru.barabo.report.gui

import ru.barabo.plastic.afina.AccessMode
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.card.gui.menuItem
import ru.barabo.plastic.card.gui.popupButton
import ru.barabo.report.service.DirectoryService
import ru.barabo.report.service.ReportService
import javax.swing.JToolBar

class ToolBarReport() : JToolBar() {
    init {

        popupButton("Создать ➧", "newFile") {
            menuItem("Папку", "folder") {
                DialogCreateDirectory(null, this).showDialogResultOk()
            }

            menuItem("Отчет", "exportXLS") {
                DialogCreateReport(null, this).showDialogResultOk()
            }
        }.apply {
            isEnabled = AfinaQuery.getUserDepartment().accessMode == AccessMode.FullAccess
        }

        popupButton("Правка ➧", "application") {
            menuItem("Папки", "folder") {
                DialogCreateDirectory(DirectoryService.selectedDirectory?.directory, this).showDialogResultOk()
            }

            menuItem("Отчета", "exportXLS") {
                DialogCreateReport(ReportService.selectedReport, this).showDialogResultOk()
            }
        }.apply {
            isEnabled = AfinaQuery.getUserDepartment().accessMode == AccessMode.FullAccess
        }
    }
}