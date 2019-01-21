package ru.barabo.db.service

import ru.barabo.db.EditType
import ru.barabo.db.TemplateQuery

open class StoreFilterService<T: Any>(orm: TemplateQuery, clazz: Class<T>) : StoreService<T, List<T>>(orm, clazz) {

    @Volatile private var isFiltered = false

    @Volatile var selectedRowIndex: Int = 0
    set(value) {
        if(value < 0 || value >= dataListCount()) return

        field = value

        sentRefreshAllListener(EditType.CHANGE_CURSOR)
    }

    fun selectedEntity(): T? = if(selectedRowIndex < 0 || selectedRowIndex >= dataListCount()) null else getEntity(selectedRowIndex)

    private val filterdList = ArrayList<T>()

    override fun elemRoot(): List<T> = if(isFiltered) filterdList else dataList

    override fun dataListCount() = if(isFiltered) filterdList.size else super.dataListCount()

    override fun getEntity(rowIndex: Int): T? = if(isFiltered) getItemByIndex(rowIndex) else super.getEntity(rowIndex)

    private fun getItemByIndex(index: Int) = if(index < filterdList.size) filterdList[index] else null

    @Synchronized
    fun setFilter(accessCriteria: (T) -> Boolean) {
        filterdList.clear()

        for(item in dataList) {
            if(accessCriteria(item)) filterdList += item
        }

        isFiltered = true

        sentRefreshAllListener(EditType.FILTER)
    }

    fun resetFilter() {
        isFiltered = false
        sentRefreshAllListener(EditType.FILTER)
    }
}