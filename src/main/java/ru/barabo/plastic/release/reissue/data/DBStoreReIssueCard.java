package ru.barabo.plastic.release.reissue.data;

import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.release.ivr.xml.IvrInfo;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.packet.data.DBStorePacket;
import ru.barabo.plastic.release.packet.data.DBStorePacketContent;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.impl.AbstractFilterStore;

import java.util.ArrayList;
import java.util.List;

public class DBStoreReIssueCard extends AbstractFilterStore<ReIssueCardRowField> {
	
	//final static private String SEL_REISSUE_CARD = "{ ? = call od.PTKB_PLASTIC_AUTO.getCardToReIssue }";
	
	final static protected String SEL_TIME =  "{ ? = call od.PTKB_PLASTIC_AUTO.getCardToReIssue }";
	
	final static protected String SEL_LOST = "{ ? = call od.PTKB_PLASTIC_AUTO.getCardActiveCardNow }"; // "{ ? = call od.PTKB_PLASTIC_AUTO.getCardToReIssueLost }";
	
	final static protected String SEL_APPLICATION = "{ ? = call od.PTKB_PLASTIC_AUTO.getAppCardToSend }";   
	
	private final static String NOT_SELECTED_CARDS = "Не выбрано ни одной карты в таблице!";

	private final static String NOT_PINCODE_FILTER = "Для смены Пин-кода тип отбора должен стоять <Отбор: Карты по утере/краже>";

	private TypeSelect typeSelect;

	private DBStorePlastic dbStorePlastic;
	
	public DBStoreReIssueCard(DBStorePlastic dbStorePlastic) {

		this.dbStorePlastic = dbStorePlastic;

		typeSelect = TypeSelect.values()[0];
	}

	@Override
	public List<FieldItem> getFields() {
		
		ReIssueCardRowField cursor = getRow();
		
		if(cursor == null) {
			cursor = new ReIssueCardRowField();
		}
		
		return cursor.fieldItems();
	}
	
	public DBStorePacket getDBStorePacket() {
		return (DBStorePacket)dbStorePlastic.getPacket();
	}
	
	public DBStorePacketContent getDBStorePacketContent() {
		return (DBStorePacketContent)dbStorePlastic.getContent();
	}

	public IvrInfo getIvrInfo() {
		if (typeSelect != TypeSelect.Lost) {
			return null;
		}

		ReIssueCardRowField field = getRow();
		if (field == null || field.getId() == null) {
			return null;
		}

		return IvrInfo.create(field.getId());
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
	public void moveRow(int rowFrom, int rowTo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<ReIssueCardRowField> initData() {

		List<ReIssueCardRowField> data = new ArrayList<>();

		try {
			List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(typeSelect.getSelect(), null);

			for (Object[] row : datas) {
				data.add(ReIssueCardRowField.create(row) );
			}
		} catch (SessionException ignored) {
		}

		return data;
	}

	@Override
	protected ReIssueCardRowField createEmptyRow() {
		return new ReIssueCardRowField();
	}

	@Override
	protected ReIssueCardRowField cloneRow(ReIssueCardRowField row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void insertRow(ReIssueCardRowField row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateRow(ReIssueCardRowField oldData,
			ReIssueCardRowField newData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void remove(ReIssueCardRowField row) {
		// TODO Auto-generated method stub
		
	}
	
	public void refreshData() {
		this.setMustUpdate();
		this.getData();
	}
	

}

