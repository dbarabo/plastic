package ru.barabo.plastic.release.sms.packet.data;

import org.apache.log4j.Logger;
import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.packet.data.DBStorePacketContent;
import ru.barabo.plastic.release.packet.data.PacketContentRowField;
import ru.barabo.plastic.release.packet.data.PacketRowField;
import ru.barabo.total.db.DBStore;

public class DBStoreSmsPacketContent extends DBStorePacketContent {
	
	final static transient private Logger logger = Logger.getLogger(DBStoreSmsPacketContent.class.getName());
	
	final static public String UPD_PHONE_CARD = "{ call od.PTKB_PLASTIC_AUTO.updatePhoneCard(?, ?) }";

	public DBStoreSmsPacketContent(DBStorePlastic dbStorePlastic, DBStore<PacketRowField> packet) {
		super(dbStorePlastic, packet);
	}
	
	public PacketContentRowField createSmsRecord(Number parentId, Number newCardId,
												 int typeOnOff, String newPhone) {
		
		if(newPhone != null) {
			String error = saveNewPhone(newPhone, newCardId);
			
			if(error != null) {
				logger.error(error);
				return null;
			}
		}
		
		final java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
		
		Number typeDoc = (typeOnOff == 0) ? 2 : 3;
		
		Object[] row = new Object[]{null, parentId, typeDoc, null, newCardId, null, null, null, 0, now};
		
		PacketContentRowField field = PacketContentRowField.create(row);
		insertRow(field);
		
		return field;
	}
	
	private String saveNewPhone(String newPhone, Number newCardId) {

		try {
			AfinaQuery.INSTANCE.execute(UPD_PHONE_CARD, new Object[] { newPhone, newCardId });
		} catch (SessionException e) {
			return e.getMessage();
		}
		return null;
	}

}
