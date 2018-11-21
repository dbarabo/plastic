package ru.barabo.plastic.release.packet.data;

import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.Type;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.db.impl.Field;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PacketRowField extends AbstractRowFields {
	
	//final static transient private Logger logger = Logger.getLogger(PacketRowField.class.getName());

	final static String FIELD_UPDATED = "Изменен";

	@Override
	protected List<FieldItem> createFields() {
		List<FieldItem> fields = new ArrayList<>();

		fields.add(new Field("#id", false, Type.LONG, null, "ID", 1, 0, true));
		fields.add(new Field("Автор", true, Type.STRING, null, "CREATOR", 50, 1, true, null, null));
		fields.add(new Field("Создан", true, Type.DATE, null, "CREATED", 80, 2, true, null,
				new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")));
		fields.add(new Field("Наименование", true, Type.STRING, null, "NAME", 240, 3, true));
		fields.add(new Field("Изменил", true, Type.STRING, null, "UPDATER", 60, 4, true, null, null));
		fields.add(new Field("Состояние", true, Type.LONG,
				new String[]{"Новый","Выпущен","Отправлен","Отправка Ок","Отправка Error",
				"Ответ Всё Ок", "Ответ Част Ок","Error-Ответ Всё", "Error-Ответ Част",
				"OCI-ВСЁ", "OCI-Част", "SMS-Отправка","SMS-Ok", "SMS-Error", "SMS-Oтвет-Оk", 
				"SMS-Ответ Част Ок", "SMS-Oтвет-Error","SMS-Oтвет-Error Част", "Карты в ГО", "Ушли в доп.офисы", 
				"Карты в Доп. офисах", "Выдано клиенту"}, 
				"STATE", 120, 5, true, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21}));
		fields.add(new Field(FIELD_UPDATED, true, Type.DATE, null, "UPDATED", 100, 6, true, null,
				new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")));
		fields.add(new Field("Файл заявлений", true, Type.STRING, null, "APP_FILE", 270, 7, true,
				null));
		fields.add(new Field("Загруженные файлы", true, Type.STRING, null, "LOAD_FILES", 350, 8,
				true, null));

		fields.add(new Field("#TYPE_PACKET", false, Type.LONG, null, "TYPE_PACKET", 1, 9, true));
	
		return fields;
	}
	
	public void setTypePacket(Number typePacket) {

		fieldItems().get(9).setValueFieldObject(typePacket);
	}

	public int getState() {
		Number val = (Number)fieldItems().get(5).getVal();
		
		return val == null ? -1 : val.intValue();
	}
	
	public void setState(Number state) {
		fieldItems().get(5).setValueFieldObject(state);
	}
	
	public void setFileApp(String fileName) {
		fieldItems().get(7).setValueFieldObject(fileName);
	}
	
	public void setName(String name) {
		fieldItems().get(3).setValueFieldObject(name);
	}
	
	public String getName() {
		return (String)fieldItems().get(3).getVal();
	}
	
	
	static public PacketRowField create(Object[] row) {
		PacketRowField finBankRow = new PacketRowField();
		
		for(int index = 0; index < finBankRow.fieldItems().size(); index++) {
			finBankRow.fieldItems().get(index).setValueFieldObject(row[index] );
		}

		return finBankRow;
	}

}
