package ru.barabo.plastic.release.reissue.gui;

import org.apache.log4j.Logger;
import ru.barabo.gui.swing.ButtonKarkas;
import ru.barabo.gui.swing.StaticMenu;
import ru.barabo.plastic.card.gui.PanelCards;
import ru.barabo.plastic.fio.gui.FioChangeTab;
import ru.barabo.plastic.main.resources.owner.Cfg;
import ru.barabo.plastic.release.ivr.xml.IvrInfo;
import ru.barabo.plastic.release.ivr.xml.IvrXml;
import ru.barabo.plastic.release.packet.data.DBStorePacket;
import ru.barabo.plastic.release.packet.data.DBStorePacketContent;
import ru.barabo.plastic.release.packet.data.PacketContentRowField;
import ru.barabo.plastic.release.packet.data.PacketRowField;
import ru.barabo.plastic.release.reissue.data.DBStoreReIssueCard;
import ru.barabo.plastic.release.reissue.data.ReIssueCardRowField;
import ru.barabo.plastic.release.reissue.data.TypeSelect;
import ru.barabo.plastic.schema.gui.MainSchemaTab;
import ru.barabo.plastic.schema.gui.selector.SelectorTab;
import ru.barabo.plastic.terminal.gui.TabPosTerminal;
import ru.barabo.plastic.unnamed.gui.PanelUnnamed;
import ru.barabo.plastic.unnamed.gui.TopToolBarInPath;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.gui.any.AbstractTopToolBar;
import ru.barabo.total.gui.any.ShowMenuListener;
import ru.barabo.total.gui.table.TotalRowTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class TopToolBarReIssueCard<E extends AbstractRowFields> extends AbstractTopToolBar {
	
	final static transient private Logger logger = Logger.getLogger(TopToolBarReIssueCard.class.getName());
	
	private final static String NOT_SELECTED_CARDS = "Не выбрано ни одной карты в таблице!";

	private final static String NOT_PINCODE_FILTER = "Для смены Пин-кода тип отбора должен стоять <Отбор: Карты по утере/краже>";

	private final static String DEF_NEW_PACKET_NAME = "Перевыпуск карт по сроку";
	
	private final static String INCORRECT_DATA = "Некорректные данные";

	private final ButtonKarkas[] selectTypeReissue = {

			new ButtonKarkas("time", "Отбор: Карты истекшие", 0, this::selectTime),
			new ButtonKarkas("lost", "Отбор: Карты действующие", 0, this::selectLost),
			new ButtonKarkas("application", "Отбор: Заявления 'На отправку'",0, this::selectApplication)
	};

	private final ButtonKarkas[] moreDelbIssue = {
			new ButtonKarkas("unnamed", "Неименные карты", 0, this::showUnnamedCard),
			new ButtonKarkas("user", "Смена персональных данных", 0, this::showChangeFio),

			new ButtonKarkas("pos", TabPosTerminal.TITLE, 0, this::showTerminal),
			new ButtonKarkas("schema", "Схемы транзакций", 0, this::showShema),

			new ButtonKarkas(null, "???", 0, this::showNewCard)
	};
	
	private final ButtonKarkas[] buttonKarkases = {
			new ButtonKarkas("sendPaket", "В пакет на отправку", null, this::addToPacket),
			new ButtonKarkas("death", "В пакет BTRT25", null,  this::addToPacketBtrt25),
			new ButtonKarkas(null, null, null, null),
			
			new ShowMenuListener(selectTypeReissue).createButtonKarkas(0),

			new ButtonKarkas(null, null, null, null),

			new ButtonKarkas("password", "сменить ПИН-код!", null, this::changePin),

			new ButtonKarkas(null, null, null, null),

			new StaticMenu("more", "Ещё...", moreDelbIssue)
	};

	private DBStore<E> store;
	
	TopToolBarReIssueCard(DBStore<E> store, JComponent focusComp) {
		super(focusComp);

		this.store = store;

		initButton();

		// AbstractButton btrt25 = getButtonKarkases()[1].getButton();
		// btrt25.setVisible( ((DBStoreReIssueCard) store).getDBStorePacket().isSuperWorkspace() );
	}

	private void showTerminal(ActionEvent event) {

		TopToolBarInPath.addNewTabIfAbsent( getButtonKarkases()[0].getButton(), TabPosTerminal.TITLE, TabPosTerminal.class );
	}

	private void showShema(ActionEvent event) {

		TopToolBarInPath.addNewTabIfAbsent( getButtonKarkases()[0].getButton(), MainSchemaTab.TITLE, MainSchemaTab.class );
	}

	private void showChangeFio(ActionEvent event) {

		TopToolBarInPath.addNewTabIfAbsent( getButtonKarkases()[0].getButton(), FioChangeTab.TITLE, FioChangeTab.class );
	}

	private void showUnnamedCard(ActionEvent event) {

		TopToolBarInPath.addNewTabIfAbsent( getButtonKarkases()[0].getButton(), PanelUnnamed.TITLE, PanelUnnamed.class );
	}

	private void showNewCard(ActionEvent event) {

		TopToolBarInPath.addNewTabIfAbsent( getButtonKarkases()[0].getButton(), PanelCards.TITLE, PanelCards.class );
	}


	public static void messageError(String error) {
		JOptionPane.showMessageDialog(null, 
				error,  null, JOptionPane.ERROR_MESSAGE );
	}
	
	
	private void selectAll(ActionEvent e) {
		focusComp.requestFocus();
		JTable tbl = (JTable)focusComp;
		tbl.setRowSelectionInterval(0, tbl.getRowCount()-1);
	}
	
	private void selectTime(ActionEvent e) {
		((TotalRowTable)focusComp).setMustFullRefresh();
		store.setViewType(0);
		focusComp.requestFocus();
	}
	
	private void selectLost(ActionEvent e) {
		((TotalRowTable)focusComp).setMustFullRefresh();
		store.setViewType(1);
		focusComp.requestFocus();
	}
	
	private void selectApplication(ActionEvent e) {
		((TotalRowTable)focusComp).setMustFullRefresh();
		store.setViewType(2);
		focusComp.requestFocus();
	}

	private void returnBack(ActionEvent e) {

		SelectorTab tab = (SelectorTab)this.getParent();
		tab.tabsSaver.cancel();
	}

	/**
	 * меняет пин-код на карте
	 * 
	 */
	private void changePin(ActionEvent e) {

		DBStoreReIssueCard dBStoreReIssueCard = (DBStoreReIssueCard) store;
		
		if (dBStoreReIssueCard.getTypeSelect() != TypeSelect.Lost.ordinal()) {
			messageError(NOT_PINCODE_FILTER);
			return;
		}

		ReIssueCardRowField field = dBStoreReIssueCard.getRow();
		if (field == null || field.getId() == null) {
			messageError(NOT_SELECTED_CARDS);
			return;
		}

		IvrInfo ivrInfo = dBStoreReIssueCard.getIvrInfo();
		if (ivrInfo == null) {
			messageError(INCORRECT_DATA);
			return;
		}

		IvrXml.startIvrProccessByDb(ivrInfo);
	}

	private void addToPacketBtrt25(ActionEvent e) {

		DBStoreReIssueCard dBStoreReIssueCard = (DBStoreReIssueCard) store;

		DBStorePacket dBStorePacket = dBStoreReIssueCard.getDBStorePacket();

		JTable tbl = (JTable) focusComp;
		int[] rows = tbl.getSelectedRows();

		if (dBStoreReIssueCard.getTypeSelect() != TypeSelect.Lost.ordinal()) {
			messageError("Отбор должен быть <По утере-краже>");
			return;
		}

		if (rows.length == 0) {
			messageError(NOT_SELECTED_CARDS);
			return;
		}

		PacketRowField field = dBStorePacket.createDefaultRecord(DBStorePacket.BTRT25_PACKET_NAME);

		DBStorePacketContent content = dBStoreReIssueCard.getDBStorePacketContent();

		List<E> data = store.getData();

		final int index = rows[0];

		PacketContentRowField fieldContent = content.createBtrt25Record(field.getId(),
				data.get(index).getId());
		dBStorePacket.updateAllData();
		dBStoreReIssueCard.updateAllData();
	}
		
	/**
	 * добавляет в пакет на отправку 
	 */
	private void addToPacket(ActionEvent e) {

		DBStoreReIssueCard dBStoreReIssueCard = (DBStoreReIssueCard)store;
		
		DBStorePacket dBStorePacket = dBStoreReIssueCard.getDBStorePacket();

		if (dBStorePacket.isWorkPlaceDopik()) {
			messageError(Cfg.path().errorDopik());
			return;
		}


		JTable tbl = (JTable)focusComp;
		int[] rows =tbl.getSelectedRows();
		
		if(rows.length == 0) {
			messageError(NOT_SELECTED_CARDS);
			return;
		}
		
		PacketRowField field = dBStorePacket.createDefaultRecord(DEF_NEW_PACKET_NAME);

		logger.info("PacketRowField.id=" + field.getId());

		DBStorePacketContent content = dBStoreReIssueCard.getDBStorePacketContent();
		
		List<E> data = store.getData();
		for (int index : rows) {

			String error = content.checkReissueCard(field.getId(), data.get(index).getId());

			if (error != null) {
				JOptionPane.showMessageDialog(null,
						error, null, JOptionPane.ERROR_MESSAGE);
			} else {
				content.createReissueCardRecord(field.getId(), data.get(index).getId(),
								store.getTypeSelect());
			}
		}
		
		dBStorePacket.updateAllData();
		dBStoreReIssueCard.updateAllData();
	}

	@Override
	protected ButtonKarkas[] getButtonKarkases() {
		return buttonKarkases;
	}
}
