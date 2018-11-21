package ru.barabo.plastic.release.ivr.xml;

import org.apache.log4j.Logger;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.main.resources.owner.Cfg;
import ru.barabo.total.utils.StrUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ответ Ivr
 * @author debara
 *
 */
class IvrResponse {

	static private final int THEARD_SLEEP = 1500;

	static private final long MAX_WAIT_TIME = 7 * 60 * 1000; // 7 минут

	final static transient private Logger logger = Logger.getLogger(IvrResponse.class.getName());

	static private volatile Map<String, IvrInfo> map = new ConcurrentHashMap<>();

	static private volatile Map<Number, IvrInfo> mapDb = new ConcurrentHashMap<>();
	
	static private AtomicLong lastStartedDb = new AtomicLong(0);
	
	static private AtomicInteger countMapDb = new AtomicInteger(0);

	static private AtomicInteger countMap = new AtomicInteger(0);

	static private AtomicLong lastStarted = new AtomicLong(0);

	static private final String PATH_RESPONSE_ = Cfg.path().responseIvrPath_();

	static private final Map<Integer, String> ERROR_CODE = new HashMap<>();

	static private final String MESSAGE_IVR_ACTIVE = "IVR-сервис доступен в течении %d часов \n"
			+ "Позвоните по телефону %s (звонок бесплатный) и следуйте инструкциям";

	static {

		ERROR_CODE.put(-1, "Прочие ошибки");

		ERROR_CODE.put(11, "Незарегистрированный идентификатор вызывающей системы");

		ERROR_CODE.put(12, "Неподдерживаемый тип идентификатора карты");

		ERROR_CODE.put(13, "Плохой идентификатор карты");

		ERROR_CODE.put(14, "Нет такой карты");

		ERROR_CODE.put(15, "Плохой номер телефона");

		ERROR_CODE.put(16, "Плохой статус карты");

		ERROR_CODE.put(17, "Плохой сертификат или сертификат отсутствует");

		ERROR_CODE.put(91, "Сервис временно недоступен");

		ERROR_CODE.put(99, "Неопределенная внутренняя ошибка сервера");
	}

	synchronized static String addWaitResponse(String fileName, IvrInfo ivrInfo) {

		if (fileName == null || ivrInfo == null) {
			return "DATA is null";
		}

		if (map.get(fileName) != null) {
			return String.format("file already is exists WaitResponse %s", fileName);
		}

		map.put(fileName, ivrInfo);

		lastStarted = new AtomicLong(System.currentTimeMillis());
		if (countMap.getAndIncrement() == 0) {
			new Thread(IvrResponse::startTheard).start();
		}
		return null;
	}
	
	synchronized static private int isStateResponse(Number idRequest) {

		Object state = AfinaQuery.INSTANCE.selectValue(Cfg.query().selectIvrStateById(),
				new Object[] { idRequest });

		return !(state instanceof Number) ? -1 : ((Number)state).intValue();
	}

	synchronized static String addWaitResponseByDb(IvrInfo ivrInfo) {
		if (ivrInfo == null || ivrInfo.getIdRequest() == null) {
			return "DATA is null";
		}

		if (mapDb.get(ivrInfo.getIdRequest()) != null) {
			return String.format("request already is exists WaitResponse %d", ivrInfo.getIdRequest());
		}
		
		mapDb.put(ivrInfo.getIdRequest(), ivrInfo);

		lastStartedDb = new AtomicLong(System.currentTimeMillis());

		if (countMapDb.getAndIncrement() == 0) {
			new Thread(IvrResponse::startTheardDb).start();
		}

		return null;
	}

	static private void startTheardDb() {
		do {
			try {
				Thread.sleep(THEARD_SLEEP);
			} catch (InterruptedException e) {
				logger.error("startTheard", e);
			}

			for (Map.Entry<Number, IvrInfo> entry : mapDb.entrySet()) {

				if (isStateResponse(entry.getKey()) != 0) {
					sendMessageByResponse(entry.getKey());
				}
			}

			// валимся по тайм-ауту
			if (System.currentTimeMillis() - lastStartedDb.get() > MAX_WAIT_TIME) {
				sendErrorTimeOutDb();
				break;
			}

		} while (countMapDb.get() > 0);
	}


	static private void startTheard() {
		do {
			try {
				Thread.sleep(THEARD_SLEEP);
			} catch (InterruptedException e) {
				logger.error("startTheard", e);
			}

			for(Map.Entry<String, IvrInfo> entry : map.entrySet()) {

				if (new File(PATH_RESPONSE_ + entry.getKey()).exists()) {

					readDataFile(entry.getKey());
				}
			}

			// валимся по тайм-ауту
			if (System.currentTimeMillis() - lastStarted.get() > MAX_WAIT_TIME) {
				sendErrorTimeOut();
				break;
			}

		} while (countMap.get() > 0);
	}
	
	static private IvrInfo removeKey(String fileName) {
		IvrInfo ivr = map.remove(fileName);
		if (ivr != null) {
			countMap.decrementAndGet();
		}

		return ivr;
	}

	static private IvrInfo removeKey(Number idRequest) {
		IvrInfo ivr = mapDb.remove(idRequest);
		if (ivr != null) {
			countMapDb.decrementAndGet();
		}

		return ivr;
	}

