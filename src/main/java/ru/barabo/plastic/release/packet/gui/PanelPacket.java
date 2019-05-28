package ru.barabo.plastic.release.packet.gui;


import ru.barabo.plastic.packet.gui.ToolBarPacket;
import ru.barabo.plastic.release.packet.data.DBStorePacketAllContent;
import ru.barabo.plastic.release.packet.data.PacketContentRowField;
import ru.barabo.plastic.release.packet.data.PacketRowField;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FilteredStore;
import ru.barabo.total.gui.filter.impl.FilterAccessTable;
import ru.barabo.total.gui.table.TotalRowTable;

import javax.swing.*;
import java.awt.*;

public class PanelPacket extends JPanel {
	
	// final static transient private Logger logger = Logger.getLogger(PanelPacket.class.getName());

	public PanelPacket(FilteredStore<PacketRowField> store, DBStore<PacketContentRowField> storeContent,
					   DBStorePacketAllContent allContent) {
		
		setLayout(new BorderLayout());
		
		TotalRowTable<PacketRowField> tableFocus = new TotalRowTable<>(store);

		TotalRowTable<PacketContentRowField> tableContent = new TotalRowTable<>(storeContent);
		
		add(new ToolBarPacket<>(store, tableFocus, tableContent)/*new TopToolBarPacket<>(store, tableFocus, tableContent)*/, BorderLayout.NORTH);
		
		JScrollPane leftPanel = new JScrollPane(tableFocus);
		
		JScrollPane rightPanel = new JScrollPane(tableContent);
		
		FilterAccessTable<PacketRowField, PacketContentRowField> filter =
				new FilterAccessTable<>(
						allContent,
						store,
						PacketContentRowField.PLASTIC_PACK_FIELD,
						tableFocus,
						tableContent,
						storeContent);

		JPanel panelFilter = new JPanel(new BorderLayout(), true);
		panelFilter.add(filter, BorderLayout.PAGE_START);
		panelFilter.add(rightPanel, BorderLayout.CENTER);

		JSplitPane splitBar = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel, panelFilter /* rightPanel */);
		
		add(splitBar, BorderLayout.CENTER);
		
		splitBar.setResizeWeight(0.4);
	}

}