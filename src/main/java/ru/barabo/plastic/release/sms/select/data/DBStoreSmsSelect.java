package ru.barabo.plastic.release.sms.select.data;

import ru.barabo.afina.AfinaQuery;
import ru.barabo.db.SessionException;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacket;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacketContent;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.impl.AbstractFilterStore;

import java.util.ArrayList;
import java.util.List;

public class DBStoreSmsSelect extends AbstractFilterStore<SmsSelectField> {
	
	private final static String SEL_SMS_ADD = "{ ? = call od.PTKB_PLASTIC_AUTO.getCardToSmsAdd }";
	private final static String SEL_SMS_REMOVE = "{ ? = call od.PTKB_PLASTIC_AUTO.getCardToSmsRemove }";
	
	private DBStorePlastic dbStorePlastic;
	
	private TypeSelect typeSelect;
	
	public DBStoreSmsSelect(DBStorePlastic dbStorePlastic) {
		this.dbStorePlastic = dbStorePlastic;
		
		typeSelect = TypeSelect.values()[0];
	}
	
	public DBStoreSmsPacket getDBStoreSmsPacket() {
		
		return dbStorePlastic.getSmsPacket();
	}
	
	public DBStoreSmsPacketContent getDBStoreSmsPacketContent() {
		
		return dbStorePlastic.getSmsContent();
	}

	@Override
	public List<FieldItem> getFields() {
		
		SmsSelectField cursor = getRow();
		
		if(cursor == null) {
			cursor = new SmsSelectField();
		}
		
		return cursor.fieldItems();
	}

	@Override
	public void setViewType(int type) {
		typeSelect = TypeSelect.values()[type];
		
		setMustUpdate();
		getData();
		
	}

	@Override
	public int getTypeSelect() {
		return typeSelect.ordinal();
	}

	@Override
	protected List<SmsSelectField> initData() {
		try {
			List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(typeSelect.getSelect(), null);

			List<SmsSelectField> data = new ArrayList<>();

			for (Object[] row : datas) {
				data.add(SmsSelectField.create(row) );
			}

			return data;
		} catch (SessionException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected SmsSelectField createEmptyRow() {
		return new SmsSelectField();
	}

	@Override
	protected SmsSelectField cloneRow(SmsSelectField row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void insertRow(SmsSelectField row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateRow(SmsSelectField oldData,
			SmsSelectField newData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void remove(SmsSelectField row) {
		// TODO Auto-generated method stub
		
	}

	enum TypeSelect {
		SMS_ADD(DBStoreSmsSelect.SEL_SMS_ADD),
		SMS_REMOVE(DBStoreSmsSelect.SEL_SMS_REMOVE);
			
		private String select;
		
		TypeSelect(String sel) {
			select = sel;
		}
		
		public String getSelect() {
			return select;
		}
	}
}

