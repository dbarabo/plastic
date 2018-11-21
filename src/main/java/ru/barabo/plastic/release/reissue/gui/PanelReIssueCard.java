package ru.barabo.plastic.release.reissue.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import ru.barabo.plastic.release.reissue.data.ReIssueCardRowField;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FilteredStore;
import ru.barabo.total.gui.filter.impl.FilterTableSimple;
import ru.barabo.total.gui.table.TotalRowTable;

public class PanelReIssueCard extends JPanel {
	
	final static transient private Logger logger = 
			Logger.getLogger(PanelReIssueCard.class.getName());
	
	
public PanelReIssueCard(DBStore<ReIssueCardRowField> store) {
		
		setLayout(new BorderLayout());
		
		TotalRowTable<ReIssueCardRowField> tableFocus = new TotalRowTable<ReIssueCardRowField>(store);
		
		add(new TopToolBarReIssueCard<ReIssueCardRowField>(store, tableFocus), BorderLayout.NORTH);
		
		JScrollPane rightPanel = new JScrollPane(tableFocus);
		
		
		FilterTableSimple<ReIssueCardRowField> filter =
				new FilterTableSimple<ReIssueCardRowField>((FilteredStore) store,tableFocus);
		
		JPanel panelFilter = new JPanel(new BorderLayout(), true);
		panelFilter.add(filter, BorderLayout.PAGE_START);
		panelFilter.add(rightPanel, BorderLayout.CENTER);
		
		
		
		add(panelFilter, BorderLayout.CENTER);
	}

}
