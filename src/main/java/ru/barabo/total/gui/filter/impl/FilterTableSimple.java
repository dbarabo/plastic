package ru.barabo.total.gui.filter.impl;

import org.apache.log4j.Logger;
import ru.barabo.total.db.FilteredStore;
import ru.barabo.total.gui.any.DefFilterEditor;
import ru.barabo.total.gui.filter.FilterTable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

public class FilterTableSimple<E> extends JTable implements FilterTable {
	
	final static transient private Logger logger = Logger.getLogger(FilterTableSimple.class.getName());

	private JTable mainTable;
	private TableCellEditor defaultEditor;
	
	private FilteredStore store;
	
	
	public FilterTableSimple(FilteredStore store, JTable mainTable) {
		super();
		this.mainTable = mainTable;
		this.store = store;
		
		setModel(new FilterModelSimple(store, mainTable, this));
        
        initEditorRenderer(mainTable);
	}
	
	private void initEditorRenderer(JTable mainTable) {
		DefFilterEditor def = new DefFilterEditor();
		
		JTextComponent text = (JTextComponent)def.getField();
		
		text.addKeyListener(new TextKeyListener(this));
		
		defaultEditor = def;
		
		
		mainTable.getColumnModel().addColumnModelListener(new ColumnFilterListener(this)); 
	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		return defaultEditor;
	}
	
	@Override
	public void updateWidthColumn() {
	
		for (int index = 0; index < mainTable.getColumnModel().getColumnCount(); index++) {
			
			int width = mainTable.getColumnModel().getColumn(index).getWidth();
			
			width = index == 0 ? width + 1 : width;
			
			this.getColumnModel().getColumn(index).setPreferredWidth(width);
			
			this.getColumnModel().getColumn(index).setMinWidth(width);
			
			this.getColumnModel().getColumn(index).setMaxWidth(width);
		}
	}
	
	@Override
	public void setFilterPress() {
		
		int columnIndex = this.getSelectedColumn();
		
		//logger.info("columnIndex=" + columnIndex);
		
		JTextComponent field = (JTextComponent)((DefFilterEditor)defaultEditor).getField();
		
		if (field == null) {

			return;
		}
		
		logger.info("field.getText()=" + field.getText());
		
		store.setFilterValue(columnIndex, field.getText());

		// this.requestFocus();
	}
	
	
}
