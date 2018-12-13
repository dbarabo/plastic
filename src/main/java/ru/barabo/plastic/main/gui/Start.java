package ru.barabo.plastic.main.gui;

import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.plastic.afina.VersionChecker;
import ru.barabo.plastic.main.resources.ResourcesManager;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.main.gui.ConfigPlastic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class Start extends JFrame{
	
	//final static transient private Logger logger = Logger.getLogger(Start.class.getName());

	public Start() {

		if(!ModalConnect.initConnect(this)) {
			System.exit(0);
		}

		VersionChecker.runCheckVersion();

        try {
            DBStorePlastic plastic = new DBStorePlastic();

            buildUI(plastic);
        } catch (SessionException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),null, JOptionPane.ERROR_MESSAGE );
            System.exit(0);
        }

	}

	private JComponent buildPlasticAuto(DBStorePlastic store) {
		
		return new ConfigPlastic(store);
	}

   /**
	 * рисуем интерфейс
	 */
	private void buildUI(DBStorePlastic plastic) {
		
		JComponent mainPanel = buildPlasticAuto(plastic);

		getContentPane().setLayout( new BorderLayout() );
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		setTitle( title() );

        setIconImage(Objects.requireNonNull(ResourcesManager.getIcon("plastic")).getImage());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                VersionChecker.exitCheckVersion();
            }
        });

		pack();
	    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setVisible( true );
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private String title() {
       String db = AfinaQuery.isTestBaseConnect() ? "TEST" : "AFINA";

        String user = AfinaQuery.getUser();

        return String.format(TITLE, db, user);
    }

    final private static String TITLE = "Пластик выпуск-перевыпуск: [%s] [%s]";
}
