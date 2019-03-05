package ru.barabo.gui.swing.table

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.table.AbstractTableModel

open class EntityTable<T: Any>(private val columns: List<ColumnTableModel<T, *>>, private val store: StoreFilterService<T>) : JTable(),
    StoreListener<List<T>> {

    private var isFirstRefresh = true

    private val columnSum: Int

    init {
        model = DefaultTableModel(columns, store)

        setColumnsSize(columns)

        selectionModel.addListSelectionListener(::selectListener)

        componentPopupMenu = getPopupMenu()

        store.addListener(this)

        columnSum = columns.map { it.width }.sum()
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

        //setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN)

        val delimetr = width.toDouble() / columnSum

        for((index, column) in columns.withIndex()) {

            columnModel.getColumn(index).preferredWidth = (column.width * delimetr).toInt()

            columnModel.getColumn(index).width = (column.width * delimetr).toInt()
        }
    }

    override fun refreshAll(elemRoot: List<T>, refreshType: EditType) {

        if(refreshType == EditType.CHANGE_CURSOR &&
           selectionModel.minSelectionIndex == store.selectedRowIndex) return

        val tableModel = model as? AbstractTableModel ?: return

        if(isFirstRefresh && elemRoot.isNotEmpty()) {
            isFirstRefresh = false
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            tableModel.fireTableStructureChanged()
            setColumnsSize(columns)

            return
        }

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
            tableData.append(entityToString(row)).append("\n")
        }

        val selection = StringSelection(tableData.toString())

        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }

    private fun copyRow(e: ActionEvent) {
        val row = store.selectedEntity()?.let { entityToString(it) } ?: return

        val selection = StringSelection(row)

        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }

    private fun entityToString(entity: T): String {
        return if(model is DefaultTableModel<*>) (model as DefaultTableModel<T>).getEntityByString(entity)
            else entity.toString()
    }

}

