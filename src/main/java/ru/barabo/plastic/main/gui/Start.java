package ru.barabo.plastic.main.gui;

import ru.barabo.afina.AfinaQuery;
import ru.barabo.afina.UserDepartment;
import ru.barabo.afina.VersionChecker;
import ru.barabo.afina.gui.ModalConnect;
import ru.barabo.db.SessionException;
import ru.barabo.gui.swing.ResourcesManager;
import ru.barabo.plastic.release.main.data.DBStorePlastic;
import ru.barabo.plastic.release.main.gui.ConfigPlastic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class Start extends JFrame{

	public Start() {

		if( !ModalConnect.initConnect(this) ) {

			System.exit(0);
		}

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

		VersionChecker.runCheckVersion("PLASTIC.JAR", 53);

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

       UserDepartment userDep = AfinaQuery.getUserDepartment();
       //String user = AfinaQuery.getUser();

        return String.format(TITLE, db, userDep.getUserName(), userDep.getDepartmentName(), userDep.getWorkPlace());
    }

    final private static String TITLE = "Пластиковые карты: [%s] [%s] [%s] [%s]";
}
