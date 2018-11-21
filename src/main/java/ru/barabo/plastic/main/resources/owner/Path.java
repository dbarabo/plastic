package ru.barabo.plastic.main.resources.owner;

import java.io.File;
import java.util.Calendar;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.apache.log4j.Logger;

@Sources({ "${cfgpath}/path.properties" })
public interface Path extends Config {

	final static public Logger logger = Logger.getLogger(Path.class.getName());

	@DefaultValue("Начальник УОД                Файзулина Людмила Габтерфетовна")
	String extractSigner();

	@DefaultValue("ivr_%tY%<tm%<td%<tH%<tM%<tS_%s.xml")
	String ivrXmlFileTemplate(Calendar calendar, String cardTail);
	
	default String ivrXmlFileName(String cardTail) {

		return ivrXmlFileTemplate(Calendar.getInstance(), cardTail);
	}

	default String ivrFullFilePath(String fileName) {
		return hCardOutDateNow() + "/" + fileName;
	}
	
	@DefaultValue("<allowPinSetting>\n\t<request>\n\t\t<callingSystemId>0226</callingSystemId>\n\t\t"
			+ "<cardId>%s=%s</cardId>\n\t\t<cardIdType>P</cardIdType>\n\t\t"
			+ "<clientPhone>%s</clientPhone>\n\t</request>\n</allowPinSetting>")
	String ivrXml(String card, String cardEnd, String phone);

	@DefaultValue("Невалидные данные у карты, срока или телефона")
	String errorCard();

	@DefaultValue("H:/КартСтандарт/in/")
	String hCardIn_();

	default String responseIvrPath_() {
		return hCardIn_();
	}

	default String responseIvrPathDateNow() {
		return getFolderDateNow(hCardIn_());
	}

	@DefaultValue("H:/КартСтандарт/out/")
	String hCardOut_();

	default String hCardOutDateNow() {

		return getFolderDateNow(hCardOut_());
	}

	default String getFolderDateNow(String mainFolder_) {

		String pathName = mainFolder_ + defaultDateFolderNow();

		createFolders(pathName);

		return pathName;
	}

	@DefaultValue("%tY/%tm/%td")
	String defaultDateFolder(Calendar calendarYear, Calendar calendarMonth, Calendar calendarDay);

	default String defaultDateFolder(Calendar calendar) {
		return defaultDateFolder(calendar, calendar, calendar);
	}

	default String defaultDateFolderNow() {
		return defaultDateFolder(Calendar.getInstance());
	}

	default void createFolders(String pathName) {
		final File dir = new File(pathName);

		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	@DefaultValue("Сотрудники доп. офисов не имют права самостоятельно формировать заявления на перевыпуск")
	String errorDopik();
}
