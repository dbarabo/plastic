package ru.barabo.plastic.release.application.data;

import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.ListenerStore;
import ru.barabo.total.db.StateRefresh;
import ru.barabo.total.db.impl.AbstractDBStore;

import java.util.ArrayList;
import java.util.List;

public class DBStoreClientFind extends AbstractDBStore<ClientRowField> implements
		ListenerStore<AppCardRowField> {

	private final static String SEL_CLIENT =
			"{ ? = call od.PTKB_PLASTIC_AUTO.getClientFind( ?, ?, ?, ?, ? ) }";

	//final static transient private Logger logger = Logger.getLogger(DBStoreClientFind.class.getName());

	private DBStorePlastic dbStorePlastic;

	private boolean isPersonActive;

	void setPersonActive(boolean isPersonActive) {
		this.isPersonActive = isPersonActive;
	}

	public DBStoreClientFind(DBStorePlastic dbStorePlastic, DBStore<AppCardRowField> applicationCard) {

		super();

		this.dbStorePlastic = dbStorePlastic;

		applicationCard.addListenerStore(this);
	}

	@Override
	public List<FieldItem> getFields() {
		ClientRowField cursor = getRow();

		if (cursor == null) {
			cursor = new ClientRowField();
		}

		return cursor.fieldItems();
	}

	private Object[] getPersonParams(AppCardRowField field) {

		String personName = field.getPersonName();
		if (personName != null && "".equals(personName.trim())) {
			personName = null;
		}
		if (personName != null) {
			personName = personName.replace(' ', '%').toUpperCase();
		}

		String personAddress = field.getPersonAddress();
		if (personAddress != null && "".equals(personAddress.trim())) {
			personAddress = null;
		}
		if (personAddress != null) {
			personAddress = personAddress.replace(' ', '%').toUpperCase();
		}

		String personInn = field.getPersonInn();
		if (personInn != null && "".equals(personInn.trim())) {
			personInn = null;
		}
		if (personInn != null) {
			personInn = personInn.replace(' ', '%').toUpperCase();
		}

		String personDocSeria = field.getPersonDocSeria();
		if (personDocSeria != null && "".equals(personDocSeria.trim())) {
			personDocSeria = null;
		}
		if (personDocSeria != null) {
			personDocSeria = personDocSeria.replace(' ', '%');
		}

		String personDocNumber = field.getPersonDocNumber();
		if (personDocNumber != null && "".equals(personDocNumber.trim())) {
			personDocNumber = null;
		}
		if (personDocNumber != null) {
			personDocNumber = personDocNumber.replace(' ', '%');
		}

		if (personName == null && personAddress == null && personInn == null
				&& personDocSeria == null && personDocNumber == null) {
			return null;
		}

		return new Object[] { personName == null ? "" : personName,
				personAddress == null ? "" : personAddress,
				personDocNumber == null ? "" : personDocNumber,
				personDocSeria == null ? "" : personDocSeria,
				personInn == null ? "" : personInn
		};
	}

	private Object[] getCustomerParams(AppCardRowField field) {

		String customerName = field.getFieldByLabel(AppCardRowField.CUSTOMER_NAME).getValueField();
		if (customerName != null && "".equals(customerName.trim())) {
			customerName = null;
		}
		if (customerName != null) {
			customerName = customerName.replace(' ', '%').toUpperCase();
		}

		String customerAddress = field.getFieldByLabel(AppCardRowField.CUSTOMER_ADDRESS)
				.getValueField();
		if (customerAddress != null && "".equals(customerAddress.trim())) {
			customerAddress = null;
		}
		if (customerAddress != null) {
			customerAddress = customerAddress.replace(' ', '%').toUpperCase();
		}

		String customerInn = field.getFieldByLabel(AppCardRowField.CUSTOMER_INN).getValueField();
		if (customerInn != null && "".equals(customerInn.trim())) {
			customerInn = null;
		}
		if (customerInn != null) {
			customerInn = customerInn.replace(' ', '%').toUpperCase();
		}

		if (customerName == null && customerAddress == null && customerInn == null) {
			return null;
		}

		return new Object[] { customerName == null ? "" : customerName,
				customerAddress == null ? "" : customerAddress,
				"",
				"",
				customerInn == null ? "" : customerInn
		};
	}

	private Object[] getFindParams() {
		DBStoreApplicationCard appCard = dbStorePlastic.getApplicationCard();

		AppCardRowField field = appCard.getRow();

		if (field == null) {
			return null;
		}

		if (isPersonActive) {
			return getPersonParams(field);
		} else {
			return getCustomerParams(field);
		}

	}

	@Override
	protected List<ClientRowField> initData() {
		List<ClientRowField> result = new ArrayList<>();

		Object[] params = getFindParams();
		if (params == null) {
			return result;
		}

		try {
			List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_CLIENT, params);

			for (Object[] row : datas) {
				result.add(ClientRowField.create(row));
			}

		} catch (SessionException ignored) {}
		return result;
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


	@Override
	protected ClientRowField createEmptyRow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ClientRowField cloneRow(ClientRowField row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void insertRow(ClientRowField row) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateRow(ClientRowField oldData, ClientRowField newData) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void remove(ClientRowField row) {
		// TODO Auto-generated method stub

	}

	private void refreshFromParent() {
		setMustUpdate();
		getData();
	}

	@Override
	public void setCursor(AppCardRowField row) {
		refreshFromParent();
	}

	@Override
	public void refreshData(List<AppCardRowField> allData, StateRefresh stateRefresh) {
		refreshFromParent();

	}

	private boolean isToPerson(String focusedComponentName) {
		return !AppCardRowField.isCustomerFieldName(focusedComponentName);
	}

	/**
	 * Установка клиента в заявление
	 */
	public void selectClient(String focusedComponentName) {
		if (isToPerson(focusedComponentName)) {
			dbStorePlastic.getApplicationCard().setPersonFromClient(getRow());
		} else {
			dbStorePlastic.getApplicationCard().setCustomerFromClient(getRow());
		}
	}

}
