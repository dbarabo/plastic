package ru.barabo.plastic.release.application.data;

import org.apache.log4j.Logger;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.Type;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.db.impl.Field;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClientRowField extends AbstractRowFields {
	
	final static transient private Logger logger = Logger
			.getLogger(ClientRowField.class.getName());

	final static transient int ID_INDEX = 0;

	final static transient int LABEL_INDEX = 1;

	final static transient int ADDRESS_INDEX = 2;

	final static transient int INN_INDEX = 3;

	final static transient int SERIA_INDEX = 4;

	final static transient int NUMBER_INDEX = 5;

	final static transient int KOGDA_INDEX = 6;

	final static transient int KEM_INDEX = 7;

	final static transient int BIRTHDAY_INDEX = 8;

	final static transient int PHONE_INDEX = 9;

	@Override
	protected List<FieldItem> createFields() {
		
		List<FieldItem> fields = new ArrayList<>();

		fields.add(new Field("#id", false, Type.LONG, null, null, 0, fields.size(), true));
		
		fields.add(new Field("#label", false, Type.STRING, null, null, 0, fields.size(),
				true));

		fields.add(new Field("#address", false, Type.STRING, null, null, 0,
				fields.size(), true));

		fields.add(new Field("#inn", false, Type.STRING, null, null, 0, fields.size(),
				true));
		
		fields.add(new Field("#seria", false, Type.STRING, null, null, 0, fields.size(),
				true));

		fields.add(new Field("#number", false, Type.STRING, null, null, 0, fields.size(),
				true));

		fields.add(new Field("#kogda", false, Type.DATE, null, null, 0, fields.size(),
				true, null, new SimpleDateFormat("dd.MM.yyyy")));

		fields.add(new Field("#kem", false, Type.STRING, null, null, 0, fields.size(),
				true));

		fields.add(new Field("#birthday", false, Type.DATE, null, null, 0, fields.size(),
				true, null, new SimpleDateFormat("dd.MM.yyyy")));

		fields.add(new Field("#phone", false, Type.STRING, null, null, 0, fields.size(),
				true));

		return fields;
	}

	static public ClientRowField create(Object[] row) {
		ClientRowField field = new ClientRowField();

		for (int index = 0; index < field.fieldItems().size(); index++) {
			try {
				// logger.info("index = " + index);
				// logger.info("row[index] = " + row[index]);
				field.fieldItems().get(index).setValueFieldObject(row[index]);
			} catch (java.lang.ClassCastException e) {
				logger.error("index = " + index);
				logger.error(e);

				throw (e);
			}
		}

		return field;
	}

	public String getAddress() {
		return (String) fieldItems().get(2).getVal();
	}

	public String getInn() {
		return (String) fieldItems().get(3).getVal();
	}

	public String getSeria() {
		return (String) fieldItems().get(4).getVal();
	}

	public String getNumber() {
		return (String) fieldItems().get(5).getVal();
	}
}
