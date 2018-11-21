package ru.barabo.plastic.release.reissue.data;

import org.apache.log4j.Logger;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.Type;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.db.impl.Field;
import ru.barabo.total.db.impl.formatter.CardFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ReIssueCardRowField extends AbstractRowFields {
	
	final static transient private Logger logger = Logger.getLogger(ReIssueCardRowField.class.getName());

	@Override
	protected List<FieldItem> createFields() {
		List<FieldItem> fields = new ArrayList<>();

		
		fields.add(new Field("#id", false, Type.LONG, null, "DOC", 1, 0, true));
		fields.add(new Field("№ Карты/Заявления", true, Type.STRING, null, "cardnum", 140, 1, true,
				null,
				new CardFormat()));
		
		fields.add(new Field("Держатель", true, Type.STRING, null, "PERSON", 350, 2, true));
		fields.add(new Field("Владелец", true, Type.STRING, null, "CUSTOMER", 350, 3, true));
		fields.add(new Field("Окончание", true, Type.DATE, null, "", 70, 4, true, null,
				new SimpleDateFormat("dd.MM.yyyy")	) );
		
		fields.add(new Field("Начало", true, Type.DATE, null, "", 70, 5, true, null,
				new SimpleDateFormat("dd.MM.yyyy")) );
		
		fields.add(new Field("id_cardtype", false, Type.LONG, null, "", 1, 6, true));
		fields.add(new Field("Продукт", true, Type.STRING, null, "", 230, 7, true));
		fields.add(new Field("Автор", true, Type.STRING, null, "", 60, 8, true));
	
		return fields;
	}
	
	static public String formatCard(Object val) {
		String txt = (String)val;
		
		if(txt == null || txt.length() < 16) return txt;
		
		  return String.format("%s %s %s %s", 
				   txt.substring(0, 4), txt.substring(4, 8), 
				   txt.substring(8, 12), txt.substring(12, 16));
	}
	
	
	static public ReIssueCardRowField create(Object[] row) {
		ReIssueCardRowField finBankRow = new ReIssueCardRowField();
		
		for(int index = 0; index < finBankRow.fieldItems().size(); index++) {
			finBankRow.fieldItems().get(index).setValueFieldObject(row[index] );
		}

		return finBankRow;
	}
}
