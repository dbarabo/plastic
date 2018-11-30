package ru.barabo.plastic.release.sms.packet.data;


import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;
import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.packet.data.*;
import ru.barabo.total.db.StateRefresh;
import ru.barabo.total.utils.Util;

import java.util.List;


public class DBStoreSmsPacket extends DBStorePacket {
	
	final static transient private Logger logger = Logger.getLogger(DBStoreSmsPacket.class.getName());
	
	final static private String INS_PACKET = "insert into od.PTKB_PLASTIC_PACK "
			+ " (ID, CREATOR, CREATED, NAME, UPDATER, STATE, UPDATED, APP_FILE, LOAD_FILES, TYPE_PACKET) values "
			+ " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	final static private String SEL_NEW_PACKET = 
			"select max(ID) from od.PTKB_PLASTIC_PACK where state = 0 and TYPE_PACKET in (1, 2)";
	
	final static private String SEL_PACKET_COND = 
			"select ID, CREATOR, CREATED, NAME, UPDATER, STATE, UPDATED, APP_FILE, LOAD_FILES, TYPE_PACKET "
			+ "from od.PTKB_PLASTIC_PACK "
			+ "where CREATOR = %s and STATE >= ? and STATE <= ? "
			+ "and TYPE_PACKET = 1 "
			+ "order by UPDATED desc";
	
	final static private String CREATE_FILE_SMS = "{ call od.PTKB_PLASTIC_AUTO.createSmsFileDataApp(?, ?, ?) }";
	
	final static private String TO_SMS_STATE = "{ call od.PTKB_PLASTIC_AUTO.fromSmsSentToNew(?) }";


	public DBStoreSmsPacket(DBStorePlastic dbStorePlastic) {
		super(dbStorePlastic);
	}
	
	@Override
	protected String getSelectPacketData() {
		return SEL_PACKET_COND;
	}
	
	@Override
	protected void insertRow(PacketRowField row) {
		row.setId(AfinaQuery.nextSequence());

		row.setTypePacket(1);

		try {
			AfinaQuery.INSTANCE.execute(INS_PACKET, row.getFields());
		} catch (SessionException ignored) {
		}
	}
	
	private DBStoreSmsPacketContent getDBStoreSmsPacketContent() {
		
		return dbStorePlastic.getSmsContent();
	}
	
	@Override
	public DBStorePacketContent getDBStorePacketContentPacket() {
		return getDBStoreSmsPacketContent();
	}

	@Override
	protected Number getNewStateIdPack(TypePacket typePacket) {
		
		Object value = AfinaQuery.INSTANCE.selectValue(SEL_NEW_PACKET, null);

		return !(value instanceof Number) ? null : (Number)value;
	}
	
	public String toSmsState() {
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}

        try {
            AfinaQuery.INSTANCE.execute(TO_SMS_STATE, new Object[] { field.getId() });
        } catch (SessionException e) {
            return e.getMessage();
        }

		field.setState(StatePlasticPacket.NEW.ordinal());
		
		DBStoreSmsPacketContent content = getDBStoreSmsPacketContent();
		content.refreshData(getData(), StateRefresh.ALL);
		
		return null;
	}
	
	
	public String createSms() {
		
		PacketRowField field = getRow();
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}
		
		String fileName = getAppFile();

        List<Object> values;
        try {
            values = AfinaQuery.INSTANCE.execute(CREATE_FILE_SMS, new Object[]{field.getId(), fileName}, new int[]{OracleTypes.CLOB});
        } catch (SessionException e) {
            return e.getMessage();
        }

		if(values == null || values.size() == 0 || values.get(0) == null) {
			return null;
		}
		
		String data;
		try {
			data = Util.clob2string(( java.sql.Clob )values.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			logger.error(e);
			
			return e.getMessage(); 
		}
		
		String path = Util.getPathFileOut() + "/" + fileName;
		
		String error = Util.writeTextFile(path, data, "cp1251");
		if(error != null) {
			return error;
		}
				
		field.setState(StatePlasticPacket.SMS_SENT.ordinal() );
		field.setFileApp(fileName);

		updateAllData();

		DBStoreSmsPacketContent content = getDBStoreSmsPacketContent();
		content.refreshData(getData(), StateRefresh.ALL);
		
		return null;
	}

	
}

