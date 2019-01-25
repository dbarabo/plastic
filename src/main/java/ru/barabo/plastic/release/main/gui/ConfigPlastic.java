package ru.barabo.plastic.release.main.gui;

import org.apache.log4j.Logger;
import ru.barabo.plastic.release.application.gui.PanelApplication;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.packet.data.DBStorePacket;
import ru.barabo.plastic.release.packet.gui.PanelPacket;
import ru.barabo.plastic.release.reissue.gui.PanelReIssueCard;
import ru.barabo.plastic.release.sms.packet.gui.PanelSmsPacket;
import ru.barabo.plastic.release.sms.select.gui.PanelSelectSms;
import ru.barabo.plastic.schema.gui.MainSchemaTab;
import ru.barabo.plastic.schema.gui.account.TabAccount;
import ru.barabo.plastic.unnamed.gui.PanelUnnamed;

import javax.swing.*;
import java.awt.*;

/**
 * главная панель пластика - а книгу добавляется
 * @author debara
 *
 */
public class ConfigPlastic extends JPanel {
	
	final static transient private Logger logger = Logger.getLogger(ConfigPlastic.class.getName());
	

	public ConfigPlastic(DBStorePlastic store) {
		
		JTabbedPane book = new JTabbedPane(JTabbedPane.TOP);
		
		setLayout(new BorderLayout());
		
		boolean isDopik = ((DBStorePacket) store.getPacket()).isWorkPlaceDopik();

		logger.info("isDopik=" + isDopik);

		book.addTab("Карты на перевыпуск", new PanelReIssueCard(store.getReIssueCard()));

		book.addTab("Пакеты на отправку", new PanelPacket(store.getPacket(), store.getContent(),
				store.getAllContent()));
		
		book.addTab("Карты на SMS", new PanelSelectSms(store.getSmsSelect()));

		book.addTab("Пакеты на SMS",
					new PanelSmsPacket(store.getSmsPacket(), store.getSmsContent()));

        book.addTab("Неименные карты",	new PanelUnnamed(store));

		book.addTab("Заявление на Выпуск",
					new PanelApplication(store.getApplicationCard(), store.getClientFind()));

		book.addTab("Схемы", new MainSchemaTab() );

		//book.addTab("Настройки", new ServicePanel());


		add(book, BorderLayout.CENTER);
	}
}
