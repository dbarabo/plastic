package ru.barabo.plastic.release.sms.select.data;

import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.Type;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.db.impl.Field;
import ru.barabo.total.db.impl.formatter.CardFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SmsSelectField extends AbstractRowFields {
	
	//final static transient private Logger logger = Logger.getLogger(SmsSelectField.class.getName());

	@Override
	protected List<FieldItem> createFields() {
		List<FieldItem> fields = new ArrayList<>();

		
		fields.add(new Field("#id", false, Type.LONG, null, "DOC", 1, 0, true));
		fields.add(new Field("№ Карты", true, Type.STRING, null, "cardnum", 140, 1, true, null,
				new CardFormat()));
		
		fields.add(new Field("Держатель", true, Type.STRING, null, "PERSON", 350, 2, true));
		fields.add(new Field("Владелец", true, Type.STRING, null, "CUSTOMER", 350, 3, true));
		fields.add(new Field("Окончание", true, Type.DATE, null, "", 70, 4, true, null,
				new SimpleDateFormat("dd.MM.yyyy")));
		
		fields.add(new Field("Начало", true, Type.DATE, null, "", 70, 5, true, null,
				new SimpleDateFormat("dd.MM.yyyy")));
		
		fields.add(new Field("id_cardtype", false, Type.LONG, null, "", 1, 6, true));
		fields.add(new Field("Продукт", true, Type.STRING, null, "", 230, 7, true));
		fields.add(new Field("Автор", false, Type.STRING, null, "", 0, 8, true));
		fields.add(new Field("Телефон", true, Type.STRING, null, "", 100, 9, true));
	
		return fields;
	}
	
	static public SmsSelectField create(Object[] row) {
		SmsSelectField finBankRow = new SmsSelectField();
		
		for(int index = 0; index < finBankRow.fieldItems().size(); index++) {
			finBankRow.fieldItems().get(index).setValueFieldObject(row[index] );
		}

		return finBankRow;
	}
	
	public String getPhone() {
		return (String)fieldItems().get(9).getVal();
	}
}
