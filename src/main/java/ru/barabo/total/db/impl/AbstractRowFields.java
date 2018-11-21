package ru.barabo.total.db.impl;

import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;


import org.apache.log4j.Logger;
import ru.barabo.total.db.FieldItem;

public abstract class AbstractRowFields {

	final static transient private Logger logger = Logger.getLogger(AbstractRowFields.class.getName());
	
	private List<FieldItem> fields;
	
	abstract protected List<FieldItem> createFields();
	
	private Object getSqlVal(FieldItem field) {

		return Optional.ofNullable(field.getVal()).orElse(field.getClazz().getClazz());
	}

	/**
	 * @return список столбцов ч/з запятую
	 */
	public String fieldsAsList() {

		return fieldItems().stream()
				.filter(f -> isDbColumn(f.getColumn(), f.getVal(), false))
				.map(f -> f.getColumn())
				.collect(Collectors.joining(", "));
	}

	public Object[] getValuesFieldDB(boolean isInsert) {

		List<Object> row = fieldItems().stream().skip(isInsert ? 0 : 1)
				.filter(f -> isDbColumn(f.getColumn(), f.getVal(), isInsert))
				.map(this::getSqlVal).collect(Collectors.toList());

		if (isInsert) {
			return row.toArray();
		}

		row.add(getSqlVal(fieldItems().get(0)));

		return row.toArray();
	}

	public boolean isDbColumn(String columnName, Object value, boolean isInsert) {
		return columnName != null && Character.isLetter(columnName.charAt(0)) &&
				((!isInsert) || value != null);
	}

	static public <T extends AbstractRowFields> T create(Class<T> clazz) {

		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("create", e);
		}

		return null;
	}

	static public <T extends AbstractRowFields> T create(Object[] row, Class<T> rowClazz) {
		T rowField = create(rowClazz);

		for (int index = 0; index < Math.min(rowField.fieldItems().size(), row.length); index++) {
			rowField.fieldItems().get(index).setValueFieldObject(row[index]);
		}

		return rowField;
	}

	public AbstractRowFields() {
		fields = createFields();
	}
	
	public List<FieldItem> fieldItems() {
		return fields;
	}
	
	public FieldItem getFieldByColumn(String column) {

		if (column == null) {
			return null;
		}

		for (FieldItem field : fieldItems()) {
			if (column.equals(field.getLabel())) {
				return field;
			}
		}

		return null;
	}

	public FieldItem getFieldByLabel(String label) {

		for (FieldItem field : fieldItems()) {
			if (label.equals(field.getLabel())) {
				return field;
			}
		}

		return null;
	}

	public String getRowString() {
		String result = "";
		
		for(FieldItem field : fields) {
			if(field.isExistsGrid()) {
				result += (field.getValueField() == null ? "" : field.getValueField()) + "\t";
			}
		}
		
		return result;
	}
		
	public void cloneTo(AbstractRowFields destRow) {
		
		for(int index = 0; index < fields.size(); index++) {
			destRow.fields.get(index).setValueFieldObject(fields.get(index).getVal() );
		}
	}

	public Object[] getFields() {
		Object[] row = new Object[fields.size()];
		
		for(int index = 0; index < fields.size(); index++) {
			row[index] = fields.get(index).getVal();
		}
		return row;
	}
	
	public long getFieldDBCount() {
		
		return fields.stream().filter(field -> field.getColumn() != null).count();
		
	}
	
	public Object[] getFieldsDB() {
		
		Object[] row = new Object[(int)getFieldDBCount()];
		
		int index = 0;
		for(FieldItem field : fields) {
			if(field.getColumn() != null) {
				row[index] = field.getVal();
				index++;
			}
		}
		return row;
	}
	
	private boolean isEqual(Object val1, Object val2) {
		if(val1 == val2) return true;
		
		if(val1 == null || val2 == null) return false;
		
		return val1.equals(val2);
	}

	public String getUpdateData(AbstractRowFields oldData, 
			Vector<Object> values) {
		
		String result = null;
		
		for(int index = 1; index < fields.size(); index++) {
			
			if(!isEqual(fields.get(index).getVal(), 
					oldData.fields.get(index).getVal()) ) {
				
				values.add(fields.get(index).getVal());
				
				if(result != null) {
					result += ", ";
					result += fields.get(index).getColumn() + " = ?";
					
				}  else {
					result = fields.get(index).getColumn() + " = ?";
				}
			}
		}
		
		values.add(fields.get(0).getVal());
		return result;
	}
	
	
	
	public Number getId() {
		return (Number)fields.get(0).getVal();
	}
	
	public void setId(Number id) {
		fields.get(0).setValueFieldObject(id);
	}
	
	public String getName() {
		return (String)fields.get(1).getVal();
	}
	
	public String getAccounts() {
		return (String)fields.get(7).getVal();
	}
	
	public String getAccountsPercent() {
		
		String res = (String)fields.get(8).getVal();
		if(res == null || "".equals(res)) return null;
		
		Number perc = (Number)fields.get(9).getVal();
		if(perc == null) return null;
				
		int val = (int) (perc.doubleValue()*100);
		
		String percTak = "/" + val + "\n";
		
		res = res.replace(",", percTak);
		
		res = res.replace("'", "");
		
		res += percTak;
		
		return res;
	}
	
	public void setName(String name) {
		fields.get(1).setValueFieldObject(name);
	}
	
	public int getTypePl() {
		Number val = (Number)fields.get(2).getVal();
		
		return val == null ? -1 : val.intValue();
	}
	
	public void setTypePl(Number typePl) {
		fields.get(2).setValueFieldObject(typePl);
	}
	
	public Number getOrd() {
		return (Number)fields.get(3).getVal();
	}
	
	public void setOrd(Number ord) {
		fields.get(3).setValueFieldObject(ord);
	}
}
