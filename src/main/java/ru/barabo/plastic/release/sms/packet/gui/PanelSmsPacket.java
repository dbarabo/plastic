package ru.barabo.plastic.release.sms.packet.gui;

import ru.barabo.plastic.release.packet.data.PacketContentRowField;
import ru.barabo.plastic.release.packet.data.PacketRowField;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.gui.table.TotalRowTable;

import javax.swing.*;
import java.awt.*;


public class PanelSmsPacket extends JPanel {
	
	// final static transient private Logger logger = Logger.getLogger(PanelSmsPacket.class.getName());
	
	
public PanelSmsPacket(DBStore<PacketRowField> store, DBStore<PacketContentRowField> storeContent) {
		
		setLayout(new BorderLayout());
		
		TotalRowTable<PacketRowField> tableFocus = new TotalRowTable<>(store);

		TotalRowTable<PacketContentRowField> tableContent = new TotalRowTable<>(storeContent);
		
		add(new TopToolBarSmsPacket(store, tableFocus), BorderLayout.NORTH);
		
		JScrollPane leftPanel = new JScrollPane(tableFocus);
		
		JScrollPane rightPanel = new JScrollPane(tableContent);
		
		JSplitPane splitBar = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel,  rightPanel);
		
		add(splitBar, BorderLayout.CENTER);
		
		splitBar.setResizeWeight(0.4);
	}
}