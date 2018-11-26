package ru.barabo.plastic.release.reissue.gui;

import ru.barabo.plastic.release.reissue.data.ReIssueCardRowField;
import ru.barabo.total.db.FilteredStore;
import ru.barabo.total.gui.filter.impl.FilterTableSimple;
import ru.barabo.total.gui.table.TotalRowTable;

import javax.swing.*;
import java.awt.*;

public class PanelReIssueCard extends JPanel {
	
	//final static transient private Logger logger = Logger.getLogger(PanelReIssueCard.class.getName());
	
	
public PanelReIssueCard(FilteredStore<ReIssueCardRowField> store) {
		
		setLayout(new BorderLayout());
		
		TotalRowTable<ReIssueCardRowField> tableFocus = new TotalRowTable<>(store);
		
		add(new TopToolBarReIssueCard<>(store, tableFocus), BorderLayout.NORTH);
		
		JScrollPane rightPanel = new JScrollPane(tableFocus);
		
		
		FilterTableSimple filter = new FilterTableSimple(store,  tableFocus);
		
		JPanel panelFilter = new JPanel(new BorderLayout(), true);
		panelFilter.add(filter, BorderLayout.PAGE_START);
		panelFilter.add(rightPanel, BorderLayout.CENTER);
		
		
		
		add(panelFilter, BorderLayout.CENTER);
	}

}
