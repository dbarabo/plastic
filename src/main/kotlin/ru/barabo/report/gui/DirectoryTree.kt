package ru.barabo.report.gui

import org.jdesktop.swingx.JXHyperlink
import org.jdesktop.swingx.JXTaskPane
import ru.barabo.db.EditType
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.report.entity.Directory
import ru.barabo.report.entity.GroupDirectory
import ru.barabo.report.entity.Report
import ru.barabo.report.service.DirectoryService
import ru.barabo.report.service.ReportService
import ru.barabo.xls.ParamContainer
import java.awt.Container
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JToolBar

class DirectoryTree(private val paramPanel: Container, title: JLabel? = null) : JToolBar(VERTICAL) {

    private val refreshReport = Refresher<Report>(this, title)
    private val refreshDirectory = Refresher<Directory>(this, title)

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        isFloatable = true

        rebuild(title)

        DirectoryService.addListener(refreshDirectory)
        ReportService.addListener(refreshReport)
    }

    internal fun rebuild(title: JLabel?) {
        this.removeAll()

        for(groupDirectory in DirectoryService.directories) {

            add( groupDirectory.buildTree(paramPanel, title) )
        }
    }
}

private class Refresher<T>(private val tree: DirectoryTree, private val title: JLabel?): StoreListener<List<T>> {

    override fun refreshAll(elemRoot: List<T>, refreshType: EditType) {
        if(refreshType in listOf(EditType.INSERT, EditType.EDIT, EditType.DELETE, EditType.INIT, EditType.ALL) ) {
            tree.rebuild(title)
        }
    }
}

private fun GroupDirectory.buildTree(paramPanel: Container, title: JLabel?): Container {

    val item = JXTaskPane(directory.name)

    item.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(me: MouseEvent) {
                DirectoryService.selectedDirectory = this@buildTree
            }
        }
    )

    for(childDirectory in childDirectories) {
        item.add( childDirectory.buildTree(paramPanel, title) )
    }

    for(report in reports) {
        item.add( report.buildItem(paramPanel, title) )
    }

    return item
}

private fun Report.buildItem(paramPanel: Container, title: JLabel?): Container {
    val reportItem = JXHyperlink()

    reportItem.text = nameWithCount // name

    reportItem.addActionListener { this.clickReport(paramPanel, title)  }

    return reportItem
}

private fun Report.clickReport(paramPanel: Container, title: JLabel?) {

   processShowError {
       title?.text = name

       ReportService.prepareRun(this)
           .buildWithrequestParam(ArrayList(), Params(paramPanel) )
   }
}

private class Params(override val container: Container): ParamContainer {

    override fun afterReportCreated(reportFile: File) {
        processShowError {
            Desktop.getDesktop().open(reportFile)
        }
    }
}

