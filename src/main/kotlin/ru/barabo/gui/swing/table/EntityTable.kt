package ru.barabo.gui.swing.table

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
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

        componentPopupMenu = getPopupMenu()

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
           selectionModel.minSelectionIndex == store.selectedRowIndex) return

        val tableModel = model as? AbstractTableModel ?: return

        tableModel.fireTableDataChanged()
    }

    override fun getColumnClass(column: Int): Class<*>  = model.getColumnClass(column)

    private fun getPopupMenu(): JPopupMenu =
        JPopupMenu().apply {
            add( JMenuItem("Копировать строку").apply {
                addActionListener { copyRow(it)}
            })

            add( JMenuItem("Копировать всю таблицу").apply {
                addActionListener { copyTable(it)}
            })
        }

    private fun copyTable(e: ActionEvent) {

        val data = store.elemRoot()

        val tableData = StringBuilder()

        for (row in data) {
            tableData.append(row).append("\n")
        }

        val selection = StringSelection(tableData.toString())

        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }

    private fun copyRow(e: ActionEvent) {
        val row = store.selectedEntity() ?: return

        val selection = StringSelection(row.toString())

        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }
}

