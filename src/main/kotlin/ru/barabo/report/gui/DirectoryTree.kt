package ru.barabo.report.gui

import org.jdesktop.swingx.JXHyperlink
import org.jdesktop.swingx.JXTaskPane
import org.slf4j.LoggerFactory
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.report.entity.GroupDirectory
import ru.barabo.report.entity.Report
import ru.barabo.report.service.DirectoryService
import ru.barabo.report.service.ReportService
import java.awt.Container
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BoxLayout
import javax.swing.JToolBar

private val logger = LoggerFactory.getLogger(DirectoryTree::class.java)

class DirectoryTree(private val paramPanel: Container) : JToolBar(VERTICAL) {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        isFloatable = true

        rebuild()
    }

    fun rebuild() {

        for(groupDirectory in DirectoryService.directories) {

            add( groupDirectory.buildTree(paramPanel) )
        }
    }
}

private fun GroupDirectory.buildTree(paramPanel: Container): Container {

    val item = JXTaskPane(directory.name)

    item.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(me: MouseEvent) {
                DirectoryService.selectedDirectory = this@buildTree
            }
        }
    )

    for(childDirectory in childDirectories) {
        item.add( childDirectory.buildTree(paramPanel) )
    }

    for(report in reports) {
        item.add( report.buildItem(paramPanel) )
    }

    return item
}

private fun Report.buildItem(paramPanel: Container): Container {
    val reportItem = JXHyperlink()

    reportItem.text = name

    reportItem.addActionListener { this.clickReport(paramPanel)  }

    return reportItem
}

private fun Report.clickReport(paramPanel: Container) {

    processShowError {

        val exceSql = ReportService.prepareRun(this)

        exceSql.requestParam(paramPanel) {
            processShowError { Desktop.getDesktop().open(it) }
        }
    }
}