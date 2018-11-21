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

	public static class SingletonHolder {
		static final StaticData HOLDER_INSTANCE = new StaticData();
	}

	public static StaticData getInstance() {
		return SingletonHolder.HOLDER_INSTANCE;
	}

	private String[] typeLabelCards;
	private int[] typeIdCards;

	private String[] currencyLabel;
	private int[] currencyId;

	private String[] cardProductLabel;
	private int[] cardProductId;
	private String[] cardProductTypeLabel;
	private String[] cardProductCurrencyLabel;
	private int[] isSalaryCard;
	private String[] cardProductDesign;


	private String[] departmentLabel;
	private int[] departmentId;

	private String[] designLabel;
	private int[] designId;

	int getIndexByProductLabel(String productLabel) {
		for (int index = 0; index < cardProductLabel.length; index++) {
			if (cardProductLabel[index].equals(productLabel)) {
				return index;
			}
		}

		return -1;
	}

	String[] getCardProductDesign() {
		return cardProductDesign;
	}


	int[] getIsSalaryCard() {
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

	int[] getDesignId() {
		return designId;
	}

	String[] getDepartmentLabel() {
		return departmentLabel;
	}

	int[] getDepartmentId() {
		return departmentId;
	}

	String[] getCardProductLabel() {
		return cardProductLabel;
	}

	int[] getCardProductId() {
		return cardProductId;
	}

	String[] getTypeLabelCards() {
		return typeLabelCards;
	}

	int[] getTypeIdCards() {
		return typeIdCards;
	}

	String[] getCurrencyLabel() {
		return currencyLabel;
	}

	int[] getCurrencyId() {
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

		} catch (SessionException ignored) {
		}
	}

	private void initTypeCard() throws SessionException {
		List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_TYPE_CARDS, null);

		typeLabelCards = new String[datas.size()];

		typeIdCards = new int[datas.size()];

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

		cardProductId = new int[datas.size()];

		cardProductTypeLabel = new String[datas.size()];

		cardProductCurrencyLabel = new String[datas.size()];

		isSalaryCard = new int[datas.size()];

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

		currencyId = new int[datas.size()];

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

		departmentId = new int[datas.size()];

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

		designId = new int[datas.size()];

		int index = 0;
		for (Object[] row : datas) {

			designId[index] = ((Number) row[0]).intValue();

			designLabel[index] = ((String) row[1]);
			index++;
		}
	}
}
