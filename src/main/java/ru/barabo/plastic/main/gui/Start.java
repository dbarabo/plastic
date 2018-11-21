package ru.barabo.plastic.main.gui;

import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.main.gui.ConfigPlastic;

import javax.swing.*;
import java.awt.*;

public class Start extends JFrame{
	
	//final static transient private Logger logger = Logger.getLogger(Start.class.getName());

	public Start() {

		if(!ModalConnect.initConnect(this)) {
			System.exit(0);
		}

		DBStorePlastic plastic = new DBStorePlastic();

		buildUI(plastic);
	}

	private JComponent buildPlasticAuto(DBStorePlastic store) {
		
		return new ConfigPlastic(store);
	}

	final private static String TITLE = "Пластик выпуск-перевыпуск";

	/**
	 * рисуем интерфейс
	 */
	private void buildUI(DBStorePlastic plastic) {
		
		JComponent mainPanel = buildPlasticAuto(plastic);

		getContentPane().setLayout( new BorderLayout() );
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		this.setTitle(TITLE);
		pack();
	    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setVisible( true );
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
}
