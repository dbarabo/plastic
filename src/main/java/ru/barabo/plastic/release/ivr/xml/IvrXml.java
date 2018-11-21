package ru.barabo.plastic.release.ivr.xml;

import org.apache.log4j.Logger;
import ru.barabo.plastic.main.resources.owner.Cfg;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class IvrXml {

	final static transient private Logger logger = Logger.getLogger(IvrXml.class.getName());

	private final static String PIN_IS_SEND = "Запрос отправлен. Ждите ответа...";

	private final static String PIN_MESSAGE = "Внимание! Старый ПИН-код будет сброшен и клиент не сможет воспользоваться картой!\n"
			+ "Не меняйте ПИН-код без ведома клиента!\nПроверьте корректность номера телефона на который придет новый ПИН-код\n";

	static private String saveIvr(IvrInfo ivrInfo) {

		final String error = checkBeforeSave(ivrInfo);
		if (error != null) {
			return error;
		}


		// final String fileName = Cfg.path().ivrXmlFileName(
		// ivrInfo.getCardName().substring(ivrInfo.getCardName().length() - 4));
		final String fullFileName = Cfg.path().ivrFullFilePath(ivrInfo.getFileName());

		try {
			Files.write(Paths.get(fullFileName), Cfg.path().ivrXml(ivrInfo.getCardName(),
					ivrInfo.getCardEnd(), ivrInfo.getPhone())
					.getBytes());
		} catch (IOException e) {
			logger.error("saveIvr IOException", e);
			return e.getMessage();
		}

		return ivrInfo.getFileName();
	}

	/**
	 * создает запись запроса
	 */
	static private String createRecord(IvrInfo ivrInfo) {
		String error = checkBeforeSave(ivrInfo);
		if (error != null) {
			return error;
		}

		error = ivrInfo.createRequestDb().getError();
		if (error != null) {
			return messageError(error);
		}

		return null;
	}

	static private String checkBeforeSave(IvrInfo ivrInfo) {
		ivrInfo = ivrInfo.check(true);

		if (ivrInfo == null) {
			return Cfg.path().errorCard();
		}

		final String fileName = Cfg.path().ivrXmlFileName(
				ivrInfo.getCardName().substring(ivrInfo.getCardName().length() - 4));

		ivrInfo.setFileName(fileName);

		return null;
	}

	/**
	 * запускает процесс ожидания ответа создавая запись в Афине
	 */
	static public void startIvrProccessByDb(IvrInfo ivrInfo) {
		String error = startIvrProccessBefore(ivrInfo);
		if (error != null) {
			return;
		}

		error = createRecord(ivrInfo);
		if (error != null) {
			return;
		}

		error = IvrResponse.addWaitResponseByDb(ivrInfo);
		if (error != null) {
			messageError(error);
			return;
		}

		JOptionPane.showMessageDialog(null, PIN_IS_SEND, ivrInfo.getClientName(),
				JOptionPane.INFORMATION_MESSAGE);

	}

	static private String startIvrProccessBefore(IvrInfo ivrInfo) {
		if (ivrInfo == null) {
			return messageError("IvrInfo is null");
		}

		String newPhone = (String) JOptionPane.showInputDialog(null,
				PIN_MESSAGE, ivrInfo.getClientName(), JOptionPane.QUESTION_MESSAGE, null,
				null, ivrInfo.getPhone());

		String error = ivrInfo.savePhone(newPhone);
		if (error != null) {
			return messageError(error);
		}

		return null;
	}

	/**
	 * запускает процесс ожидания ответа создавая файл в h:/картстандарт
	 * 
	 */
	static public String startIvrProccess(IvrInfo ivrInfo) {

		String error = startIvrProccessBefore(ivrInfo);
		if (error != null) {
			return error;
		}

		String errorFileName = IvrXml.saveIvr(ivrInfo);
		if (errorFileName != null && errorFileName.indexOf("ivr_") != 0) {
			return messageError(error);
		}

		error = IvrResponse.addWaitResponse(errorFileName, ivrInfo);
		if (error != null) {
			return messageError(error);
		}

		JOptionPane.showMessageDialog(null, PIN_IS_SEND, ivrInfo.getClientName(),
				JOptionPane.INFORMATION_MESSAGE);

		return null;
	}

	private static String messageError(String error) {

		JOptionPane.showMessageDialog(null,
				error, null, JOptionPane.ERROR_MESSAGE);

		return error;
	}
}
