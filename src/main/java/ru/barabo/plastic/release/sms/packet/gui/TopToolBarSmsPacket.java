package ru.barabo.plastic.release.sms.packet.gui;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import org.apache.log4j.Logger;


import ru.barabo.plastic.release.packet.data.PacketRowField;
import ru.barabo.plastic.release.packet.gui.TopToolBarPacket;
import ru.barabo.plastic.release.reissue.gui.TopToolBarReIssueCard;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacket;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.gui.any.ButtonKarkas;

public class TopToolBarSmsPacket extends TopToolBarPacket<PacketRowField> {
	
	static private final Logger logger = Logger.getLogger(TopToolBarSmsPacket.class.getName());
	
	
	private ButtonKarkas[] stateButton;


	TopToolBarSmsPacket(DBStore<PacketRowField> store,
							   JComponent focusComp) {
		super(store, focusComp, null);
		
		stateButton = new ButtonKarkas[]{
			new ButtonKarkas("sms", "Отправить на SMS", this::toCreateFileSmsState, null),         //0 NEW
			
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			new ButtonKarkas("wait", "Ждём", this::wait, null),
			
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        // SMS_SENT
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        // SMS_SENT_ACCESS
			new ButtonKarkas("sms", "Вернуть в SMS", this::toSmsState, null),          // SMS_SENT_ERROR 
			new ButtonKarkas(null, null, null, null),                                  // SMS_RESPONSE_OK_ALL_OIA
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        // SMS_RESPONSE_OK_PART_OIA 
			new ButtonKarkas("sms", "Вернуть в SMS", this::toSmsState, null),          // SMS_RESPONSE_ERROR_ALL_OIA
			new ButtonKarkas("sms", "Вернуть в SMS", this::toSmsState, null)          // SMS_RESPONSE_ERROR_PART_OIA
	};
		
		stateButtonIndex = -1;
		
		//logger.info("CREATED=" + stateButton);
		
		refreshData(store.getData());
		
	}
	
	private void toCreateFileSmsState(ActionEvent e) {
		DBStoreSmsPacket dBStoreSmsPacket = (DBStoreSmsPacket)store;
		String result = dBStoreSmsPacket.createSms();
		if(result != null) {
			TopToolBarReIssueCard.messageError(result);
		}
		dBStoreSmsPacket.refreshData();
	
	}
	
	private void toSmsState(ActionEvent e) {
		DBStoreSmsPacket dBStoreSmsPacket = (DBStoreSmsPacket)store;
		String result = dBStoreSmsPacket.toSmsState();
		if(result != null) {
			TopToolBarReIssueCard.messageError(result);
		}
		dBStoreSmsPacket.refreshData();

		focusComp.requestFocus();
	}
	
	protected ButtonKarkas getStateButton(int stateButtonIndex) {
		logger.info("stateButtonIndex=" + stateButtonIndex);
		
		//logger.info("stateButtons=" + stateButton);
		
		return stateButton == null || stateButtonIndex >= stateButton.length ?  null : stateButton[stateButtonIndex];
	}
	
	protected int getStateButtonsCount() {
		//logger.info("stateButtons=" + stateButton);
		return stateButton == null ? 0 : stateButton.length;
	}

}
