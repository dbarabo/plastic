package ru.barabo.total.gui.any;

import ru.barabo.plastic.main.resources.ResourcesManager;
import ru.barabo.total.db.DBStore;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTopToolBar <E> extends JToolBar {

	protected DBStore<E> store;
	
	private List<ButtonGroup> groups;
	
	protected JComponent focusComp;

	
	abstract protected ButtonKarkas[] getButtonKarkases();
	
	public AbstractTopToolBar(DBStore<E> store, JComponent focusComp) {
		this.store = store;
		
		this.focusComp = focusComp;
		
		setLayout(new FlowLayout(FlowLayout.LEFT) );
		
		setFloatable(true);
		
	}

	protected void initButton() {
		for(ButtonKarkas karkas : getButtonKarkases()) {
			if(karkas.getName() == null) {
				this.addSeparator();
			} else {
				add(createButton(karkas) );
			}
		}
	}
	
	private void addGroup(AbstractButton button, int index) {
		 
		if(groups == null) {
			groups = new ArrayList<>();
		}
		
		if(groups.size() <= index) {
			groups.add(new ButtonGroup());
		}
		
		groups.get(index).add(button);
	}
	
	protected AbstractButton createButton(ButtonKarkas karkas) {
		if(karkas.getName() == null) return null;
		
		ImageIcon icon = ResourcesManager.getIcon(karkas.getIco());
		
		AbstractButton button;
		
		if(karkas.getGroupIndex() != null) {
			button = new JToggleButton(icon);
			addGroup(button, karkas.getGroupIndex());
		} else {
			button = new JButton(icon);
		}
		// show caption on
		button.setText(karkas.getName());
		
		button.setToolTipText(karkas.getName());
		button.addActionListener(karkas.getListener() );
		karkas.setButton(button);
		
		return button;
	}

}

