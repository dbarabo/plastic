package ru.barabo.plastic.release.application.gui;

import ru.barabo.plastic.release.application.data.AppCardRowField;
import ru.barabo.plastic.release.application.data.ClientRowField;
import ru.barabo.plastic.release.application.gui.table.FindClientTable;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.gui.detail.TableSheetForm;

import javax.swing.*;
import java.awt.*;

public class PanelApplication extends JPanel {

	//final static transient private Logger logger = Logger.getLogger(ConfigPlastic.class.getName());

	public PanelApplication(DBStore<AppCardRowField> store, DBStore<ClientRowField> clientFind) {

		setLayout(new BorderLayout());

		TableSheetForm<AppCardRowField> detailForm = new TableSheetForm<>(store, null);

		JScrollPane leftPanel = new JScrollPane(detailForm);

		FindClientTable<ClientRowField> findClientTable = new FindClientTable<>(clientFind, detailForm);

		JScrollPane rightPanel = new JScrollPane(findClientTable);

		JSplitPane splitBar = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);

		add(new TopToolBarApplication<>(store, detailForm), BorderLayout.NORTH);

		add(splitBar, BorderLayout.CENTER);

		splitBar.setResizeWeight(0.6);
	}
}
