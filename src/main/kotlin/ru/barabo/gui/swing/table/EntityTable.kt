package ru.barabo.gui.swing.table

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreListener
import ru.barabo.db.service.StoreService
import ru.barabo.total.gui.table.TotalRenderer
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellRenderer

open class EntityTable<T: Any, G>(val columns: List<ColumnTableModel<T, *>>, store: StoreService<T, G>) : JTable(),
    StoreListener<G> {

//    private var renderer: TableCellRenderer = TotalRenderer()

    init {
        model = DefaultTableModel(columns, store)

        setColumnsSize(columns)

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

        store.addListener(this)
    }

//    override fun getCellRenderer(row: Int, column: Int): TableCellRenderer = renderer

    private fun setColumnsSize(columns: List<ColumnTableModel<T, *>>) {

        for((index, column) in columns.withIndex()) {

            columnModel.getColumn(index).preferredWidth = column.width
        }
    }

    override fun refreshAll(elemRoot: G, refreshType: EditType) {

        val tableModel = model as? AbstractTableModel ?: return

        tableModel.fireTableDataChanged()
    }

    override fun getColumnClass(column: Int): Class<*>  = model.getColumnClass(column)
}