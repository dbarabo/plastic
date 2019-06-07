package ru.barabo.plastic.release.packet.data;

import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;
import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.release.ivr.xml.IvrInfo;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.ListenerStore;
import ru.barabo.total.db.StateRefresh;
import ru.barabo.total.db.impl.AbstractDBStore;

import java.util.ArrayList;
import java.util.List;

public class DBStorePacketContent extends AbstractDBStore<PacketContentRowField>
  implements ListenerStore<PacketRowField> {
	
	final static transient private Logger logger = Logger.getLogger(DBStorePacketContent.class.getName());
	
	final static private String SEL_CONTENT = "{ ? = call od.PTKB_PLASTIC_AUTO.getPlasticContent( ? ) }";
	
	final static private String INS_CONTENT = "{ call od.PTKB_PLASTIC_AUTO.addContent(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
	
	final static private String DELETE_APP_CONTENT = "{ call od.PTKB_PLASTIC_AUTO.deleteContentApplication(?) }";

	final static private String UPD_OUT_CLIENT_CONTENT = "{ call od.PTKB_PLASTIC_AUTO.cardToOneClient(?) }";

	final static private String PREPARE_OUT_CARD = "{ call od.PTKB_PLASTIC_AUTO.prepareOutCard(?, ?) }";

	final static private String BEFORE_PREPARE_PLATINA_OUT =
			"{ call od.PTKB_PLASTIC_AUTO.prepareCashInPlatinaComission(?, ?, ?, ?, ?, ?, ?) }";

	final static private String AFTER_PREPARE_PLATINA_OUT =
			"{ call od.PTKB_PLASTIC_AUTO.createOrExistsCashInPlatina(?, ?, ?, ?) }";


	final static private String EXEC_CHECK_REISSUE_CARD = "{ call od.PTKB_PLASTIC_AUTO.checkCardToReIssue(?, ?) }";

	final static private String DEL_CONTENT = "delete from od.ptkb_plast_pack_content where id = ?";

    final static private String SEL_TYPE_APPLICATION = "select od.PTKB_PLASTIC_AUTO.getTypeApplication( ? ) from dual";

	final static private String SEL_IS_IVR_CARD_ID = "select c.new_card from od.Ptkb_Plast_Pack_Content c "
			+ "where c.id = ? and nvl(od.ObjAttr.GetOneProp(c.app_card, od.PropClass(-120)), 0) != 0";

	protected DBStorePlastic dbStorePlastic;
	
	private Number parentId;
		
	
	public DBStorePacketContent(DBStorePlastic dbStorePlastic, DBStore<PacketRowField> packet) {
		this.dbStorePlastic = dbStorePlastic;
		this.parentId = null;
		packet.addListenerStore(this);

		setCursor(packet.getRow() );
	}

	@Override
	public List<FieldItem> getFields() {
		
		PacketContentRowField cursor = getRow();
		
		if(cursor == null) {
			cursor = new PacketContentRowField();
		}
		
		return cursor.fieldItems();
	}

	@Override
	public void setViewType(int type) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getTypeSelect() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * тип 
	 */
	private Number getTypeDocApplication(Number applicationDoc) {
		
		Object value = AfinaQuery.INSTANCE.selectValue(SEL_TYPE_APPLICATION, new Object[]{ applicationDoc });

		return !(value instanceof Number) ? null : (Number)value;
	}

	@Override
	protected List<PacketContentRowField> initData() {

		List<PacketContentRowField> data = new ArrayList<>();

		try {
			List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_CONTENT, new Object[]{parentId});

			for (Object[] row : datas) {
				data.add(PacketContentRowField.create(row) );
			}
		} catch (SessionException ignored) {
		}
		return data;
	}

	@Override
	protected PacketContentRowField createEmptyRow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PacketContentRowField cloneRow(PacketContentRowField row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void insertRow(PacketContentRowField row) {
		row.setId(AfinaQuery.nextSequence() );

		try {
			AfinaQuery.INSTANCE.execute(INS_CONTENT, row.getFieldsDB());
		} catch (SessionException ignored) {}
	}
	
	@Override
	public void removeRow() {
		PacketContentRowField row = getRow();
		if (row == null || row.getId() == null)
			return;

        try {
            AfinaQuery.INSTANCE.execute(DELETE_APP_CONTENT,
                    new Object[] { row.getId() });

            super.removeRow();
        } catch (SessionException ignored) {        }
	}

	public IvrInfo getIvrInfo() {

		if (AfinaQuery.isTestBaseConnect()) {
			return null;
		}

		PacketContentRowField field = getRow();
		if (field == null || field.getId() == null) {
			return null;
		}

		Number cardId = (Number) AfinaQuery.INSTANCE.selectValue(SEL_IS_IVR_CARD_ID, new Object[] { field.getId() });
		if (cardId == null) {
			return null;
		}

		return IvrInfo.create(cardId);
	}

	public String outClientOnly() {
		PacketContentRowField field = getRow(); 
		
		if(field == null) {
            return "Не выбрана строка содержимого пакета!";
		}
		
		if(field.getState() != StatePlasticPacket.CARD_HOME_OFFICCES.getDbValue() &&
				field.getState() != StatePlasticPacket.PREPARE_CARD_TO_OUT.getDbValue() ) {
			return DBStorePacket.STATE_NONE_GET_HOMES;
		}

        try {
            AfinaQuery.INSTANCE.execute(UPD_OUT_CLIENT_CONTENT, new Object[] {field.getId()});
        } catch (SessionException e) {
            return e.getMessage();
        }

		field.setState(StatePlasticPacket.CARD_TO_CLIENT.getDbValue());

		sendListenersCursor(field);

		PacketRowField row = dbStorePlastic.getPacket().getRow();
		setCursor(row);

		return null;
	}

	public void prepareCreditCardOut(Number limit) throws Exception {
		PacketContentRowField field = getFieldIdCheckState(StatePlasticPacket.CARD_HOME_OFFICCES,
				DBStorePacket.STATE_NONE_GET_HOMES);

		AfinaQuery.INSTANCE.execute(PREPARE_OUT_CARD, new Object[] {field.getId(), limit});

		field.setState(StatePlasticPacket.PREPARE_CARD_TO_OUT.getDbValue());

		sendListenersCursor(field);

		PacketRowField row = dbStorePlastic.getPacket().getRow();
		setCursor(row);
	}

	public PlatinaCashIn beforePreparePlatinaCardOut() throws Exception {

		PacketContentRowField field = getFieldIdCheckState(StatePlasticPacket.CARD_HOME_OFFICCES,
				DBStorePacket.STATE_NONE_GET_HOMES);

		List<Object> outParam = AfinaQuery.INSTANCE.executeOut(BEFORE_PREPARE_PLATINA_OUT, new Object[] {field.getId()},
			new int[] {OracleTypes.VARCHAR, OracleTypes.NUMBER, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR});

		logger.error("outParam=" + outParam);

		PlatinaCashIn platinaCashIn = PlatinaCashIn.createFromList(outParam, field.getName());

		if(platinaCashIn == null) throw new Exception("BEFORE_PREPARE_PLATINA_OUT return null");

		if(platinaCashIn.getLabel() != null) {
			return platinaCashIn;
		}

		field.setState(StatePlasticPacket.PREPARE_CARD_TO_OUT.getDbValue());
		sendListenersCursor(field);

		PacketRowField row = dbStorePlastic.getPacket().getRow();
		setCursor(row);

		return platinaCashIn;
	}

	public Number endPreparePlatinaCardOut(PlatinaCashIn platinaCashIn) throws Exception {
		PacketContentRowField field = getFieldIdCheckState(StatePlasticPacket.CARD_HOME_OFFICCES,
				DBStorePacket.STATE_NONE_GET_HOMES);

		List<Object> outParam = AfinaQuery.INSTANCE.executeOut(AFTER_PREPARE_PLATINA_OUT,
				new Object[] {field.getId(), platinaCashIn.getLabel(), platinaCashIn.getDescriptionDefault()},
				new int[] {OracleTypes.NUMBER});

		if(outParam == null || outParam.isEmpty()) throw new Exception("endPreparePlatinaCardOut return null");

		return (Number)outParam.get(0);
	}

	private PacketContentRowField getFieldIdCheckState(StatePlasticPacket stateMustBe,
													   String exceptionNoneState) throws Exception {
		PacketContentRowField field = getRow();

		if(field == null) throw new Exception("Не выбрана строка содержимого пакета!");

		if(stateMustBe != null && field.getState() != stateMustBe.getDbValue() ) {
			throw new Exception(exceptionNoneState);
		}

		return field;
	}
	
	public String checkReissueCard(Number parentId, Number cardId) {

        try {
            AfinaQuery.INSTANCE.execute(EXEC_CHECK_REISSUE_CARD, new Object[] { parentId, cardId });
        } catch (SessionException e) {
            return e.getMessage();
        }
        return null;
	}

	public void createReissueCardRecord(Number parentId, Number docId,
										int typeSelect) {
		final java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
		
		Number cardId = (typeSelect == 0 || typeSelect == 1) ? docId : null;
		
		Number appId = (typeSelect == 2) ? docId : null;
		
		Number typeDoc = cardId != null ? 0 : getTypeDocApplication(docId);
		
		Object[] row = new Object[]{null, parentId, typeDoc, null, cardId, appId, null, null, 0, now};
		
		PacketContentRowField field = PacketContentRowField.create(row);
		
		//logger.info("field=" + field);
		
		insertRow(field);
	}

	public PacketContentRowField createBtrt25Record(Number parentId, Number cardId) {

		final java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

		Number typeDoc = TypePacket.BTRT25.getDbValue().longValue();

		Object[] row = new Object[] { null, parentId, typeDoc, null, cardId, null, null, null, 0,
				now };

		PacketContentRowField field = PacketContentRowField.create(row);

		insertRow(field);

		return field;
	}

	@Override
	protected void updateRow(PacketContentRowField oldData,
			PacketContentRowField newData) {
		// TODO Auto-generated method stub
		
	}

	
	//////////////PARENT////////////////
	@Override
	protected void remove(PacketContentRowField row) {

        try {
            AfinaQuery.INSTANCE.execute(DEL_CONTENT, new Object[]{row.getId()});
        } catch (SessionException ignored) {}
    }

	@Override
	public void setCursor(PacketRowField row) {
		if(row == null || row.getId() == null) return;
		
		parentId =  row.getId();
		
		//logger.info(this.getClass().getName() + " setCursor parentId=" + parentId);

		setMustUpdate();
		getData();
		
	}

	@Override
	public void refreshData(List<PacketRowField> allData, StateRefresh stateRefresh) {
		if(allData == null || allData.size() == 0) return;
		
		setCursor(allData.get(0));
		
	}

}