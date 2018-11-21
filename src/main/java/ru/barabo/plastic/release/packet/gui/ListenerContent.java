package ru.barabo.plastic.release.packet.gui;

import ru.barabo.plastic.release.packet.data.PacketContentRowField;
import ru.barabo.total.db.ListenerStore;

import java.util.List;

public class ListenerContent implements ListenerStore<PacketContentRowField> {
	
	private TopToolBarPacket topToolBar;
	
	ListenerContent(TopToolBarPacket topToolBar) {
		this.topToolBar = topToolBar;
	}

	@Override
	public void setCursor(PacketContentRowField row) {
		topToolBar.refreshStateContentButton(row);
		
	}

	@Override
	public void refreshData(List<PacketContentRowField> allData) {
		
		PacketContentRowField row = allData == null || allData.size() == 0 ? null : allData.get(0);
		
		topToolBar.refreshStateContentButton(row);
		
	}

}
