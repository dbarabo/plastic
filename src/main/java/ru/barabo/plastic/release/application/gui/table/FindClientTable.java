package ru.barabo.plastic.release.application.gui.table;

import ru.barabo.plastic.release.application.data.AppCardRowField;
import ru.barabo.plastic.release.application.data.ClientRowField;
import ru.barabo.plastic.release.application.data.DBStoreClientFind;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.gui.detail.TableSheetForm;
import ru.barabo.total.gui.table.TotalRowTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class FindClientTable<E extends ClientRowField> extends TotalRowTable<E> {

	private TableSheetForm<AppCardRowField> detailForm;

	public FindClientTable(DBStore<E> store, TableSheetForm<AppCardRowField> detailForm) {
		super(store);

		this.detailForm = detailForm;

		this.setRowHeight(50);

		renderer = getDefaultRenderer(String.class);

		addMouseListener(getMouseListener());
	}

	@Override
	protected AbstractTableModel getDefaultTableModel(DBStore<E> store) {
		return new FindClientTableModel<E>(store);
	}

	private MouseListener getMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() != 2 || (!SwingUtilities.isLeftMouseButton(e))) {
					return;
				}
				
				DBStoreClientFind store = (DBStoreClientFind)FindClientTable.this.getStore();

				store.selectClient(detailForm.getFocusedComponentName());
			}
		};
	}
}
