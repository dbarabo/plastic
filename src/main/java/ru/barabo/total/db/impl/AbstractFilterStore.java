package ru.barabo.total.db.impl;

import org.apache.log4j.Logger;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.FilteredStore;
import ru.barabo.total.db.ListenerStore;
import ru.barabo.total.db.Type;

import java.util.*;

public abstract class AbstractFilterStore<E extends AbstractRowFields> extends AbstractDBStore<E>
implements FilteredStore {
	
	final static transient private Logger logger = Logger.getLogger(AbstractFilterStore.class.getName());
	
	private String[] filters;
	
	private List<E> filterData;
	
	private List<E> oldFilterData;
	
	public AbstractFilterStore() {
		super();
		
		filterData = new ArrayList<E>(); 
		
		oldFilterData = new ArrayList<E>(); 
	}
	
	private E getField() {
		List<E> data = super.getData();
		
		if(data != null && data.size() != 0) {
			return data.get(0);
		}
		
		return createEmptyRow();
	}
	
	private int getFieldIndex(int columnIndex) {
		E field = getField();
		
		//logger.info(field.fieldItems().size());
		
		if(field == null) return -1;

		if (columnIndex == -1) {
			return 0;
		}

		
		int indexVis = -1;
		
		for (int index = 0; index < field.fieldItems().size(); index++) {
			
			FieldItem fld = field.fieldItems().get(index);
			
			if(fld.isExistsGrid() ) {
				indexVis++;
			}
			
			if(indexVis == columnIndex) {
				return index;
			}
		}
		
		return indexVis;
	}
	
	public static String getNumberString(String valueFilter) {
		if (valueFilter == null) return null;
		
		String res = "";

		for (int index = 0; index < valueFilter.length(); index++) {
			if(valueFilter.charAt(index) >= '0' && valueFilter.charAt(index) <= '9') {
				res += valueFilter.charAt(index);
			}
		}
		
		//logger.info("getNumberString=" + res);
		
		return res;
	}
	

	public static Long getNumberFromDigits(String digits) {
		if("".equals(digits)) {
			return null;
		}
		
		while("0".equals(digits.substring(0, 1)) ) {
			digits = digits.substring(1);
		}
		
		return Long.parseLong(digits);
	}
	
	public static Long getNumber(String valueFilter) {
		if (valueFilter == null) return null;
		
		String res = getNumberString(valueFilter);
		
		return getNumberFromDigits(res);
	}
	
	private boolean isCriteriaLong(Object valueField, String valueFilter) {
		if(valueField == null) return false;
		
		if (valueFilter != null && valueFilter.contains(SEPARATOR_VALUE)) {

			return valueFilter.contains(SEPARATOR_VALUE + valueField.toString() + SEPARATOR_VALUE);
		}

		Long number = getNumber(valueFilter);
		
		if(number == null) return true;
		
		return ((Number)valueField).longValue() == number;
	}
	
	private List<String> getPartsString(String value) {
		
		if(value == null) return null;
		
		List<String> parts = new ArrayList<String>();
		
		int startIndex = 0;
		int Endindex = 0;
		String part = null;
				
		while(Endindex >= 0) {
			Endindex = value.indexOf(' ', startIndex);
			
			if(Endindex < 0) {
				//logger.info("startIndex=" + startIndex + " value.length=" + value.length());
				part = value.substring(startIndex);
				
				
				//logger.info("part="  + part);
				//logger.info("part.length=" + part.length());
			} else if(Endindex > startIndex) {
				part = value.substring(startIndex, Endindex);
				
			} else {
				part = "";
			}
			
			startIndex = Endindex + 1;
			
			//logger.info("2part.length=" + part.length());
			if(parts.size() == 0 || (!"".equals(part)) ) {
				parts.add(part.toUpperCase());
			}
			
		}
		
		return parts;
	}
	
	private boolean isCriteriaString(Object valueField, String valueFilter) {
		if(valueField == null) return false;
		
		List<String> parts = getPartsString(valueFilter);
		
		if(parts == null || parts.size() == 0) return true;
		
		String src = ((String)valueField).toUpperCase();
		
		if(src.indexOf(parts.get(0)) != 0) return false;
		
		int priorFindIndex = 0;
		
		int indexFind = 0;
		
		for(int index = 1; index < parts.size(); index++) {
			
			indexFind = src.indexOf(parts.get(index), priorFindIndex);
			
			if(indexFind <= priorFindIndex) {
				return false;
			}
			
			priorFindIndex = indexFind; 
		}
		/*
		if( "".equals(parts.get(parts.size() - 1)) ) return true;
		
		if(src.indexOf(parts.get(parts.size() - 1)) != 
				src.length() - parts.get(parts.size() - 1).length() ) return false;
		*/
		return true;
	}
	
	private Date getDate(int day, int month, int year) {
		
		Calendar calendarTmp = new GregorianCalendar();
		
		if(month == -1) {
			calendarTmp.setTime(new Date());
			month = calendarTmp.get(Calendar.MONTH) + 1;
		}

		
		if(year == -1) {
			calendarTmp.setTime(new Date());
			year = calendarTmp.get(Calendar.YEAR);
		}
		
		calendarTmp.set(year, Calendar.JANUARY, 1, 0, 0, 0);
		
		/*calendarTmp.set(Calendar.DAY_OF_MONTH, 1);
		calendarTmp.set(Calendar.MONTH, 0);
		calendarTmp.set(Calendar.YEAR, year);*/
		calendarTmp.add(Calendar.MONTH, month - 1);
		calendarTmp.add(Calendar.DAY_OF_YEAR, day - 1);
		
		calendarTmp.add(Calendar.HOUR, -10);

		
		return calendarTmp.getTime();
	}
	
	private DateDiapason getDiapason(int dayStart, int monthStart, int yearStart,
			int dayEnd, int monthEnd, int yearEnd) {
		
		Date min = getDate(dayStart, monthStart, yearStart);
		
		Date max = getDate(dayEnd, monthEnd, yearEnd);
		
		return new DateDiapason(min, max);
	}
	
	private DateDiapason getDiapason(int dayStart, int monthStart,  int dayEnd, int monthEnd) {

		Date min = getDate(dayStart, monthStart, -1);
		
		Date max = getDate(dayEnd, monthEnd, -1);
		
		return new DateDiapason(min, max);
	}
	
	private DateDiapason getMonthDay2(String digit2) {
		
		Long val = getNumberFromDigits(digit2);
		
		if(val == null) return null;
		
		if(val.intValue() <= 12) {
			return getDiapason(1, val.intValue(), 1, val.intValue() + 1);
		} else {
			return getDiapason(val.intValue(), -1, val.intValue() + 1, -1);
		}
	}
	
	private DateDiapason getMonthDay4(String digit4) {

		Long day = getNumberFromDigits(digit4.substring(0, 2));
		
		Long month = getNumberFromDigits(digit4.substring(2, 4));
		
		return getDiapason(day.intValue(), month.intValue(), day.intValue() + 1, month.intValue());
	}
	
	
	/**
	 * MMYYYY
	 * @param digit6
	 * @return
	 */
	private DateDiapason getMonthDay6(String digit6) {

		Long month = getNumberFromDigits(digit6.substring(0, 2));
		
		Long year = getNumberFromDigits(digit6.substring(2, 6));
		
		return getDiapason(1, month.intValue(), year.intValue(),
				1, month.intValue() + 1, year.intValue());
	}
	
	private DateDiapason getMonthDay8(String digit8) {

		Long day = getNumberFromDigits(digit8.substring(0, 2));
		
		Long month = getNumberFromDigits(digit8.substring(2, 4));
		
		Long year = getNumberFromDigits(digit8.substring(4, 8));
		
		return getDiapason(day.intValue(), month.intValue(), year.intValue(),
				day.intValue() + 1, month.intValue(), year.intValue() );
	}
	
	private DateDiapason getDiapason(String valueFilter) {
		
		String dt = getNumberString(valueFilter);
		
		if(dt == null) return null;
		
		if(dt.length() == 1  || dt.length() == 2 || dt.length() == 3) {
			return getMonthDay2(dt);
		} else if(dt.length() == 4 || dt.length() == 5) {
			return getMonthDay4(dt);
		} else if(dt.length() == 6 || dt.length() == 7) {
			return getMonthDay6(dt);
		} else {
			return getMonthDay8(dt);
		}
	}
	
	private boolean isCriteriaDate(Object valueField, String valueFilter) {
		if(valueField == null) return false;
		
		DateDiapason diapason = getDiapason(valueFilter);
		
		if(diapason == null) return true;
		
		long value = ((Date)valueField).getTime();
		
		return diapason.minDate.getTime() <= value && diapason.maxDate.getTime() > value;
	}
	
	private boolean isCriteriaField(Object valueField, Type clazz, String valueFilter) {
		
		switch(clazz) {
		case LONG:
			return isCriteriaLong(valueField, valueFilter);
			
		case STRING:
			return isCriteriaString(valueField, valueFilter);
		
		case DECIMAL:
			return false; //isCriteriaDouble(valueField, valueFilter);
		
		case DATE:
			return isCriteriaDate(valueField, valueFilter);
			
		default:
			return false;
		}
	}
	
	
	private boolean isCriteriaFilter(E row) {
		
		if(filters == null || filters.length == 0) return true;
		
		List<FieldItem> items = row.fieldItems();
		
		boolean isCriteria = true;
		
		for(int index = 0; index < filters.length; index++) {
			
			if(filters[index] == null || ("".equals(filters[index].trim()))) continue;
			
			if(!isCriteriaField(items.get(index).getVal(), 
					            items.get(index).getClazz(), 
					            filters[index])) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isEqualFilter(List<E> filterData, List<E> oldFilterData) {
		
		if(filterData.size() != oldFilterData.size()) return false;
		
		for (E row : filterData) {
			if(oldFilterData.indexOf(row) < 0) {
				return false;
			}
		}
		
		return true;
	}
	
	private void checkAfterFilter() {
		if(isEqualFilter(filterData, oldFilterData)) return;
		
		
		oldFilterData.clear();
		oldFilterData.addAll(filterData);
		
		sendListenersRefreshAllData();
		//sendListenersCursor(getRow());
	}
	
	/**
	 * �������� ���� ���������� �� ��������� ���� ������
	 */
	@Override
	public void sendListenersRefreshAllData() {
		if (!isFiltered()) {
			super.sendListenersRefreshAllData();
			return;
		}

		for (ListenerStore<E> listenerStore : listenersStore) {
			listenerStore.refreshData(filterData);
		}
	}

	private final static String SEPARATOR_VALUE = ";";

	@Override
	public String getAllFieldValue(int fieldIndex) {

		logger.info("getAllFieldValue fieldIndex=" + fieldIndex);
		logger.info("getAllFieldValue filterData=" + filterData);

		if (filterData != null) {
			logger.info("getAllFieldValue filterData.length=" + filterData.size());
		}

		if(filterData == null) {
			return "";
		}
		
		String result = SEPARATOR_VALUE;
		
		for(E row : filterData) {
			logger.info("getAllFieldValue row.item="
					+ row.fieldItems().get(fieldIndex).getValueField());
			result += row.fieldItems().get(fieldIndex).getValueField() + SEPARATOR_VALUE;
		}

		if (SEPARATOR_VALUE.equals(result)) {
			return "";
		}
		
		return result;
	}

	private void setFiltered() {
		filterData.clear();
		
		for (E row : super.getData()) {
			if(isCriteriaFilter(row) ) {
				filterData.add(row);
			}
		}
		
		checkAfterFilter();
	}
	
	private boolean isFiltered() {
		
		if(filters == null) return false;
		
		for(String val : filters) {
			if(val == null || "".equals(val.trim())) continue;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<E> getData() {
		
		boolean isUpdateNeed = isMustUpdate();
		
		List<E> data = super.getData();

		// if (logger.isDebugEnabled() /* && this instanceof
		// DBStorePacketContent */) {
		// logger.info("data.size=" + data.size());
		// }

		if (!isFiltered()) {
			return data;
		}
		
		if(isUpdateNeed) {
			setFiltered();
		}

		// if (logger.isDebugEnabled() /* && this instanceof
		// DBStorePacketContent */) {
		// logger.info("filterData.size=" + filterData.size());
		// }

		return filterData;
	}
	
	@Override
	public E getRow() { 
		E row = super.getRow();
		
		// logger.info(this.getClass().getName() + " getRow=" + row);

		if((!isFiltered()) || (row == null) ) return row;
		
		// logger.info("row.getName() = " + row.getName());
		// logger.info("row.getId() = " + row.getId());

		// logger.info("filterData.indexOf(row) = " + filterData.indexOf(row));

		// filterData.stream().map(f -> f.getId()).forEach(logger::info);

		if (filterData.indexOf(row) >= 0) {
			return row;
		}

		if (filterData.size() > 0 && filterData.indexOf(row) < 0) {
			return filterData.get(0);
		}

		return null;
	}
	
	@Override
	public void setRow(E row) {
		// TODO
		super.setRow(row);
	}
	
	

	@Override
	public void setFilterValue(int columnIndex, String value) {
		
		int fieldIndex = getFieldIndex(columnIndex);
		
		if(fieldIndex == -1) return;
		
		if(filters == null) {
			E field = getField();
			filters = new String[field.fieldItems().size()];
		}
		
		filters[fieldIndex] = value;
		
		setFiltered();
	}

	@Override
	public String getFilterValue(int columnIndex) {
		
		if(filters == null || filters.length == 0) return null;
		
		int fieldIndex = getFieldIndex(columnIndex);
		
		logger.info("columnIndex=" + columnIndex + " fieldIndex=" + fieldIndex);
		
		if(fieldIndex == -1) return null;

		logger.info("filters[fieldIndex]=" + filters[fieldIndex]);
		
		return filters[fieldIndex];
	}
	
	
	class DateDiapason {
		
		public Date minDate;
		
		public Date maxDate;
		
		public DateDiapason(Date minDate, Date maxDate) {
			
			this.minDate = minDate;
			
			this.maxDate = maxDate;
		}
	}

}
