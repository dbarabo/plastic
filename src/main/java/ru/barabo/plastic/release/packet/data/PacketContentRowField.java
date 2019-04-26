package ru.barabo.plastic.release.packet.data;

import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.Type;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.db.impl.Field;
import ru.barabo.total.db.impl.formatter.CardFormat;

import java.util.ArrayList;
import java.util.List;

public class PacketContentRowField extends AbstractRowFields {
	
	//final static transient private Logger logger = Logger.getLogger(PacketContentRowField.class.getName());

	final static public int PLASTIC_PACK_FIELD = 1;

	final static String FIELD_OFFICE = "Подразделение";

	@Override
	protected List<FieldItem> createFields() {
		List<FieldItem> fields = new ArrayList<>();

		fields.add(new Field("#id", false, Type.LONG, null, "ID", 1, 0, true));
		fields.add(new Field("#PLASTIC_PACK", false, Type.LONG, null, "PLASTIC_PACK", 1,
				PLASTIC_PACK_FIELD, true));
		fields.add(new Field("Тип", true, Type.LONG,
				new String[] { "Перевыпуск", "Выпуск", "SMS+", "SMS-", "BTRT25", "Неименованная" }, "TYPE_PACK", 80,
				2, true,
				new Integer[] { 0, 1, 2, 3, 4, 5 }));
		fields.add(new Field("#PRODUCT_TO", false, Type.LONG, null, "PRODUCT_TO", 1, 3, true));
		fields.add(new Field("#CARD", false, Type.LONG, null, "CARD", 1, 4, true));
		fields.add(new Field("id Заявления", true, Type.LONG, null, "APP_CARD", 80, 5, true));
		fields.add(new Field("#PERSON", false, Type.LONG, null, "PERSON", 1, 6, true));
		fields.add(new Field("#CUSTOMER", false, Type.LONG, null, "CUSTOMER", 1, 7, true));
		fields.add(new Field("Статус", true, Type.LONG,
				new String[]{"Новый","Выпуск","Отправлен", "Отправка-Ок", 
				"Отправка-Error", "Ответ-Ок", "?", "Error-Ответ", "?", "OCI-Ok","?", 
				"SMS-Отправка","SMS-Ok", "SMS-Error","SMS-Oтвет-Оk", 
				"SMS-Ответ Част Ок", "SMS-Oтвет-Error","SMS-Oтвет-Error Част","Карты в ГО", "Ушли в доп.офисы",
				"Карты в Доп. офисах", "Выдано клиенту"}, 
				"STATE", 90, 8, true, new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
						15, 16, 17, 18, 19, 20, 21 }));
		
		fields.add(new Field("Добавлен", true, Type.DATE, null, "CREATED", 80, 9, true));
		
		fields.add(new Field("#NEW_CARD", false, Type.LONG, null, null, 0, 10, true));
		
		fields.add(new Field("Держатель", true, Type.STRING, null, null, 250, 11, true));
		fields.add(new Field("Тип карты", true, Type.STRING, null, null, 170, 12, true));
		fields.add(new Field("Старая карта", true, Type.STRING, null, null, 150, 13, true, null,
				new CardFormat()));
		fields.add(new Field("Старая до", true, Type.DATE, null, null, 80, 14, true));
		fields.add(new Field("Ответ", true, Type.STRING, null, null, 100, 15, true));
		fields.add(new Field("Телефон-СМС", true, Type.STRING, null, null, 130, 16, true));
		fields.add(new Field("Новая карта", true, Type.STRING, null, null, 150, 17, true, null,
				new CardFormat()));
		
		fields.add(new Field(FIELD_OFFICE, true, Type.STRING, null, null, 100, 18, true));

		fields.add(new Field("№ Заявления", true, Type.STRING, null, null, 60, 19, true));

		return fields;
	}

	public int getApplicationId() {

		Number value = (Number) fieldItems().get(5).getVal();

		return value == null ? -1 : value.intValue();
	}

	public String getName() {
		return (String)fieldItems().get(11).getVal();
	}
	
	public int getState() {
		Number val = (Number)fieldItems().get(8).getVal();
		
		return val == null ? -1 : val.intValue();
	}
	
	void setState(Number state) {
		fieldItems().get(8).setValueFieldObject(state);
	}
	
	
	static public PacketContentRowField create(Object[] row) {
		PacketContentRowField content = new PacketContentRowField();
		
		for (int index = 0; index < row.length; index++) {
			content.fieldItems().get(index).setValueFieldObject(row[index] );
		}

		return content;
	}

}