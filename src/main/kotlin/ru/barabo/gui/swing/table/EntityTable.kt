package ru.barabo.gui.swing.table

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.db.service.StoreService
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.table.AbstractTableModel

open class EntityTable<T: Any>(columns: List<ColumnTableModel<T, *>>, private val store: StoreFilterService<T>) : JTable(),
    StoreListener<List<T>> {

//    private var renderer: TableCellRenderer = TotalRenderer()

    init {
        model = DefaultTableModel(columns, store)

        setColumnsSize(columns)

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)


        selectionModel.addListSelectionListener(::selectListener)

        store.addListener(this)
    }

    private fun selectListener(e: ListSelectionEvent) {
        if (e.valueIsAdjusting) return

        val selModel = e.source as? ListSelectionModel ?: return
        // Номер текущей строки таблицы
        if (selModel.isSelectionEmpty) return

        store.selectedRowIndex = selModel.minSelectionIndex
    }

//    override fun getCellRenderer(row: Int, column: Int): TableCellRenderer = renderer

    private fun setColumnsSize(columns: List<ColumnTableModel<T, *>>) {

        for((index, column) in columns.withIndex()) {

            columnModel.getColumn(index).preferredWidth = column.width
        }
    }

    override fun refreshAll(elemRoot: List<T>, refreshType: EditType) {

        if(refreshType == EditType.CHANGE_CURSOR &&
           selectionModel.minSelectionIndex == store.selectedRowIndex) return;

        val tableModel = model as? AbstractTableModel ?: return

        tableModel.fireTableDataChanged()
    }

    override fun getColumnClass(column: Int): Class<*>  = model.getColumnClass(column)
}