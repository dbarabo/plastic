package ru.barabo.plastic.release.main.gui;

import ru.barabo.plastic.card.gui.PanelCards;
import ru.barabo.plastic.release.application.gui.PanelApplication;
import ru.barabo.plastic.release.main.data.DBStorePlastic;

import javax.swing.*;
import java.awt.*;

/**
 * главная панель пластика - а книгу добавляется
 * @author debara
 *
 */
public class ConfigPlastic extends JPanel {
	
	public ConfigPlastic(DBStorePlastic store) {

		JTabbedPane book = new JTabbedPane(JTabbedPane.TOP);
		
		setLayout(new BorderLayout());

		book.addTab(PanelCards.TITLE, new PanelCards() );

		book.addTab("Заявление на Выпуск",
					new PanelApplication(store.getApplicationCard(), store.getClientFind()));

		add(book, BorderLayout.CENTER);
	}
}
