package ru.barabo.total.gui.filter.impl;


import org.apache.log4j.Logger;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FilteredStore;
import ru.barabo.total.gui.any.DefFilterEditor;
import ru.barabo.total.gui.filter.FilterTable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

public class FilterAccessTable<E, F> extends JTable implements FilterTable {

	final static transient private Logger logger = Logger.getLogger(FilterAccessTable.class
			.getName());

	// private JTable mainTable;
	private JTable searchTable;
	private TableCellEditor defaultEditor;

	public FilterAccessTable(FilteredStore srcFilterStore, FilteredStore toAccessStore,
							 int srcFilterFieldAssoc, JTable mainTable, JTable searchTable, DBStore<F> searchAddStore) {
		super();
		this.searchTable = searchTable;

		setModel(new FilterAccessModel(srcFilterStore, toAccessStore, srcFilterFieldAssoc,
				mainTable, searchTable, searchAddStore));

		initEditorRenderer(searchTable);
	}

	private void initEditorRenderer(JTable searchTable) {
		DefFilterEditor def = new DefFilterEditor();

		JTextComponent text = (JTextComponent) def.getField();

		text.addKeyListener(new TextKeyListener(this));

		defaultEditor = def;

		searchTable.getColumnModel().addColumnModelListener(new ColumnFilterListener(this));
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		return defaultEditor;
	}

	@Override
	public void updateWidthColumn() {

		for (int index = 0; index < searchTable.getColumnModel().getColumnCount(); index++) {

			int width = searchTable.getColumnModel().getColumn(index).getWidth();

			width = index == 0 ? width + 1 : width;

			this.getColumnModel().getColumn(index).setPreferredWidth(width);

			this.getColumnModel().getColumn(index).setMinWidth(width);

			this.getColumnModel().getColumn(index).setMaxWidth(width);
		}
	}

	@Override
	public void setFilterPress() {

		int columnIndex = this.getSelectedColumn();

		// logger.info("columnIndex=" + columnIndex);

		JTextComponent field = (JTextComponent) ((DefFilterEditor) defaultEditor).getField();

		if (field == null) {
			return;
		}

		((FilterAccessModel) this.getModel()).setFilterValue(field.getText(), columnIndex);
	}

}
