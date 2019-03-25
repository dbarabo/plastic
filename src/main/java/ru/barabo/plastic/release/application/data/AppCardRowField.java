package ru.barabo.plastic.release.application.data;

import org.apache.log4j.Logger;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.Type;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.db.impl.DetailField;
import ru.barabo.total.gui.detail.FactoryComponent;

import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppCardRowField extends AbstractRowFields {
	
	final static transient private Logger logger = Logger.getLogger(AppCardRowField.class.getName());

	final static transient String CARD_PRODUCT_FIELD = "Продукт карты";

	final static transient String PERSON_ID = "#person";

	final static transient String PERSON_NAME = "Держатель";

	final static transient String PERSON_ADDRESS = "Адрес держателя";

	final static transient String PERSON_SERIA = "Серия";

	final static transient String PERSON_NUMBER = "Номер";

	final static transient String PERSON_INN = "Инн держателя";

	final static transient String PERSON_BIRTHDAY = "Дата рождения";

	final static transient String PERSON_KOGDA = "Когда выдан";

	final static transient String PERSON_KEM = "Кем выдан";

	final static transient String CARD_PHONE = "Телефон";

	final static transient String CUSTOMER_ID = "#customer";

	final static transient String CUSTOMER_NAME = "Владелец";

	final static transient String CUSTOMER_ADDRESS = "Адрес владельца";

	final static transient String CUSTOMER_INN = "Инн владельца";

	final static transient String EMBOSS_NAME_FIELD = "Имя на карте";

	final static transient String ISSALARY_FIELD = "Зарплатная";

	final static transient String DEPARTMENT_FIELD = "Подразделение";

	final static transient String ISSMS_FIELD = "SMS-сервис";

	final static transient String CODEWORD_FIELD = "Кодовое слово";

	final static transient String DESIGNCARD_FIELD = "Дизайн карты";

	private final static transient String PRODUCTCARD_FIELD = "Продукт карты";

	final static transient public String FIELD_STATE_NAME = "Состояние";

	final static transient String FIELD_NO_PIN_CONVERT = "БЕЗ ПИН-конверта";

	final static transient private String[] CUSTOMERS = new String[] { CUSTOMER_NAME,
			CUSTOMER_ADDRESS, CUSTOMER_INN };

	@Override
	protected List<FieldItem> createFields() {
		List<FieldItem> fields = new ArrayList<>();

		fields.add(new DetailField("#id", true, Type.LONG, null,
				"DOC", 1, fields.size(), true, null, null, "Общая информация", 0, 1, 1));

		fields.add(new DetailField("#cardtype", false, Type.LONG,
				null, "cardtype", 0, fields.size(), true, null, null, null, 0,
				0, 0));
		
		fields.add(new DetailField(PERSON_ID, false, Type.LONG, null,
				"person", 0, fields.size(), true, null, null, null, 0, 0, 0));
		
		fields.add(new DetailField("#customer", false, Type.LONG,
				null, "customer", 0, fields.size(), true, null, null, null, 0,
				0, 0));
		
		fields.add(new DetailField("#doctype", false, Type.LONG,
				null, "doctype", 0, fields.size(), true, null, null, null, 0,
				0, 0));
		
		fields.add(new DetailField("#Состояние", false, Type.LONG,
				null, "entitystate", 1, fields.size(), true, null, null, null,
				0, 1, 0));
		
		fields.add(new DetailField("Создан", true, Type.DATE, null,
				"initdate", 1, fields.size(), true, null,
				new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"),
				"Общая информация", 0, 2, 1));
		
		fields.add(new DetailField("Автор", true, Type.STRING, null,
				"authorid", 1, fields.size(), true, null, null, "Общая информация", 1, 2, 1));
		
		fields.add(new DetailField(PRODUCTCARD_FIELD, true, Type.LONG,

				StaticData.getInstance().getCardProductLabel(),
				null, 2, fields.size(), true,
				StaticData.getInstance().getCardProductId(),
				null, "Информация о карте", 2, 1, 1,
				(ActionListener) this::selectDesignProduct));
		
		fields.add(new DetailField(EMBOSS_NAME_FIELD, true, Type.STRING,
				null, "embosname", 1, fields.size(), false, null, null, "Информация о карте", 0,
				4, 1));
		
		fields.add(new DetailField("#account", false, Type.LONG,
				null, "account", 1, fields.size(), true, null, null, null, 0,
				0, 0));
		
		fields.add(new DetailField(ISSALARY_FIELD, true, Type.LONG,
				new String[] { "0", "1" }, null, 1, fields.size(), true, null, null,
				"Информация о карте", 0, 0, 1,
				(ActionListener) this::selectTypeProduct));
		
		fields.add(new DetailField(CARD_PHONE, true, Type.STRING,
				null, null, 2, fields.size(), false, null, null, "Информация о карте", 1, 2, 1));
		
		fields.add(new DetailField(DEPARTMENT_FIELD, true, Type.LONG,
				StaticData.getInstance().getDepartmentLabel(),
				null, 2, fields.size(), true,
				StaticData.getInstance().getDepartmentId(),
				null, "Информация о карте", 2, 4, 1));
		
		fields.add(new DetailField(ISSMS_FIELD, true, Type.LONG,
				new String[] { "0", "1" }, null, 1, fields.size(), true, null, null,
				"Информация о карте", 1, 0, 1));
		
		fields.get(fields.size() - 1).setValueFieldObject(1);

		fields.add(new DetailField(CODEWORD_FIELD, true,
				Type.STRING, null, null, 2, fields.size(), false,
				null, null, "Информация о карте", 1, 3, 1));
		
		fields.add(new DetailField(DESIGNCARD_FIELD, true, Type.LONG,
				StaticData.getInstance().getDesignLabel(),
				null, 1, fields.size(), true,
				StaticData.getInstance().getDesignId(),
				null, "Информация о карте", 0, 2, 1));
		
		fields.add(new DetailField(PERSON_NAME, true, Type.STRING,
				null, null, 4, fields.size(), false, null, null,
				"Информация о Держателе", 0, 0, 1,
				getKeyListenerFindClientByPerson()));
		
		fields.add(new DetailField(PERSON_ADDRESS, true, Type.STRING,
				null, null, 4, fields.size(), false, null, null,
				"Информация о Держателе", 0, 1, 1,
				getKeyListenerFindClientByPerson()));
		
		fields.add(new DetailField(PERSON_BIRTHDAY, true, Type.DATE, null, null, 1,
				fields.size(), true, null, new SimpleDateFormat("dd.MM.yyyy"),
				"Информация о Держателе", 0, 2, 1));
		
		fields.add(new DetailField("Тип документа", true, Type.STRING,
				null, null, 1, fields.size(), true, null, null,
				"Документ Держателя", 0, 0, 1));
		
		fields.add(new DetailField(PERSON_SERIA, true, Type.STRING, null,
				null, 1, fields.size(), false, null, null, "Документ Держателя",
				0, 1, 1,
				getKeyListenerFindClientByPerson()));
		
		fields.add(new DetailField(PERSON_NUMBER, true, Type.STRING, null,
				null, 1, fields.size(), false, null, null, "Документ Держателя",
				1, 1, 1,
				getKeyListenerFindClientByPerson()));
		
		fields.add(new DetailField(PERSON_KOGDA, true, Type.DATE,
				null, null, 1, fields.size(), true, null, new SimpleDateFormat("dd.MM.yyyy"),
				"Документ Держателя", 1, 0, 1));
		
		fields.add(new DetailField(PERSON_KEM, true, Type.STRING,
				null, null, 2, fields.size(), true, null, null,
				"Документ Держателя", 0, 2, 1));
		
		fields.add(new DetailField(PERSON_INN, true,
				Type.STRING, null, null, 3, fields.size(), false,
				null, null,
				"Информация о Держателе", 1, 2, 1,
				getKeyListenerFindClientByPerson()));
		
		fields.add(new DetailField(CUSTOMER_NAME, true, Type.STRING,
				null, null, 1, fields.size(), false, null, null,
				"Информация о Владельце",
				0, 19, 1,
				getKeyListenerFindClientByCustomer()));
		
		fields.add(new DetailField(CUSTOMER_ADDRESS, true, Type.STRING, null, null, 1, fields
				.size(), false, null, null,
				"Информация о Владельце", 0, 20, 1,
				getKeyListenerFindClientByCustomer()));
		
		fields.add(new DetailField(CUSTOMER_INN, true,
				Type.STRING, null, null, 1, fields.size(), false,
				null, null,
				"Информация о Владельце", 0, 21, 1,
				getKeyListenerFindClientByCustomer()));
		
		fields.add(new DetailField("Счет", true, Type.STRING, null,
				null, 1, fields.size(), true, null, null, "Информация о карте", 0, 3, 1));
		
		fields.add(new DetailField(FIELD_STATE_NAME, true, Type.STRING, null,
				null, 1, fields.size(), true, null, null, "Общая информация", 1, 0, 1));
		
		fields.add(new DetailField("Тип заявления", true, Type.STRING, null,
				null, 2, fields.size(), true, null, null, "Общая информация", 0, 0, 1));
		
		fields.add(new DetailField("Тип карты", true, Type.LONG,
				StaticData.getInstance().getTypeLabelCards(),
				null, 1, fields.size(), true,
				StaticData.getInstance().getTypeIdCards(),
				null, "Информация о карте", 0, 1, 1,
				(ActionListener) this::selectTypeProduct));
		
		fields.add(new DetailField("Валюта карты", true, Type.LONG,
				StaticData.getInstance().getCurrencyLabel(),
				null, 1, fields.size(), true,
				StaticData.getInstance().getCurrencyId(),
				null, "Информация о карте", 1, 1, 1,
				(ActionListener) this::selectTypeProduct));

		fields.add(new DetailField(FIELD_NO_PIN_CONVERT, true, Type.LONG,
				new String[] { "0", "1" }, null, 1, fields.size(), true, null, null,
				"Информация о карте", 2, 0, 1));

		fields.add(new DetailField("Статус в ПЦ", true, Type.STRING, null,
				null, 1, fields.size(), true, null, null, "Информация о карте", 1, 3, 1));
/*
		fields.add(new DetailField("Действует с", true, Type.DATE, null,
				null, 1, fields.size(), true, null, new SimpleDateFormat("dd.MM.yyyy"),
				"Информация о карте", 1, 4, 1));
*/
		fields.get(fields.size() - 1).setValueFieldObject(1);

		return fields;
	}
	
	/**
	 * принадлежит ли к полям кастомера
	 */
	static boolean isCustomerFieldName(String fieldLabel) {

		return Arrays.asList(CUSTOMERS).contains(fieldLabel);
	}

	private KeyListener getKeyListenerFindClientByPerson() {
		return new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				
				logger.info("e.getKeyChar()=" + e.getKeyChar());

				if (e.getKeyChar() != ' ' && e.getKeyChar() != '\b') {
					return;
				}
				
				DetailField fieldPerson =  (DetailField)getFieldByLabel(PERSON_NAME);
				setFieldValueFromComponent(fieldPerson);

				logger.info("fieldPerson=" + fieldPerson);
				logger.info("fieldPerson value=" + fieldPerson.getValueField());

				DetailField fieldPersonAddress = (DetailField) getFieldByLabel(PERSON_ADDRESS);
				setFieldValueFromComponent(fieldPersonAddress);

				DetailField fieldPersonInn = (DetailField) getFieldByLabel(PERSON_INN);
				setFieldValueFromComponent(fieldPersonInn);

				DetailField fieldPersonSeria = (DetailField) getFieldByLabel(PERSON_SERIA);
				setFieldValueFromComponent(fieldPersonSeria);

				DetailField fieldPersonNumber = (DetailField) getFieldByLabel(PERSON_NUMBER);
				setFieldValueFromComponent(fieldPersonNumber);

				DBStorePlastic dbStorePlastic = StaticData.getInstance().getDbStorePlastic();

				if (dbStorePlastic == null) {
					return;
				}

				DBStoreClientFind clientFind = dbStorePlastic.getClientFind();

				logger.info("clientFind setMustUpdate=" + getComponentValueField(fieldPerson));

				clientFind.setPersonActive(true);

				clientFind.setMustUpdate(true);
				clientFind.getData();
			}
		};
	}

	private KeyListener getKeyListenerFindClientByCustomer() {
		return new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				logger.info("e.getKeyChar()=" + e.getKeyChar());

				if (e.getKeyChar() != ' ') {
					return;
				}

				DetailField fieldCustomer = (DetailField) getFieldByLabel(CUSTOMER_NAME);
				final String customerName = getComponentValueField(fieldCustomer);

				DetailField fieldCustomerAddress = (DetailField) getFieldByLabel(CUSTOMER_ADDRESS);
				final String customerAddress = getComponentValueField(fieldCustomerAddress);

				DetailField fieldCustomerInn = (DetailField) getFieldByLabel(CUSTOMER_INN);
				final String customerInn = getComponentValueField(fieldCustomerAddress);


				if ((customerName == null || customerName.length() < 5) &&
						(customerAddress == null || customerAddress.length() < 10) &&
						(customerInn == null || customerInn.length() < 5)) {

					return;
				}

				logger.info("customerName=" + customerName);
				logger.info("customerAddress=" + customerAddress);
				logger.info("customerInn=" + customerInn);

				setFieldValueFromComponent(fieldCustomer);
				setFieldValueFromComponent(fieldCustomerAddress);
				setFieldValueFromComponent(fieldCustomerInn);

				DBStorePlastic dbStorePlastic = StaticData.getInstance().getDbStorePlastic();

				if (dbStorePlastic == null) {
					return;
				}

				DBStoreClientFind clientFind = dbStorePlastic.getClientFind();

				clientFind.setPersonActive(false);
				clientFind.setMustUpdate(true);
				clientFind.getData();
			}
		};
	}

	private void selectDesignProduct(ActionEvent e) {

		setSelectComboBoxValueToField(e);

		String productLabel = (String) getProductCardComponent().getSelectedItem();

		int indexProduct = StaticData.getInstance().getIndexByProductLabel(productLabel);

		if (indexProduct == -1) {
			return;
		}

		DBStorePlastic dbStorePlastic = StaticData.getInstance().getDbStorePlastic();

		AppCardRowField fieldRow = dbStorePlastic.getApplicationCard().getRow();
		String priorValue = fieldRow.getFieldByLabel(DESIGNCARD_FIELD).getValueField();

		String designCode = StaticData.getInstance().getCardProductDesign()[indexProduct];

		String[] designs = StaticData.getInstance().getDesignLabel();

		List<String> items = new ArrayList<>();

        for (String design : designs) {
            if (designCode.equals(design.substring(0, designCode.length()))) {

                items.add(design);
            }
        }

		JComboBox comboDesign = getDesignComponent();
		FactoryComponent.setListItems(comboDesign, items);

		if (priorValue != null &&
                items.stream().anyMatch(f -> f.equals(priorValue))) {
			comboDesign.setSelectedItem(priorValue);
		} else {
			comboDesign.setSelectedItem(comboDesign.getSelectedItem());

			fieldRow.getFieldByLabel(DESIGNCARD_FIELD).setValueField(
					(String) comboDesign.getSelectedItem());
		}
	}

	private void setSelectComboBoxValueToField(ActionEvent e) {

		final JComboBox comp = (JComboBox) e.getSource();

		DBStorePlastic dbStorePlastic = StaticData.getInstance().getDbStorePlastic();
		AppCardRowField fieldRow = dbStorePlastic.getApplicationCard().getRow();

		FieldItem selectField = fieldRow.getFieldByLabel(comp.getName());
		if (selectField != null) {
			logger.info("COMBO_NAME=" + comp.getName() + " VALUE=" + comp.getSelectedItem());
			selectField.setValueField((String) comp.getSelectedItem());
		}
	}

	private void setSelectComponentValueToField(ActionEvent e) {

		if (e.getSource() instanceof JComboBox) {
			setSelectComboBoxValueToField(e);
		} else {
			setSelectCheckBoxValueToField(e);
		}
	}

	private void setSelectCheckBoxValueToField(ActionEvent e) {

		final JCheckBox comp = (JCheckBox) e.getSource();

		DBStorePlastic dbStorePlastic = StaticData.getInstance().getDbStorePlastic();
		AppCardRowField fieldRow = dbStorePlastic.getApplicationCard().getRow();

		FieldItem selectField = fieldRow.getFieldByLabel(comp.getName());
		if (selectField != null) {
			selectField.setValueField(comp.isSelected() ? "1" : "0");
		}
	}

	private void selectTypeProduct(ActionEvent e) {

		setSelectComponentValueToField(e);

		logger.info(e.getSource());
		
		String typeCardValue = (String) getTypeCardComponent().getSelectedItem();

		String currencyCardValue = (String) getCurrencyCardComponent().getSelectedItem();

		int isSalary = getSalaryCardComponent().isSelected() ? 1 : 0;

		String[] types = StaticData.getInstance().getCardProductTypeLabel();

		String[] currencies = StaticData.getInstance().getCardProductCurrencyLabel();

		int[] isSalaries = StaticData.getInstance().getIsSalaryCard();

		DBStorePlastic dbStorePlastic = StaticData.getInstance().getDbStorePlastic();
		AppCardRowField fieldRow = dbStorePlastic.getApplicationCard().getRow();
		final String priorValue = fieldRow.getFieldByLabel(PRODUCTCARD_FIELD).getValueField();

		logger.info("PRODUCTCARD_FIELD priorValue=" + priorValue);

		List<String> items = new ArrayList<>();

		for (int index = 0; index < types.length; index++) {

			if (typeCardValue == null || typeCardValue.equals(types[index])) {

				if (currencyCardValue == null || currencyCardValue.equals(currencies[index])) {

					if (isSalary == isSalaries[index]) {
						items.add(StaticData.getInstance().getCardProductLabel()[index]);
					}
				}
			}
		}

		JComboBox comboCardProduct = getProductCardComponent();

		FactoryComponent.setListItems(comboCardProduct, items);
		logger.info("priorValue=" + priorValue);

		if (priorValue != null &&
                items.stream().anyMatch(f -> f.equals(priorValue))) {
			comboCardProduct.setSelectedItem(priorValue);
		} else {
			comboCardProduct.setSelectedItem(comboCardProduct.getSelectedItem());

			fieldRow.getFieldByLabel(PRODUCTCARD_FIELD).setValueField(
					(String) comboCardProduct.getSelectedItem());
		}

		logger.info("comboCardProduct.getSelectedItem()=" + comboCardProduct.getSelectedItem());

		selectDesignProduct(new ActionEvent(getDesignComponent(), e.getID(), e.getActionCommand()));
	}

	static public AppCardRowField create(Object[] row) {
		AppCardRowField field = new AppCardRowField();
		
		for(int index = 0; index < field.fieldItems().size(); index++) {
			try {
				field.fieldItems().get(index).setValueFieldObject(row[index]);
			} catch (java.lang.ClassCastException e) {
				logger.error("index = " + index);

				throw (e);
			}
		}

		return field;
	}
	
	private JComboBox getProductCardComponent() {
		DetailField field = (DetailField) fieldItems().get(8);

		return (JComboBox) field.getComponent();
	}

	private JComboBox getTypeCardComponent() {
		DetailField field = (DetailField) fieldItems().get(32);

		return (JComboBox) field.getComponent();
	}

	private JComboBox getDesignComponent() {
		DetailField field = (DetailField) fieldItems().get(16);

		return (JComboBox) field.getComponent();
	}

	private JComboBox getCurrencyCardComponent() {
		DetailField field = (DetailField) fieldItems().get(33);

		return (JComboBox) field.getComponent();
	}

	private JCheckBox getSalaryCardComponent() {
		DetailField field = (DetailField) fieldItems().get(11);

		return (JCheckBox) field.getComponent();
	}

	private String getComponentValueField(DetailField field) {
		JComponent comp = field.getComponent();

		if (comp == null) {
			return null;
		}

		if (comp instanceof JComboBox) {
			return (String) ((JComboBox) comp).getSelectedItem();
		} else if (comp instanceof JTextField) {
			return ((JTextField) comp).getText();
		} else if (comp instanceof JCheckBox) {
			return ((JCheckBox) comp).isSelected() ? "1" : "0";
		} else {
			return null;
		}
	}

	private void setFieldValueFromComponent(DetailField field) {

		String value = getComponentValueField(field);

		logger.info("setFieldValueFromComponent=" + value);

		DBStorePlastic dbStorePlastic = StaticData.getInstance().getDbStorePlastic();

		AppCardRowField fieldRow = dbStorePlastic.getApplicationCard().getRow();

		if (value != null) {
			fieldRow.fieldItems().get(field.getIndex()).setValueField(value);
		}
	}

	String getPersonName() {
		return (String) fieldItems().get(17).getVal();
	}

	String getPersonAddress() {
		return (String) fieldItems().get(18).getVal();
	}

	String getPersonInn() {
		return (String) fieldItems().get(25).getVal();
	}

	String getPersonDocSeria() {
		return (String) fieldItems().get(21).getVal();
	}

	String getPersonDocNumber() {
		return (String) fieldItems().get(22).getVal();
	}
}
