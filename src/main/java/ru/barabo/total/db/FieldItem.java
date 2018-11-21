package ru.barabo.total.db;



public interface FieldItem {
	
	public final static int LONG_CLAZZ = 0;
	
	public final static int STRING_CLAZZ = 1;
	
	public final static int DOUBLE_CLAZZ = 2;
	
	public final static int DATE_CLAZZ = 3;
	
	/**
	 * название поля
	 */
	String getLabel();
	
	
	/**
	 * присутствует ли филд в сетке
	 */
	boolean isExistsGrid();

	/**
	 * установка значения филда
	 */
	void setValueField(String value);
		
	/**
	 * установка списка значений
	 */
	void setListField(String[] list);
	
	void setValueFieldObject(Object value);
	
	String getColumn();
	
	/**
	 * возврат списка
	 */
	String[] getListField();
	
	/**
	 * возврат значения фильтра для целых возвращает в виде min;max
	 */
	String getValueField();
	
	Object getVal();
	
	/**
	 * 
	 */
	int getWidth();
	
	
	int getIndex();
	
	boolean isReadOnly();
	
	Type getClazz();

}
