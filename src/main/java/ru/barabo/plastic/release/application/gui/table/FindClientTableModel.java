package ru.barabo.plastic.release.application.gui.table;

import ru.barabo.plastic.release.application.data.ClientRowField;
import ru.barabo.total.db.DBStore;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class FindClientTableModel<E extends ClientRowField> extends AbstractTableModel {

	final static private String HTML_4ROW = "<html>%s<br>%s<br>%s</html>";

	private DBStore<E> store;


	FindClientTableModel(DBStore<E> store) {
		this.store = store;
	}

	@Override
	public int getRowCount() {

		List<E> data = store.getData();

		return (data == null) ? 0 : data.size();
	}


	@Override
	public String getColumnName(int column) {

		return "Похожие клиенты";
	}

	@Override
	public int getColumnCount() {

		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		List<E> data = store.getData();

		if (data == null || data.size() <= rowIndex) {
			return null;
		}

		E row = data.get(rowIndex);

		if (row == null || row.fieldItems().size() == 0) {
			return null;
		}

		return getHtmlValue(row);
	}

	private String nvl(String value) {
		return value == null ? "" : value;
	}

	private String getHtmlValue(E row) {

		String doc = "Паспорт " + nvl(row.getSeria()) + " " + nvl(row.getNumber()) +
				" ИНН " + nvl(row.getInn());

		return String.format(HTML_4ROW, row.getName(), row.getAddress(), doc);
	}

}