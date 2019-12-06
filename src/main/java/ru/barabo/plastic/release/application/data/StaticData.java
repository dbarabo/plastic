package ru.barabo.plastic.release.application.data;

import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.release.main.data.DBStorePlastic;

import java.util.List;

public class StaticData {

	private final static String SEL_TYPE_CARDS = "{ ? = call od.PTKB_PLASTIC_AUTO.getAllTypeCards }";

	private final static String SEL_CURRENCY = "{ ? = call od.PTKB_PLASTIC_AUTO.getAllCurrency }";

	private final static String SEL_CARD_PRODUCT = "{ ? = call od.PTKB_PLASTIC_AUTO.getAllCardProduct }";

	private final static String SEL_DEPARTMENT = "{ ? = call od.PTKB_PLASTIC_AUTO.getAllDepartment }";

	private final static String SEL_DESIGN = "{ ? = call od.PTKB_PLASTIC_AUTO.getAllDesignPlastic }";

	private final static String SEL_SALARY_CLIENT_JUR = "{ ? = call od.PTKB_PLASTIC_AUTO.getSalaryClientList }";

	public static class SingletonHolder {
		static final StaticData HOLDER_INSTANCE = new StaticData();
	}

	public static StaticData getInstance() {
		return SingletonHolder.HOLDER_INSTANCE;
	}

	private String[] typeLabelCards;
	private Integer[] typeIdCards;

	private String[] currencyLabel;
	private Integer[] currencyId;

	private String[] cardProductLabel;
	private Integer[] cardProductId;
	private String[] cardProductTypeLabel;
	private String[] cardProductCurrencyLabel;
	private Integer[] isSalaryCard;
	private String[] cardProductDesign;


	private String[] departmentLabel;
	private Integer[] departmentId;

	private String[] designLabel;
	private Integer[] designId;

	private String[] salaryClientLabel;
	private Integer[] salaryClientId;

	int getIndexByProductLabel(String productLabel) {
		for (int index = 0; index < cardProductLabel.length; index++) {
			if (cardProductLabel[index].equals(productLabel)) {
				return index;
			}
		}

		return -1;
	}

	String[] getSalaryClientLabel() { return salaryClientLabel; }

	Integer[] getSalaryClientId() { return salaryClientId; }

	String[] getCardProductDesign() {
		return cardProductDesign;
	}

	Integer[] getIsSalaryCard() {
		return isSalaryCard;
	}

	String[] getCardProductCurrencyLabel() {
		return cardProductCurrencyLabel;
	}

	String[] getCardProductTypeLabel() {
		return cardProductTypeLabel;
	}

	String[] getDesignLabel() {
		return designLabel;
	}

	Integer[] getDesignId() {
		return designId;
	}

	String[] getDepartmentLabel() {
		return departmentLabel;
	}

	Integer[] getDepartmentId() {
		return departmentId;
	}

	public String[] getCardProductLabel() {
		return cardProductLabel;
	}

	Integer[] getCardProductId() {
		return cardProductId;
	}

	String[] getTypeLabelCards() {
		return typeLabelCards;
	}

	Integer[] getTypeIdCards() {
		return typeIdCards;
	}

	String[] getCurrencyLabel() {
		return currencyLabel;
	}

	Integer[] getCurrencyId() {
		return currencyId;
	}

	private DBStorePlastic dbStorePlastic;

	public DBStorePlastic getDbStorePlastic() {
		return dbStorePlastic;
	}

	void init(DBStorePlastic dbStorePlastic) {

		this.dbStorePlastic = dbStorePlastic;

		try {
			initTypeCard();

			initCurrency();

			initCardProduct();

			initDepartment();

			initDesign();

			initSalaryClientJur();

		} catch (SessionException ignored) {
		}
	}

	private void initTypeCard() throws SessionException {
		List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_TYPE_CARDS, null);

		typeLabelCards = new String[datas.size()];

		typeIdCards = new Integer[datas.size()];

		int index = 0;
		for (Object[] row : datas) {

			typeIdCards[index] = ((Number) row[0]).intValue();

			typeLabelCards[index] = ((String) row[1]);
			index++;
		}
	}

	private void initCardProduct() throws SessionException {
		List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_CARD_PRODUCT, null);

		cardProductLabel = new String[datas.size()];

		cardProductId = new Integer[datas.size()];

		cardProductTypeLabel = new String[datas.size()];

		cardProductCurrencyLabel = new String[datas.size()];

		isSalaryCard = new Integer[datas.size()];

		cardProductDesign = new String[datas.size()];

		int index = 0;
		for (Object[] row : datas) {

			cardProductId[index] = ((Number) row[0]).intValue();

			cardProductLabel[index] = ((String) row[1]);

			cardProductTypeLabel[index] = ((String) row[2]);

			cardProductCurrencyLabel[index] = ((String) row[3]);

			isSalaryCard[index] = ((Number) row[4]).intValue();

			cardProductDesign[index] = ((String) row[5]);
			index++;
		}
	}

	private void initCurrency() throws SessionException {
		List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_CURRENCY, null);

		currencyLabel = new String[datas.size()];

		currencyId = new Integer[datas.size()];

		int index = 0;
		for (Object[] row : datas) {

			currencyId[index] = ((Number) row[0]).intValue();

			currencyLabel[index] = ((String) row[1]);
			index++;
		}
	}

	private void initDepartment() throws SessionException {
		List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_DEPARTMENT, null);

		departmentLabel = new String[datas.size()];

		departmentId = new Integer[datas.size()];

		int index = 0;
		for (Object[] row : datas) {

			departmentId[index] = ((Number) row[0]).intValue();

			departmentLabel[index] = ((String) row[1]);
			index++;
		}
	}

	private void initDesign() throws SessionException {
		List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_DESIGN, null);

		designLabel = new String[datas.size()];

		designId = new Integer[datas.size()];

		int index = 0;
		for (Object[] row : datas) {

			designId[index] = ((Number) row[0]).intValue();

			designLabel[index] = ((String) row[1]);
			index++;
		}
	}

	private void initSalaryClientJur() throws SessionException {
		List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_SALARY_CLIENT_JUR, null);

		salaryClientLabel = new String[datas.size() + 1];

		salaryClientId = new Integer[datas.size() + 1];

		salaryClientLabel[0] = "";
		salaryClientId[0] = null;

		int index = 1;
		for (Object[] row : datas) {

			salaryClientId[index] = ((Number) row[0]).intValue();

			salaryClientLabel[index] = ((String) row[1]);
			index++;
		}
	}
}
