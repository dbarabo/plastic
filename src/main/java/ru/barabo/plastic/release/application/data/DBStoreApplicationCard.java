package ru.barabo.plastic.release.application.data;

import oracle.jdbc.OracleTypes;
import ru.barabo.afina.AfinaQuery;
import ru.barabo.db.SessionException;
import ru.barabo.plastic.gui.PlasticGui;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.packet.data.DBStorePacket;
import ru.barabo.plastic.release.resources.RtfDataLoanPactCredit;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.impl.AbstractDBStore;
import ru.barabo.total.report.rtf.RtfReport;
import ru.barabo.total.utils.Util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBStoreApplicationCard extends AbstractDBStore<AppCardRowField> {

	private final static String SEL_APPLICATION = "{ ? = call od.PTKB_PLASTIC_AUTO.getAppCardItem( ? ) }";

	final static private String CREATE_APPLICATION =
			"{ call od.PTKB_PLASTIC_AUTO.createApplicationCard(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

	final static private String CHANGE_APPLICATION =
			"{ call od.PTKB_PLASTIC_AUTO.changeApplicationCard(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

	final static private String TO_SENT_APPLICATION = "{ call od.PTKB_PLASTIC_AUTO.toSentStateApplication(?) }";

    final static private String SEND_TO_PC_APPLICATION = "{ call od.PTKB_PLASTIC_AUTO.sendToPcApplication(?, ?, ?) }";

	final static private String APPLICATION_IS_EMPTY = "Нечего сохранять в заявлении";

	final static private String PERSON_NOT_SELECTED = "Не выбран держатель";

	final static private String CARD_PRODUCT_NOT_SELECTED = "Не выбран карточный продукт";

	final static private String ERROR_CREATE_APPLICATION = "Ошибка создания заявления";

	final static private String NO_STATE_INFO = "не определено Состояние заявления";

	private DBStorePlastic dbStorePlastic;
	
	private Number applicationId;
	
	
	public DBStoreApplicationCard(DBStorePlastic dbStorePlastic) {
		
		super();
		
		this.dbStorePlastic = dbStorePlastic;
		
		applicationId = null;

		StaticData.getInstance().init(dbStorePlastic);
	}

	@Override
	public List<FieldItem> getFields() {
		AppCardRowField cursor = getRow();
		
		if(cursor == null) {
			cursor = new AppCardRowField();
		}

		return cursor.fieldItems();
	}

	private String checkNewApplicationData(AppCardRowField field) {
		if (field == null) {
			return APPLICATION_IS_EMPTY;
		}

		if (field.getFieldByLabel(AppCardRowField.PERSON_ID).getVal() == null) {
			return PERSON_NOT_SELECTED;
		}

		if (field.getFieldByLabel(AppCardRowField.CARD_PRODUCT_FIELD).getVal() == null) {
			return CARD_PRODUCT_NOT_SELECTED;
		}

		return null;
	}

	public String printApplication() {

		AppCardRowField field = getRow();
		if (field == null || field.fieldItems().get(0).getVal() == null) {
			return APPLICATION_IS_EMPTY;
		}
		
		return RtfReport.build(new RtfDataLoanPactCredit((Number) field.fieldItems().get(0)
				.getVal()));
	}

    public String sendToPcApplication() {
        AppCardRowField field = getRow();

        if (field == null ||
                field.getFieldByLabel(AppCardRowField.FIELD_STATE_NAME).getVal() == null) {
            return NO_STATE_INFO;
        }

        String error = sendToPcApplication(field);

        if (error != null) {
            return error;
        }

        setViewType(((Number) field.fieldItems().get(0).getVal()).intValue());

        DBStorePacket dbPacket = (DBStorePacket)dbStorePlastic.getPacket();
        dbPacket.updateAllData();

        return null;
    }

    private String sendToPcApplication(AppCardRowField field) {
        try {
			String fileName = DBStorePacket.getAppFile();

			List<Object> values = AfinaQuery.INSTANCE.execute(SEND_TO_PC_APPLICATION, new Object[]{fileName, field.getId()}, new int[] { OracleTypes.CLOB });

			if(values == null || values.size() == 0 || values.get(0) == null) {
				throw new Exception(DBStorePacket.ERROR_CREATE_FILE);
			}

			String data = Util.clob2string(( java.sql.Clob )values.get(0));

			String path = AfinaQuery.isTestBaseConnect() ? "c:"  : Util.getPathFileOut();
			path = path + "/" + fileName;

			return DBStorePacket.saveFile(path, data);

        } catch (Exception e) {
            return e.getMessage();
        }
    }

	public String toSentApplication() {
		AppCardRowField field = getRow();

		if (field == null ||
				field.getFieldByLabel(AppCardRowField.FIELD_STATE_NAME).getVal() == null) {
			return NO_STATE_INFO;
		}

		String error = toSentApplication(field);

		if (error != null) {
			return error;
		}

		setViewType(((Number) field.fieldItems().get(0).getVal()).intValue());

		DBStorePacket dbPacket = (DBStorePacket)dbStorePlastic.getPacket();
		dbPacket.updateAllData();

		return null;
	}

	private String toSentApplication(AppCardRowField field) {

        try {
            AfinaQuery.INSTANCE.execute(TO_SENT_APPLICATION, new Object[] { field.fieldItems().get(0).getVal() });
        } catch (SessionException e) {
            return e.getMessage();
        }
        return null;
    }

	public String saveApplication() {
		AppCardRowField field = getRow();

		if (field.fieldItems().get(0).getVal() == null) {
			return createApplication(field);
		} else {
			return changeApplication(field);
		}
	}

	/**
	 * сохранение существующего заявления
	 */
	private String changeApplication(AppCardRowField field) {
		String error = checkNewApplicationData(field);
		if (error != null) {
			return error;
		}

		checkIvrOff(field);
		Object[] params = getParamCreateApplication(field);

		params = Arrays.copyOf(params, params.length + 1);

		params[params.length - 1] = field.fieldItems().get(0).getVal();

        try {
            AfinaQuery.INSTANCE.execute(CHANGE_APPLICATION, params);
        } catch (SessionException e) {
            return e.getMessage();
        }

		setViewType(((Number) field.fieldItems().get(0).getVal()).intValue());

		DBStorePacket dbPacket = (DBStorePacket) dbStorePlastic.getPacket();
		dbPacket.updateAllData();

		return null;
	}

	private Object[] getParamCreateApplication(AppCardRowField field) {

		final String embossName = field.getFieldByLabel(AppCardRowField.EMBOSS_NAME_FIELD)
				.getValueField();

		final String phone = field.getFieldByLabel(AppCardRowField.CARD_PHONE).getValueField();

		final String codeWord = field.getFieldByLabel(AppCardRowField.CODEWORD_FIELD)
				.getValueField();

		return new Object[] {
				field.getFieldByLabel(AppCardRowField.PERSON_ID).getVal(),
				field.getFieldByLabel(AppCardRowField.CUSTOMER_ID).getVal(),
				field.getFieldByLabel(AppCardRowField.CARD_PRODUCT_FIELD).getVal(),
				embossName == null ? "" : embossName,
				phone == null ? "" : phone,
				field.getFieldByLabel(AppCardRowField.ISSALARY_FIELD).getVal(),
				field.getFieldByLabel(AppCardRowField.DEPARTMENT_FIELD).getVal(),
				field.getFieldByLabel(AppCardRowField.ISSMS_FIELD).getVal(),
				codeWord == null ? "" : codeWord,
				field.getFieldByLabel(AppCardRowField.DESIGNCARD_FIELD).getVal(),
				field.getFieldByLabel(AppCardRowField.FIELD_NO_PIN_CONVERT).getVal(),
				field.getFieldByLabel(AppCardRowField.SALARY_PROJECT_JURIC).getVal(),
				field.getFieldByLabel(AppCardRowField.ACCOUNT_LABEL).getVal()
		};
	}

	/**
	 * продолжить если нет IVR
	 * 
	 */
	private void checkIvrOff(AppCardRowField field) {
		Number noPin = (Number) field.getFieldByLabel(AppCardRowField.FIELD_NO_PIN_CONVERT)
				.getVal();

		if (noPin != null && noPin.intValue() != 0) {
			return;
		}

		UIManager.put("OptionPane.yesButtonText", "Да, проставить");
		UIManager.put("OptionPane.noButtonText", "Нет, не проставлять");
		int reply = JOptionPane
				.showConfirmDialog(
						null,
						"Согласно приказу №322 все заявления должны быть без ПИН-конверта\nПроставить признак <Без ПИН-конверта>?",
						"Заявление с ПИН-конвертом", JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION) {
			field.getFieldByLabel(AppCardRowField.FIELD_NO_PIN_CONVERT).setValueFieldObject(1);
		}
	}

	private String createApplication(AppCardRowField field) {

		String error = checkNewApplicationData(field);
		if (error != null) {
			return error;
		}

		checkIvrOff(field);
		Object[] params = getParamCreateApplication(field);

        List<Object> values;
        try {
            values = AfinaQuery.INSTANCE.execute(CREATE_APPLICATION, params, new int[] { OracleTypes.NUMBER });
        } catch (SessionException e) {
            return e.getMessage();
        }

		if (values == null || values.size() == 0 || values.get(0) == null) {
			return ERROR_CREATE_APPLICATION;
		}

		final int val = ((Number) values.get(0)).intValue();
		setViewType(val);

		DBStorePacket dbPacket = (DBStorePacket)dbStorePlastic.getPacket();
		dbPacket.updateAllData();

		return null;
	}

	/**
	 * установка данных для персона из клиентов
	 */
    void setPersonFromClient(ClientRowField clientField) {
		AppCardRowField cursor = getRow();
		
		
		cursor.getFieldByLabel(AppCardRowField.PERSON_ID).setValueField(
				clientField.fieldItems().get(ClientRowField.ID_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.PERSON_NAME).setValueField(
				clientField.fieldItems().get(ClientRowField.LABEL_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.PERSON_ADDRESS).setValueField(
				clientField.fieldItems().get(ClientRowField.ADDRESS_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.PERSON_INN).setValueField(
				clientField.fieldItems().get(ClientRowField.INN_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.PERSON_SERIA).setValueField(
				clientField.fieldItems().get(ClientRowField.SERIA_INDEX).getValueField());
		
		cursor.getFieldByLabel(AppCardRowField.PERSON_NUMBER).setValueField(
				clientField.fieldItems().get(ClientRowField.NUMBER_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.PERSON_KOGDA).setValueField(
				clientField.fieldItems().get(ClientRowField.KOGDA_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.PERSON_KEM).setValueField(
				clientField.fieldItems().get(ClientRowField.KEM_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.PERSON_BIRTHDAY).setValueField(
				clientField.fieldItems().get(ClientRowField.BIRTHDAY_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.CARD_PHONE).setValueField(
				clientField.fieldItems().get(ClientRowField.PHONE_INDEX).getValueField());

		if (cursor.getFieldByLabel(AppCardRowField.CUSTOMER_ID).getVal() == null) {
			setCustomerFromClient(clientField);
		}

		sendListenersCursor(cursor);
	}

	/**
	 * установка данных для кастомера из клиентов
	 * 
	 */
    void setCustomerFromClient(ClientRowField clientField) {
		AppCardRowField cursor = getRow();

		Number oldCustomer = (Number)cursor.getFieldByLabel(AppCardRowField.CUSTOMER_ID).getVal();

		cursor.getFieldByLabel(AppCardRowField.CUSTOMER_ID).setValueField(
				clientField.fieldItems().get(ClientRowField.ID_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.CUSTOMER_NAME).setValueField(
				clientField.fieldItems().get(ClientRowField.LABEL_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.CUSTOMER_ADDRESS).setValueField(
				clientField.fieldItems().get(ClientRowField.ADDRESS_INDEX).getValueField());

		cursor.getFieldByLabel(AppCardRowField.CUSTOMER_INN).setValueField(
				clientField.fieldItems().get(ClientRowField.INN_INDEX).getValueField());


		Number newCustomer = (Number)cursor.getFieldByLabel(AppCardRowField.CUSTOMER_ID).getVal();

		if(newCustomer != null && (oldCustomer == null || newCustomer.intValue() != oldCustomer.intValue())) {
			PlasticGui.updateAccountByField(cursor);
		}
		sendListenersCursor(cursor);
	}

	/* (non-Javadoc)
	 * @see main.total.db.DBStore#setViewType(int)
	 *  в данном случае type = cardapplication.doc (размера хватит на долгие года)
	 */
	@Override
	public void setViewType(int type) {
		applicationId = (type == -1) ? null : type;
		
		setMustUpdate();
		getData();
	}

	@Override
	public int getTypeSelect() {
		
		return (applicationId == null) ? -1 : applicationId.intValue();
	}


	@Override
	protected List<AppCardRowField> initData() {
		
		List<AppCardRowField> result = new ArrayList<>();
		
		if(applicationId == null) {
			result.add(new AppCardRowField());
			return result;
		}

		try {
			List<Object[]>  datas = AfinaQuery.INSTANCE.selectCursor(SEL_APPLICATION, new Object[]{applicationId});

			if(datas.size() > 0) {
                result.add(AppCardRowField.create(datas.get(0)) );
            }
		} catch (SessionException e) {
			return null;
		}
		return result;
	}

	@Override
	protected AppCardRowField createEmptyRow() {
		// TODO ???
		
		return new AppCardRowField();
	}

	@Override
	protected AppCardRowField cloneRow(AppCardRowField row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void insertRow(AppCardRowField row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateRow(AppCardRowField oldData, AppCardRowField newData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void remove(AppCardRowField row) {
		// TODO Auto-generated method stub
		
	}

}
