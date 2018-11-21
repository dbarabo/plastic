package ru.barabo.total.db;

public interface FilteredStore {
	

	void setFilterValue(int columnIndex, String value);
	
	String getFilterValue(int columnIndex);

	String getAllFieldValue(int fieldIndex);
}
