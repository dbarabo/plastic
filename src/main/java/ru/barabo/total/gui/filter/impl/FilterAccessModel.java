package ru.barabo.total.gui.filter.impl;

import org.apache.log4j.Logger;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FilteredStore;
import ru.barabo.total.db.impl.AbstractDBStore;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class FilterAccessModel<E, F> extends AbstractTableModel {

	final static transient private Logger logger = Logger
			.getLogger(FilterAccessModel.class.getName());

	private JTable accessTable;
	private JTable searchTable;

	private FilteredStore srcFilterStore;
	private FilteredStore toAccessStore;
	private int srcFilterFieldAssoc;

	private DBStore<F> searchAddStore;


	public FilterAccessModel(FilteredStore srcFilterStore, FilteredStore toAccessStore,
							 int srcFilterFieldAssoc, JTable accessTable, JTable searchTable,
							 DBStore<F> searchAddStore) {

		this.accessTable = accessTable;
		this.searchTable = searchTable;
		this.srcFilterStore = srcFilterStore;
		this.toAccessStore = toAccessStore;
		this.srcFilterFieldAssoc = srcFilterFieldAssoc;

		this.searchAddStore = searchAddStore;
	}

	@Override
	public int getColumnCount() {
		return searchTable.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		return srcFilterStore.getFilterValue(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		setFilterValue((String) aValue, columnIndex);

	}

	public void setFilterValue(String value, int columnIndex) {
		if ("".equals(value)) {
			value = null;
		}

		srcFilterStore.setFilterValue(columnIndex, value);

		String allValues = srcFilterStore.getAllFieldValue(srcFilterFieldAssoc);

		// logger.info("allValues==" + allValues);

		// srcFilterStore.setFilterValue(columnIndex, null);

		toAccessStore.setFilterValue(-1, allValues);

		((AbstractTableModel) accessTable.getModel()).fireTableDataChanged();

		((AbstractDBStore) searchAddStore).searchTo(((AbstractDBStore) srcFilterStore).getData());
	}
}
