package ru.barabo.total.gui.filter.impl;

import org.apache.log4j.Logger;
import ru.barabo.total.db.FilteredStore;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class FilterModelSimple<E> extends AbstractTableModel {

	private FilteredStore store;
	private JTable mainTable;
	
	private JTable modelTable;

	final static transient private Logger logger = Logger.getLogger(FilterModelSimple.class.getName());
	
	public FilterModelSimple(FilteredStore store, JTable mainTable, JTable modelTable) {
		this.store = store;
		this.mainTable = mainTable;
		this.modelTable = modelTable;
	}

	@Override
	public int getColumnCount() {
		return mainTable.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		return store.getFilterValue(columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	
		if("".equals(aValue)) aValue = null;

		store.setFilterValue(columnIndex, (String)aValue);
		((AbstractTableModel)mainTable.getModel()).fireTableDataChanged();

		// modelTable.requestFocus();
	}
}
