package ru.barabo.plastic.release.sms.select.gui;

import ru.barabo.plastic.release.sms.select.data.SmsSelectField;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FilteredStore;
import ru.barabo.total.gui.filter.impl.FilterTableSimple;
import ru.barabo.total.gui.table.TotalRowTable;

import javax.swing.*;
import java.awt.*;

public class PanelSelectSms extends JPanel {
	
	// final static transient private Logger logger = Logger.getLogger(ConfigPlastic.class.getName());
	
	
public PanelSelectSms(DBStore<SmsSelectField> store) {
		
		setLayout(new BorderLayout());
		
		TotalRowTable<SmsSelectField> tableFocus = new TotalRowTable<>(store);
		
		add(new TopToolBarSmsSelect<>(store, tableFocus), BorderLayout.NORTH);
		
		JScrollPane rightPanel = new JScrollPane(tableFocus);
				
		FilterTableSimple<SmsSelectField> filter =
				new FilterTableSimple<SmsSelectField>((FilteredStore) store,tableFocus);
		
		JPanel panelFilter = new JPanel(new BorderLayout(), true);
		panelFilter.add(filter, BorderLayout.PAGE_START);
		panelFilter.add(rightPanel, BorderLayout.CENTER);
				
		add(panelFilter, BorderLayout.CENTER);
	}

}