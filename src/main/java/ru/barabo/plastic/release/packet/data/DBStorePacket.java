package ru.barabo.plastic.release.packet.data;

import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;
import ru.barabo.afina.AfinaQuery;
import ru.barabo.db.SessionException;
import ru.barabo.plastic.main.resources.owner.Cfg;
import ru.barabo.plastic.release.application.data.DBStoreApplicationCard;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.StateRefresh;
import ru.barabo.total.db.impl.AbstractFilterStore;
import ru.barabo.total.utils.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBStorePacket extends AbstractFilterStore<PacketRowField> {
	
	final static transient private Logger logger = Logger.getLogger(DBStorePacket.class.getName());

	public static final String PACKET_NOT_SELECTED = "Не выбрана строка пакета!";

	public static final String STATE_NONE_NEW = "Перевыпуск возможен только в состоянии 'НОВЫЙ'";

	public static final String ERROR_CHANGE_PRODUCT_STATE = "Для смены продукта пакет может находится только в состоянии 'НОВЫЙ'";
	
	private static final String STATE_NONE_DELETE_DRAFT = "Нельзя удалять пакет с 'Черновиками'";

	public static final String STATE_NONE_ISSUE = "Создание файла возможно только в состоянии 'ВЫПУЩЕН'";
	
	public static final String ERROR_CREATE_FILE = "Ошибка при формировании тела файла";
	
	private static final String PACKET_NEW_NOT_SENT = "Неготовые к отправке";
	
	public final static String BTRT25_PACKET_NAME = "Отправить BTRT25";

	private static final String SEL_MAX_DATE = "select max_date from dual";

	final static private String SEL_NEW_PACKET = 
			"select max(ID) from od.PTKB_PLASTIC_PACK where state = 0 and TYPE_PACKET = ? and CREATOR = user and nvl(UPDATED, min_date) != max_date";
	
	final static private String SEL_DRAFT_PACKET =
			"select max(ID) from od.PTKB_PLASTIC_PACK where state = 0 and TYPE_PACKET = 0 and UPDATED = max_date";

	final static private String SEL_FILENAME = "select od.PTKB_PLASTIC_AUTO.getFileNameIIA from dual";
	
	final static private String INS_PACKET = "insert into od.PTKB_PLASTIC_PACK "
			+ " (ID, CREATOR, CREATED, NAME, UPDATER, STATE, UPDATED, APP_FILE, LOAD_FILES, TYPE_PACKET) values "
			+ " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	final static private String UPD_RENAME_PACKET = "update od.PTKB_PLASTIC_PACK set NAME = ?, UPDATER = user, UPDATED = sysdate where id = ?";
	
	final static public String REISSUE_CARDS = "{ call od.PTKB_PLASTIC_AUTO.reissueCards(?) }";
	
	final static private String CREATE_FILE = "{ call od.PTKB_PLASTIC_AUTO.createAppFileData(?, ?, ?) }";
	
	final static private String CREATE_BTRT25 = "{ call od.PTKB_PLASTIC_AUTO.createBtrt25FileData(?, ?, ?) }";

	final static public String TO_SMS_STATE = "{ call od.PTKB_PLASTIC_AUTO.returnToState(?, ?, ?) }";
	
	final static public String TO_CREATE_FILE_STATE = "{ call od.PTKB_PLASTIC_AUTO.returnToCreateFileState(?, ?, ?, ?) }";
	
	final static private String UPD_REMOVE_PACKET =  "{ call od.PTKB_PLASTIC_AUTO.removePacket(?) }";
	
	final static public String UPD_GO_HOME_PACKET =  "{ call od.PTKB_PLASTIC_AUTO.goHomePacket(?) }";

	final static public String UPD_TO_DOPIK_PACKET = "{ call od.PTKB_PLASTIC_AUTO.toDopikPacket(?, ?) }";

	final static public String UPD_GET_HOMES_PACKET =  "{ call od.PTKB_PLASTIC_AUTO.getHomesPacket(?) }";
	
	final static private String UPD_OUT_CLIENT_PACKET =  "{ call od.PTKB_PLASTIC_AUTO.cardToClient(?) }";
	
	final static public String STATE_NONE_SMS_OIA = "Перевод в 'ГО' может быть только из состояния 'SMS-Oтвет-Оk'";
	
	final static public String STATE_NONE_GO_HOME = "Перевод в Доп. офисы может быть только из состояния 'Карты в ГО'";
	
	final static public String STATE_NONE_TO_DOPIKI = "Перевод в 'Получен в Доп. офисах' может быть только из состояния 'Отправлено в Доп. офисы '";
	
	final static public String STATE_NONE_GET_HOMES = "Перевод в 'Выдано клиенту' может быть только из состояния 'Получен в Доп. офисах'";

	final static private String SEL_PACKET_COND = 
			"select ID, CREATOR, CREATED, NAME, UPDATER, STATE, UPDATED, APP_FILE, LOAD_FILES, TYPE_PACKET "
			+ "from od.PTKB_PLASTIC_PACK "
			+ "where CREATOR = %s and STATE >= ? and STATE <= ? "
					+ "and TYPE_PACKET in (0, 4) "
			+ "order by UPDATED desc";
	
	final static private String SEL_PACKET_DOPIK = "select ID, CREATOR, CREATED, NAME, UPDATER, STATE, UPDATED, APP_FILE, LOAD_FILES, TYPE_PACKET "
			+ "from od.PTKB_PLASTIC_PACK "
			+ "where CREATOR = %s and STATE >= ? and STATE <= ? "
			+ "and TYPE_PACKET = 0 "
			+ "and STATE >= " + StatePlasticPacket.CARD_SENT_OFFICCES.getDbValue() //19, 20, 21) "
			+ " order by UPDATED desc";
	
	private final static String SEL_FROM_PRODUCTS_NAME =
			"{ ? = call od.PTKB_PLASTIC_AUTO.getCardProductPacket( ? ) }";
	
	private final static String SEL_TO_PRODUCTS_NAME =
			"{ ? = call od.PTKB_PLASTIC_AUTO.getCardProductOutPacket( ?, ? ) }";

	public final static String UPD_CHANGE_PRODUCT = "{ call od.PTKB_PLASTIC_AUTO.changeProductPacket(?, ?, ?) }";

	protected DBStorePlastic dbStorePlastic;

	private SelectTypes selectTypes;
	private String selMyUser;
	private StatePlasticPacket selStateStart;
	private StatePlasticPacket selStateEnd;
	
	public DBStorePacket(DBStorePlastic dbStorePlastic) {
		this.dbStorePlastic = dbStorePlastic;
		selectTypes = SelectTypes.ALL;
		selMyUser = selectTypes.getSelUser(); 
		
		selStateStart = StatePlasticPacket.getMinValue();
		selStateEnd = StatePlasticPacket.getMaxValue();

		userWorkPlace = getWorkPlace();
	}

	private long userWorkPlace;

	private final static String SEL_WORK_PLACE = "select od.PTKB_PLASTIC_AUTO.getPlasticWorkPlace from dual";

	private final static long MAIN_PK_WORK_PLACE = 1005956055;
	private final static long BOSS_PK_WORK_PLACE = 1176984896;
	private final static long SUPER_WORK_PLACE = 1000005945;

	public boolean isWorkPlaceDopik() {

		return !(userWorkPlace == SUPER_WORK_PLACE || userWorkPlace == MAIN_PK_WORK_PLACE || userWorkPlace == BOSS_PK_WORK_PLACE);
	}

	public boolean isSuperWorkspace() {

		return userWorkPlace == SUPER_WORK_PLACE;
	}

	private long getWorkPlace() {

		Object value = AfinaQuery.INSTANCE.selectValue(SEL_WORK_PLACE, null);

		return !(value instanceof Number) ? -1 :  ((Number)value).longValue();
	}

	@Override
	public List<FieldItem> getFields() {
		
		PacketRowField cursor = getRow();
		
		if(cursor == null) {
			cursor = new PacketRowField();
		}
		
		return cursor.fieldItems();
	}

	@Override
	public void setViewType(int type) {
		selectTypes = SelectTypes.values()[type];
		
		selMyUser = selectTypes.getSelUser();
		
		selStateStart = selectTypes.getSelMinState();
		
		selStateEnd = selectTypes.getSelMaxState();
		
		setMustUpdate();
		getData();
	}

	@Override
	public int getTypeSelect() {

		return selectTypes.ordinal();
	}


	protected String getSelectPacketData() {

		logger.info("isWorkPlaceDopik() =" + isWorkPlaceDopik());

		return isWorkPlaceDopik() ? SEL_PACKET_DOPIK : SEL_PACKET_COND;
	}

	@Override
	protected List<PacketRowField> initData() {
		List<Object[]> datas = AfinaQuery.INSTANCE.select(String.format(getSelectPacketData(), selMyUser),
                new Object[]{selStateStart.ordinal(), selStateEnd.ordinal()});

		List<PacketRowField> data = new ArrayList<>();
		
		for (Object[] row : datas) {
			data.add(PacketRowField.create(row) );
		}
		
		return data;
	}

	@Override
	protected PacketRowField createEmptyRow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PacketRowField cloneRow(PacketRowField row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void insertRow(PacketRowField row) {
		row.setId(AfinaQuery.nextSequence() );

        try {
            AfinaQuery.INSTANCE.execute(INS_PACKET, row.getFields());
        } catch (SessionException ignored) {}
    }
	
	protected Number getNewStateIdPack(TypePacket typePacket) {
		
		Object values = AfinaQuery.INSTANCE.selectValue(SEL_NEW_PACKET, new Object[] { typePacket.getDbValue().longValue() });

        return !(values instanceof Number) ? null : (Number)values;
	}
	
	private PacketRowField getExistsPacketRowField(Number id) {
		List<PacketRowField> data = getData();
		
		if(data != null) {
			for(PacketRowField field : data) {
				if(id.longValue() == field.getId().longValue()) {
					return field;
				}
			}
		}
		
		return null;
	}

	private PacketRowField createNewStateRecord(Number id, TypePacket typePacket) {
		final java.sql.Time now = new java.sql.Time(System.currentTimeMillis());

		final String user = AfinaQuery.getUser();

		Object[] row = new Object[] { id, user, now, "", user, 0, now, null, null,
				typePacket.getDbValue().longValue() };

        return PacketRowField.create(row);
	}

	private PacketRowField creatDraftStateRecord(Number id) {

		Object maxDate = AfinaQuery.INSTANCE.selectValue(SEL_MAX_DATE, null);

		final String user = AfinaQuery.getUser();
		
		Object[] row = new Object[] { id, user, maxDate, "", user, 0, maxDate,
				null, null, TypePacket.REISSUE.getDbValue().longValue() };

		return PacketRowField.create(row);
	}

	private Number getDraftStateIdPack() {

		Object idDrafPacket = AfinaQuery.INSTANCE.selectValue(SEL_DRAFT_PACKET, null);
		
		return !(idDrafPacket instanceof Number) ? null : (Number)idDrafPacket;
	}
	

	/**
	 * @return запись для Черновика-пакета для новых заявлений
	 */
	private PacketRowField createDefaultDraftRecord(String nameRecord) {

		Number existsId = getDraftStateIdPack();

		if (existsId != null) {
			PacketRowField field = getExistsPacketRowField(existsId);
			if (field == null) {
				field = creatDraftStateRecord(existsId);
			}
			return field;
		}

		Object maxDate = AfinaQuery.INSTANCE.selectValue(SEL_MAX_DATE, null);

		final String user = AfinaQuery.getUser();

		Object[] row = new Object[] { null, user, maxDate, nameRecord, user, 0,
                maxDate, null, null, TypePacket.REISSUE.getDbValue().longValue() };

		PacketRowField field = PacketRowField.create(row);

		insertRow(field);

		return field;
	}

	public PacketRowField createDefaultRecord(String nameRecord) {

		if (PACKET_NEW_NOT_SENT.equals(nameRecord)) {
			return createDefaultDraftRecord(nameRecord);
		} else if (BTRT25_PACKET_NAME.equals(nameRecord)) {

			return createDefaultBtrt25Record(nameRecord);
		} else {
			return createDefaultNewRecord(nameRecord);
		}
	}

	private PacketRowField createDefaultBtrt25Record(String nameRecord) {
		Number existsId = getNewStateIdPack(TypePacket.BTRT25);

		if (existsId != null) {
			PacketRowField field = getExistsPacketRowField(existsId);
			if (field == null) {
				field = createNewStateRecord(existsId, TypePacket.BTRT25);
			}
			return field;
		}

		final java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

		final String user = AfinaQuery.getUser();

		Object[] row = new Object[] { null, user, now, nameRecord, user, 0, now, null, null,
				TypePacket.BTRT25.getDbValue().longValue() };

		PacketRowField field = PacketRowField.create(row);

		insertRow(field);

		return field;
	}

	/**
	 * @return запись для нового пакета для отправки
	 */
	private PacketRowField createDefaultNewRecord(String nameRecord) {
		Number existsId = getNewStateIdPack(TypePacket.REISSUE);
		
		if (existsId != null) {
			PacketRowField field = getExistsPacketRowField(existsId);
			if (field == null) {
				field = createNewStateRecord(existsId, TypePacket.REISSUE);
			}
			return field;
		}
		
		final java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
		
		final String user = AfinaQuery.getUser();
		
		Object[] row = new Object[] { null, user, now, nameRecord, user, 0, now, null, null,
				TypePacket.REISSUE.getDbValue().longValue() };
		
		PacketRowField field = PacketRowField.create(row);
		
		insertRow(field);
		
		return field;
	}
	
	public DBStoreApplicationCard getDBStoreApplicationCard() {
		return dbStorePlastic.getApplicationCard();
	}

	public DBStorePacketContent getDBStorePacketContentPacket() {
		return (DBStorePacketContent)dbStorePlastic.getContent();
	}
	
	public String getEmptyDesignCard(List<String> emptyDesignCards) {
		if (emptyDesignCards == null) {
			return "list of emptyDesignCards is null";
		}

		PacketRowField field = getRow();

		if (field == null) {
			return null;
		}

        try {
            List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(Cfg.query().selectEmptyDesign(), new Object[] { field.getId() });

            datas.forEach(f -> emptyDesignCards.add((String) f[0]));
        } catch (SessionException e) {
            return e.getMessage();
        }

		return null;
	}

	public String reIssueCards() {
		
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}
		
		if(field.getState() != 0) {
			return STATE_NONE_NEW;
		}
		
		java.util.Date updated = (java.util.Date) field.getFieldByLabel(
				PacketRowField.FIELD_UPDATED).getVal();

		/*
		if (updated != null && updated.getTime() > 2 * System.currentTimeMillis()) {
			return STATE_NONE_DELETE_DRAFT;
		}*/

        try {
            AfinaQuery.INSTANCE.execute(REISSUE_CARDS, new Object[] {field.getId()});
        } catch (SessionException e) {
            return e.getMessage();
        }

		field.setState(1);
		updateAllData();

		DBStorePacketContent content = getDBStorePacketContentPacket();
		content.refreshData(getData(), StateRefresh.ALL);

		return null;
	}
	
	public String renamePacket(String newName) {
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}
		
		java.util.Date updated = (java.util.Date) field.getFieldByLabel(
				PacketRowField.FIELD_UPDATED).getVal();

		if (updated != null && updated.getTime() > 2 * System.currentTimeMillis()) {
			return STATE_NONE_DELETE_DRAFT;
		}

        try {
            AfinaQuery.INSTANCE.execute(UPD_RENAME_PACKET, new Object[] {newName, field.getId()});
        } catch (SessionException e) {
            return e.getMessage();
        }

        field.setName(newName);

		updateAllData();
		
		return null;
	}
	
	public String outClient() {
		
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}
		
		if(field.getState() != StatePlasticPacket.CARD_HOME_OFFICCES.getDbValue()) {
			return STATE_NONE_GET_HOMES;
		}

        try {
            AfinaQuery.INSTANCE.execute(UPD_OUT_CLIENT_PACKET, new Object[] {field.getId()});
        } catch (SessionException e) {
            return e.getMessage();
        }

		updateAllData();

		DBStorePacketContent content = getDBStorePacketContentPacket();
		content.setCursor(field);

		return null;
	}
	
	public String toGetHomes() {
		
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}
		
		if(field.getState() != StatePlasticPacket.CARD_SENT_OFFICCES.getDbValue()) {
			return STATE_NONE_TO_DOPIKI;
		}

        try {
            AfinaQuery.INSTANCE.execute(UPD_GET_HOMES_PACKET, new Object[] {field.getId()});
        } catch (SessionException e) {
            return e.getMessage();
        }

		updateAllData();

		return null;
	}
	
	public Set<String> getDopikiInPacket() {
		PacketRowField field = getRow();

		if (field == null) {
			return null;
		}

		DBStorePacketContent content = getDBStorePacketContentPacket();

		List<PacketContentRowField> dataContent = content.getData();
		if (dataContent == null || dataContent.size() == 0) {
			return null;
		}

		Set<String> dopiki = new HashSet<>();

		for (PacketContentRowField row : dataContent) {
			String dopik = row.getFieldByLabel(PacketContentRowField.FIELD_OFFICE).getValueField();

			if (dopik == null || "".equals(dopik)) {
				continue;
			}
			dopiki.add(dopik);
		}

		return dopiki;
	}

	public String toDopik(String dopic) {
		PacketRowField field = getRow();

		if (field == null) {
			return PACKET_NOT_SELECTED;
		}

		if (field.getState() !=  StatePlasticPacket.CARD_GO.getDbValue()) {
			return STATE_NONE_GO_HOME;
		}

        try {
            AfinaQuery.INSTANCE.execute(UPD_TO_DOPIK_PACKET, new Object[] { field.getId(),
                            dopic.trim().toUpperCase() });
        } catch (SessionException e) {
            return e.getMessage();
        }

		updateAllData();

		DBStorePacketContent content = getDBStorePacketContentPacket();
		content.setCursor(field);

		return null;
	}

	public String goHomeState() {
		
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}
		
		if(field.getState() != StatePlasticPacket.SMS_RESPONSE_OK_ALL_OIA.getDbValue()) {
			return STATE_NONE_SMS_OIA;
		}

        try {
            AfinaQuery.INSTANCE.execute(UPD_GO_HOME_PACKET, new Object[] {field.getId()});
        } catch (SessionException e) {
            return e.getMessage();
        }

		field.setState(StatePlasticPacket.CARD_GO.getDbValue());

		updateAllData();

		DBStorePacketContent content = getDBStorePacketContentPacket();
		content.setCursor(field);

		return null;
	}

	public String removePacket() {
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}
		
		if(field.getState() != 0) {
			return STATE_NONE_NEW;
		}
		
		java.util.Date updated = (java.util.Date)field.getFieldByLabel(PacketRowField.FIELD_UPDATED).getVal();
		
		if (updated != null && updated.getTime() > 2 * System.currentTimeMillis()) {
			return STATE_NONE_DELETE_DRAFT;
		}

        try {
            AfinaQuery.INSTANCE.execute(UPD_REMOVE_PACKET, new Object[] {field.getId()});
        } catch (SessionException e) {
            return e.getMessage();
        }

		getData().remove(field);
		
		DBStorePacketContent content = getDBStorePacketContentPacket();
		content.refreshData(getData(), StateRefresh.REMOVE_ITEM);

		updateAllData();
		
		return null;
	}

	static public String getAppFile() {
		
		Object value = AfinaQuery.INSTANCE.selectValue(SEL_FILENAME, null);

		if(!(value instanceof String)) return null;

		String fileName = (String) value;
		
		if (AfinaQuery.isTestBaseConnect()) {
			return "null";
		}
		
		return fileName;
	}

	public boolean isTestBaseConnect() {
		return AfinaQuery.isTestBaseConnect();
	}

	public String getChangeFromProductItems(List<String> fromProducts) {
		PacketRowField field = getRow();

		if (field == null) {
			return PACKET_NOT_SELECTED;
		}

		if (field.getState() != 0) {
			return "Для смены продукта пакет может находится только в состоянии 'НОВЫЙ'";
		}

        try {
            List<Object[]> values = AfinaQuery.INSTANCE.selectCursor(SEL_FROM_PRODUCTS_NAME, new Object[] { field.getId() });

            values.forEach(it -> fromProducts.add((String) it[0]));

        } catch (SessionException e) {
            return e.getMessage();
        }

		return null;
	}
	
	public String getChangeToProductItems(String fromProduct, List<String> toProducts) {
		PacketRowField field = getRow();

		if (field == null || fromProduct == null) {
			return PACKET_NOT_SELECTED;
		}

		if (field.getState() != 0) {
			return "Для смены продукта пакет может находится только в состоянии 'НОВЫЙ'";
		}

        try {
            List<Object[]> values = AfinaQuery.INSTANCE.selectCursor(SEL_TO_PRODUCTS_NAME,
                    new Object[] { field.getId(), fromProduct});

            values.forEach(it -> toProducts.add((String) it[0]));

        } catch (SessionException e) {
            return e.getMessage();
        }

		return null;
	}
	
	public String changeProduct(String fromProduct, String toProduct) {
		PacketRowField field = getRow();

		if (field == null) {
			return PACKET_NOT_SELECTED;
		}

		if (field.getState() != 0) {
			return "Для смены продукта пакет может находится только в состоянии 'НОВЫЙ'";
		}

        try {
            AfinaQuery.INSTANCE.execute(UPD_CHANGE_PRODUCT,
                    new Object[] { field.getId(), fromProduct, toProduct });
        } catch (SessionException e) {
			updateAllData();
            return e.getMessage();
        }

		updateAllData();

		return null;
	}
	

	public String toCreateFileState() {
		PacketRowField field = getRow(); 
		
		if(field== null) {
			return PACKET_NOT_SELECTED;
		}

		int stateFrom = (field.getState() == 4) ? 4 : 7;
		
		int stateToOk = (field.getState() == 4) ? 3 : 5;


        try {
            AfinaQuery.INSTANCE.execute(TO_CREATE_FILE_STATE, new Object[] {field.getId(), 1, stateToOk, stateFrom});
        } catch (SessionException e) {
            return e.getMessage();
        }

		field.setState(stateToOk);
		
		DBStorePacketContent content = getDBStorePacketContentPacket();
		content.refreshData(getData(), StateRefresh.ALL);
		
		return null;
	}
	
	public String toSmsState() {
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}

        try {
            AfinaQuery.INSTANCE.execute(TO_SMS_STATE, new Object[] {field.getId(), 9, field.getState()});
        } catch (SessionException e) {
            return e.getMessage();
        }

		field.setState(9);
		
		DBStorePacketContent content = getDBStorePacketContentPacket();
		content.refreshData(getData(), StateRefresh.ALL);
		
		return null;
	}
	
	public String sendBrt25() {
		PacketRowField field = getRow();

		if (field == null) {
			return PACKET_NOT_SELECTED;
		}

		if (field.getState() != 0) {
			return "Для отправки BTRT25 пакет может находится только в состоянии 'НОВЫЙ'";
		}

		String fileName = getAppFile();
		if (fileName == null) {
			return ERROR_CREATE_FILE;
		}

		List<Object> values;

        try {
            values = AfinaQuery.INSTANCE.execute(CREATE_BTRT25, new Object[] { field.getId(), fileName }, new int[] { OracleTypes.CLOB });
        } catch (SessionException e) {
            return e.getMessage();
        }

		if (values == null || values.size() == 0 || values.get(0) == null) {
			return ERROR_CREATE_FILE;
		}

		String data;
		try {
			data = Util.clob2string((java.sql.Clob) values.get(0));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}

		String path = Util.getPathFileOut() + "/" + fileName;

        String error = saveFile(path, data);
		if (error != null) {
			return error;
		}

		field.setState(2);
		field.setFileApp(fileName);

		updateAllData();

		return null;
	}

	public String createFile() {
		
		PacketRowField field = getRow(); 
		
		if(field == null) {
			return PACKET_NOT_SELECTED;
		}
		
		if(field.getState() != 1) {
			return STATE_NONE_ISSUE;
		}
		
		String fileName = getAppFile();
		if (fileName == null) {
			return ERROR_CREATE_FILE;
		}


        List<Object> values;

        try {
            values = AfinaQuery.INSTANCE.execute(CREATE_FILE, new Object[]{field.getId(), fileName}, new int[] { OracleTypes.CLOB });
        } catch (SessionException e) {
            return e.getMessage();
        }

		if(values == null || values.size() == 0 || values.get(0) == null) {
			return ERROR_CREATE_FILE;
		}
		
		String data;
		try {
			data = Util.clob2string(( java.sql.Clob )values.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return e.getMessage();
		}
		
		String path = Util.getPathFileOut() + "/" + fileName;
		if (AfinaQuery.isTestBaseConnect()) {
			path = "c:/" + fileName;
		}

        String error = saveFile(path, data);
		if(error != null) {
			return error;
		}
				
		field.setState(2);
		field.setFileApp(fileName);

		updateAllData();

		return null;
	}
	
	static public String saveFile(String  path, String data) {
        
		logger.info("!!!!!" + data);

        return Util.writeTextFile(path, data, "cp1251");
	}


	@Override
	protected void updateRow(PacketRowField oldData,
			PacketRowField newData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void remove(PacketRowField row) {
		// TODO Auto-generated method stub
		
	}

}