package ru.barabo.plastic.release.sms.select.gui;

import ru.barabo.plastic.release.packet.data.PacketContentRowField;
import ru.barabo.plastic.release.packet.data.PacketRowField;
import ru.barabo.plastic.release.reissue.gui.TopToolBarReIssueCard;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacket;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacketContent;
import ru.barabo.plastic.release.sms.select.data.DBStoreSmsSelect;
import ru.barabo.plastic.release.sms.select.data.SmsSelectField;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.gui.any.AbstractTopToolBar;
import ru.barabo.total.gui.any.ButtonKarkas;
import ru.barabo.total.gui.any.ShowMenuListener;
import ru.barabo.total.gui.table.TotalRowTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TopToolBarSmsSelect <E extends AbstractRowFields> extends AbstractTopToolBar<E> {
	
	//final static transient private Logger logger = Logger.getLogger(TopToolBarSmsSelect.class.getName());
	
	private final static String NOT_SELECTED_CARDS = "Не выбрано ни одной карты в таблице!";
	
	private final static String DEF_NEW_PACKET_NAME = "SMS-заявления";
	
	private final static String SHOW_PHONE = "укажите 10-ти значный номер мобильного телефона (не считая +7 или 8)";
	private final static String	ERROR_NO_PHONE = "Номер телефона должен быть 10-ти значным";
	
	private final ButtonKarkas[] buttonSmsTypes = {
			new ButtonKarkas("smsAdd", "Отбор: SMS на подключение", this::selectSmsAdd, 0),
			new ButtonKarkas("smsRemove", "Отбор: SMS на отключение", this::selectselectSmsRemove, 0)
	};
	
	private final ButtonKarkas[] buttonKarkases = {
			new ButtonKarkas("sendPaket", "В пакет на SMS", this::addToPacket,	null),
			new ButtonKarkas(null, null, null, null),
			
			new ShowMenuListener(buttonSmsTypes).createButtonKarkas(0)
	};
	
	TopToolBarSmsSelect(DBStore<E> store, JComponent focusComp) {
		super(store, focusComp);
		initButton();
	}
	
	
	private void selectAll(ActionEvent e) {
		focusComp.requestFocus();
		JTable tbl = (JTable)focusComp;
		tbl.setRowSelectionInterval(0, tbl.getRowCount()-1);
	}
	
	private void selectSmsAdd(ActionEvent e) {
		((TotalRowTable)focusComp).setMustFullRefresh();
		store.setViewType(0);
		focusComp.requestFocus();
	}
	
	private void selectselectSmsRemove(ActionEvent e) {
		((TotalRowTable)focusComp).setMustFullRefresh();
		store.setViewType(1);
		focusComp.requestFocus();
	}
		
	
	private boolean isDigits10(String phone) {
		if(phone == null) return false;
		
		int count = 0;
		for(int index = 0; index < phone.length(); index++) {
			if(phone.charAt(index) >= '0' && phone.charAt(index) <= '9') {
				count++;
			}
		}
		
		return count >= 10;
	}
	
	/**
	 * добавляет в пакет на отправку 
	 */
	private void addToPacket(ActionEvent e) {
		DBStoreSmsSelect dBStoreSmsSelect = (DBStoreSmsSelect)store;
	
		DBStoreSmsPacket dBStoreSmsPacket = dBStoreSmsSelect.getDBStoreSmsPacket();
		
		JTable tbl = (JTable)focusComp;
		int[] rows =tbl.getSelectedRows();
		
		if(rows.length == 0) {
			TopToolBarReIssueCard.messageError(NOT_SELECTED_CARDS);
			return;
		}
		PacketRowField field = dBStoreSmsPacket.createDefaultRecord(DEF_NEW_PACKET_NAME);
		

		DBStoreSmsPacketContent content = dBStoreSmsSelect.getDBStoreSmsPacketContent();
		
		List<E> data = store.getData();
		for (int index : rows) {
			
			String phone = ((SmsSelectField)data.get(index)).getPhone();

			String newPhone = (String)JOptionPane.showInputDialog(null,
					null, SHOW_PHONE, JOptionPane.QUESTION_MESSAGE, null,
			        null, phone);
			
			if(!isDigits10(newPhone)) {
				TopToolBarReIssueCard.messageError(ERROR_NO_PHONE);
			} else {
				if(newPhone.equals(phone)) {
					newPhone = null;
				}
					content.createSmsRecord(field.getId(),
								data.get(index).getId(), store.getTypeSelect(), newPhone);
			}
		}
		
		dBStoreSmsPacket.refreshData();
		dBStoreSmsSelect.refreshData();
	}

	@Override
	protected ButtonKarkas[] getButtonKarkases() {
		return buttonKarkases;
	}

}
