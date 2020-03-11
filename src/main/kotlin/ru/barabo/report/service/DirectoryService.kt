package ru.barabo.report.service

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.report.entity.Directory
import ru.barabo.report.entity.GroupDirectory

object DirectoryService : StoreFilterService<Directory>(AfinaOrm, Directory::class.java) {

    lateinit var directories: MutableList<GroupDirectory>
    private set

    private var parentGroup: GroupDirectory? = null

    var selectedDirectory: GroupDirectory? = null

    override fun initData() {

        if(!::directories.isInitialized) {
            directories = ArrayList()
        }

        directories.clear()

        parentGroup = null

        super.initData()

        selectedDirectory = if(directories.isEmpty()) null else directories[0]
    }

    override fun processInsert(item: Directory) {

        val parent = item.parent?.let { parentGroup }

        val reports = ReportService.reportsByDirectory(item.id)

        val group = GroupDirectory(directory = item, parent = parent, reports = reports)

        if(parent == null) {
            parentGroup = group
            directories.add(group)
        } else {
            parentGroup?.childDirectories?.add(group)
        }
    }

    fun directoryById(directoryId: Long?): Directory? =
        if(directoryId == null) null else dataList.firstOrNull { it.id == directoryId }

}