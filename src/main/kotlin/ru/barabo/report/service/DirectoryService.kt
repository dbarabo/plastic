package ru.barabo.report.service

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.report.entity.Directory
import ru.barabo.report.entity.GroupDirectory

object DirectoryService : StoreFilterService<Directory>(AfinaOrm, Directory::class.java) {

    private val directories: MutableList<GroupDirectory> = ArrayList()

    private var parentGroup: GroupDirectory? = null

    override fun initData() {
        directories.clear()

        parentGroup = null

        super.initData()
    }

    override fun processInsert(item: Directory) {

        val parent = item.parent?.let { parentGroup }

        val reports = ReportService.reportsByDirectory(item.id)

        val group = GroupDirectory(directory = item, parent = parent, reports = reports)

        directories.add(group)

        if(parent == null) {
            parentGroup = group
        } else {
            parentGroup?.childDirectories?.add(group)
        }
    }
}