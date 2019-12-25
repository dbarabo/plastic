package ru.barabo.plastic.release.ivr.xml;

import org.apache.log4j.Logger;
import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.main.resources.owner.Cfg;
import ru.barabo.total.utils.StrUtils;
import ru.barabo.total.utils.Util;

import java.util.List;

public class IvrInfo {

	final static transient private Logger logger = Logger.getLogger(IvrInfo.class.getName());

	private String cardName;

	private String cardEnd;

	private String phone;

	private Number cardId;

	private String clientName;

	private transient String fileName;

	private transient Number idRequest;

	private transient String error;

	private IvrInfo(String cardName, String cardEnd, String phone, Number cardId, String clientName) {

		this.fileName = null;

		this.cardName = StrUtils.getDigitsOnly(cardName);

		this.cardEnd = StrUtils.getDigitsOnly(cardEnd);

		this.phone = StrUtils.getDigitsOnly(phone);

		this.cardId = cardId;

		this.clientName = clientName;
	}

	public String getError() {
		return error;
	}

	Number getIdRequest() {
		return idRequest;
	}

	/**
	 * создает запрос в афине на IVR
	 */
	IvrInfo createRequestDb() {

		idRequest = AfinaQuery.nextSequence();

		Object[] params = new Object[] {
				idRequest,
				cardName == null ? String.class : cardName,
				phone == null ? String.class : phone,
				cardEnd == null ? String.class : cardEnd,
				clientName == null ? String.class : clientName,
				fileName == null ? String.class : fileName };

		error = null;
		try {
			AfinaQuery.INSTANCE.execute(Cfg.query().insertIvrRequest(), params);
		} catch (SessionException e) {
			error = e.getMessage();
		}

		return this;
	}

	IvrInfo check(boolean isCheckPhone) {

		if (cardId == null) {
			return null;
		}

		if (cardName == null || cardName.length() < 16) {
			return null;
		}

		if (cardEnd == null || cardEnd.length() != 4) {
			return null;
		}

		if (clientName == null || "".equals(clientName.trim())) {
			return null;
		}

		if (!isCheckPhone) {
			return this;
		}

		phone = phoneUpdate(phone);
		if (phone == null) {
			return null;
		}

		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private static String phoneUpdate(String phone) {
		if (phone == null) {
			return null;
		}

		phone = StrUtils.getDigitsOnly(phone);

		if (phone.length() == 11 && (phone.charAt(0) == '7' || phone.charAt(0) == '8')) {
			phone = phone.substring(1);
		}

		if (phone.length() != 10) {
			return null;
		}

		return phone;
	}

	/**
	 * создает запись запроса в Афине
	 */
	public static IvrInfo create(Number cardId) {

		List<Object[]> datas = null;
		try {
			datas = AfinaQuery.INSTANCE.selectCursor(Cfg.query().selectIvrInfo(), new Object[] { cardId });
		} catch (SessionException ignored) { }

		Object[] row = (datas == null || datas.size() == 0) ? null : datas.get(0);

		if (Util.isExistsNullElement(row)) {
			return null;
		}

		return new IvrInfo((String) row[0], (String) row[1], (String) row[2],
				(Number) row[3],
				(String) row[4]).check(false);
	}

	String savePhone(String phone) {
		logger.info("cardId=" + cardId);
		phone = phoneUpdate(phone);
		if (phone == null || cardId == null) {
			return "Некорректный номер телефона";
		}

		if (phone.equals(this.phone)) {
			return null;
		}

		this.phone = phone;

		try {
			AfinaQuery.INSTANCE.execute(Cfg.query().updatePhoneCard(), new Object[] { phone, cardId });
		} catch (SessionException e) {
			return e.getMessage();
		}
		return null;
	}

	String getClientName() {
		return clientName;
	}

	String getCardName() {
		return cardName;
	}

	String getCardEnd() {
		return cardEnd;
	}

	String getPhone() {
		return phone;
	}
}