	static private void sendErrorTimeOutDb() {

		StringBuilder error = new StringBuilder("Ошибка времени ожидания от сервера: обратитесь в тех. поддержку\n"
				+ "Активации не произошло по следующим клиентам\n");

		for (IvrInfo ivr : mapDb.values()) {
			error.append(ivr.getClientName()).append("\n");
		}

		countMapDb = new AtomicInteger(0);
		mapDb.clear();

		final String message = error.toString();

		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message,
				"Ошибка времени ожидания от сервера IVR",
				JOptionPane.ERROR_MESSAGE));
	}

	static private void sendErrorTimeOut() {

		StringBuilder error = new StringBuilder("Ошибка времени ожидания от сервера: обратитесь в тех. поддержку\n"
				+ "Активации не произошло по следующим клиентам\n");

		for (IvrInfo ivr : map.values()) {
			error.append(ivr.getClientName()).append("\n");
		}

		countMap = new AtomicInteger(0);
		map.clear();

		final String message = error.toString();

		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message,
				"Ошибка времени ожидания от сервера IVR",
				JOptionPane.ERROR_MESSAGE));
	}

	static private int sendError(String fileName, String errorMessage) {

		IvrInfo ivr = removeKey(fileName);
		if(ivr == null) {
			return -1;
		}

		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, errorMessage,
				"Ошибка IVR: " + ivr.getClientName(),
				JOptionPane.ERROR_MESSAGE));

		return 0;
	}

	static private int sendError(Number idRequest, String errorMessage) {

		IvrInfo ivr = removeKey(idRequest);
		if (ivr == null) {
			return -1;
		}

		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, errorMessage,
				"Ошибка IVR: " + ivr.getClientName(),
				JOptionPane.ERROR_MESSAGE));

		return 0;
	}

	static private int sendError(String fileName, int errorCode) {

		String error = ERROR_CODE.get(errorCode);
		if (error == null) {
			error = String.format("Неизвестная ошибка код=%d", errorCode);
		}

		return sendError(fileName, error);
	}

	static private void sendError(Number idRequest, int errorCode) {

		String error = ERROR_CODE.get(errorCode);
		if (error == null) {
			error = String.format("Неизвестная ошибка код=%d", errorCode);
		}

		sendError(idRequest, error);
	}

	static private int sendSuccessPin(String fileName, String servicePhone, String timeMinute) {

		IvrInfo ivr = removeKey(fileName);
		if (ivr == null) {
			return -1;
		}

		return showMessageSuccess(ivr, servicePhone, timeMinute);
	}

	static private int showMessageSuccess(IvrInfo ivr, String servicePhone, String timeMinute) {
		int hours = timeMinute == null || "".equals(timeMinute.trim())
				? 24 * 60 : Integer.parseInt(timeMinute.trim());

		hours = hours / 60;

		String message = String.format(MESSAGE_IVR_ACTIVE, hours, servicePhone);

		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message,
				"IVR активен:" + ivr.getClientName(),
				JOptionPane.INFORMATION_MESSAGE));

		return 0;
	}

	static private void sendSuccessPin(Number idRequest, String servicePhone, String timeMinute) {
		IvrInfo ivr = removeKey(idRequest);
		if (ivr == null) {
			return;
		}

		showMessageSuccess(ivr, servicePhone, timeMinute);
	}

	static private void sendMessageByResponse(Number idRequest) {

		try {
			List<Object[]> rows = AfinaQuery.INSTANCE.select(Cfg.query().selectIvrById(), new Object[]{idRequest});

			final Object[] row = rows.get(0);

			final int state = row[0] == null ? -1 : ((Number) row[0]).intValue();

			final String servicePhone = (String) row[1];

			final String waitMinute = (String) row[2];

			if (state != 1) {
				sendError(idRequest, state);
			} else {
				sendSuccessPin(idRequest, servicePhone, waitMinute);
			}

		} catch (Exception e) {
			sendError(idRequest, -1);
		}
	}

	static private void readDataFile(String fileName) {
		String data = null;

		try {
			data = new String(Files.readAllBytes(Paths.get(PATH_RESPONSE_ + fileName)));
		} catch (IOException e) {
			logger.info("readDataFile", e);
			sendError(fileName, e.getMessage());
			return;
		}

		new File(PATH_RESPONSE_ + fileName).renameTo(
				new File(Cfg.path().responseIvrPathDateNow() + "/" + fileName));

		Pattern patter = Pattern.compile("status(.*?)[<|lifeTime]");
		Matcher matcher = patter.matcher(data);

		String stat = matcher.find() ? StrUtils.getDigitsOnly(matcher.group(1)) : null;

		patter = Pattern.compile("lifeTime(.*?)[<|servicePhone]");
		matcher = patter.matcher(data);
		String timeMinute = matcher.find() ? StrUtils.getDigitsOnly(matcher.group(1)) : null;

		patter = Pattern.compile("servicePhone(.*?)[<|response]");
		matcher = patter.matcher(data);
		String servicePhone = matcher.find() ? StrUtils.getDigitsOnly(matcher.group(1)) : null;

		final int status = stat == null || "".equals(stat) ? -1 : Integer.parseInt(stat);

		if (status != 0) {
			sendError(fileName, status);
		} else {
			sendSuccessPin(fileName, servicePhone, timeMinute);
		}
	}

}
