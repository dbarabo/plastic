package ru.barabo.plastic.release.packet.gui;

import ru.barabo.plastic.main.resources.owner.Cfg;
import ru.barabo.plastic.release.application.data.DBStoreApplicationCard;
import ru.barabo.plastic.release.ivr.xml.IvrInfo;
import ru.barabo.plastic.release.ivr.xml.IvrXml;
import ru.barabo.plastic.release.packet.data.*;
import ru.barabo.plastic.release.reissue.gui.TopToolBarReIssueCard;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.ListenerStore;
import ru.barabo.total.db.StateRefresh;
import ru.barabo.total.gui.any.AbstractTopToolBar;
import ru.barabo.total.gui.any.ButtonKarkas;
import ru.barabo.total.gui.any.ShowMenuListener;
import ru.barabo.total.gui.table.TotalRowTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TopToolBarPacket <E extends PacketRowField> extends AbstractTopToolBar
  implements ListenerStore<E> {
	
	//final static transient private Logger logger = Logger.getLogger(TopToolBarPacket.class.getName());
	
	private static final String MSG_WAIT = "Сейчас нужно просто ждать, когда придут ответные файлы от ПЦ";
	
	private static final String MSG_GO_HOME = "Перевести карты в Головной офис?";
	private static final String TITLE_GO_HOME = "Карты в 'ГО'";
	
	private static final String MSG_TO_DOPIKI = "Отправить карты по доп. офисам?";
	private static final String TITLE_TO_DOPIKI = "Карты в Доп. офисы";
	
	private static final String MSG_TO_GET = "Перевести карты в состояние 'Получено в доп. офисах'?";
	private static final String TITLE_TO_GET = "Карты в Доп. офисах";
	
	private static final String MSG_OUT_CLIENT = "Перевести ВСЕ карты в конечное состояние 'Выдано клиенту'?";
	private static final String TITLE_OUT_CLIENT = "Карты всем клиентам";

	private static final String MSG_ERROR_NO_CONTENT = "Не Выбрано ни одного клиента в таблице клиентов";
	private static final String MSG_OUT_CLIENT_ONLY = "Перевести карту %s в конечное состояние 'Выдано клиенту'?"; 
	private static final String TITLE_OUT_CLIENT_ONLY = "Карту клиенту";
	
	private static final String REMOVE_ALL_PACKET = "Удалить весь пакет '%s' ?";
	private static final String REMOVE_TITLE_PACKET = "Удаление пакета";
	
	private static final String REMOVE_RECORD_PACKET = "Удалить запись пакета по '%s' ?";
	private static final String REMOVE_TITLE_RECORD_PACKET = "Удаление записи в пакете";
	
	private static final String MSG_GET_NEW_NAME = "Новое имя пакета";
	
	
	private AbstractButton stateButton;
	protected int stateButtonIndex;
	
	private JMenuItem stateButtonContent;
	private int stateButtonContentIndex;
	
	private boolean isMySelect;
	private FilterWork filterWork;
	
	private JComponent contentComp;
	
	private final ButtonKarkas[] menuFilterProblemType = {
			new ButtonKarkas("allFilter", "Отбор:Все", this::selectAll, 0),
			new ButtonKarkas("newFilter", "Отбор:'Новые'", this::selectNew, 0),
			new ButtonKarkas("workFilter", "Отбор:'В Работе'", this::selectWork, 0),
			new ButtonKarkas("endFilter", "Отбор:'Закрытые'", this::selectEndWork, 0),
			new ButtonKarkas("bug", "Отбор:'С Ошибками'", this::selectError, 0)
	};

	private final ButtonKarkas[] menuFilterByUser = {
			new ButtonKarkas("allUsers", "Отбор:Любые", this::selectAllUser, 1),
			new ButtonKarkas("user", "Отбор:Мои", this::selectMy, 1),
	};

	private final ButtonKarkas[] buttonKarkases = {
			new ButtonKarkas("refresh", "Обновить", this::refresh, null),
			new ButtonKarkas("deleteAll", "Удалить", this::showMenuDelete, null),
			new ButtonKarkas("rename", "Правка", this::renamePacket, null),
			new ButtonKarkas("toApplication", "Заявление", this::gotoApplication, null),
			new ButtonKarkas("changeProduct", "Сменить продукт", this::changeProduct, null),
			new ButtonKarkas("death", "BTRT25 Send", this::sendBtr25, null),
			new ButtonKarkas(null, null, null, null),
			
			new ShowMenuListener(menuFilterProblemType).createButtonKarkas(0),
			new ShowMenuListener(menuFilterByUser).createButtonKarkas(1),
			new ButtonKarkas(null, null, null, null),
			
	};
	

	private static JPopupMenu outClientPopup;
	
	private static JPopupMenu deleteRecordPopup;

	private final ButtonKarkas[] stateButtons = {
			new ButtonKarkas("mc", "Перевыпустить", this::reIssueCards, null),         //0
			new ButtonKarkas("newFile", "Отправить файл", this::createFile, null), // 1
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //2
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //3
			new ButtonKarkas("returnNew", "Вернуть на отправку", this::toCreateFileState, null),                        //4
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //5
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //6
			new ButtonKarkas("returnNew", "Вернуть на отправку", this::toCreateFileState, null),                        //7
			new ButtonKarkas("returnNew", "Вернуть на отправку", this::toCreateFileState, null),                        //8
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //9
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //10
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //11
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //12
			new ButtonKarkas("sms", "Вернуть в SMS", this::toSmsState, null),          //13
			new ButtonKarkas("home", "Карты в ГО", this::goHome, null),                //14
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //15
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //16
			new ButtonKarkas("wait", "Ждём", this::wait, null),                        //17
			new ButtonKarkas("toDopiki", "В доп. офис->", this::toDopiki, null), // 18
			new ButtonKarkas("toGet", "Получить в офисе", this::toGet, null), // 19
			new ButtonKarkas("outClient", "Выдать карту",
					this::outClientOnly/* this::showMenuOutClient */, null), // 20
			new ButtonKarkas(null, null, null, null) // 21
	};
	
	private final ButtonKarkas[] stateButtonsContent = {
			null,         //0
			null,  //1
			null,                        //2
			null,                        //3
			null,                        //4
			null,                        //5
			null,                        //6
			null,                        //7
			null,                        //8
			null,                        //9
			null,                        //10
			null,                        //11
			null,                        //12
			null,          //13
			null,                //14
			null,                        //15
			null,                        //16
			null,                        //17
			null, //18
			null, //19
			new ButtonKarkas("outClient", "Выдать карту клиенту", this::outClientOnly, null), //20
			null // 21
	};

	protected DBStore<E> store;
	
	public TopToolBarPacket(DBStore<E> store, JComponent focusComp, JComponent contentComp) {
		super(focusComp);

		this.store = store;
		
		this.contentComp = contentComp;
		
		initButton();
		
		stateButtonIndex = -1;
		stateButton = null; 
		
		stateButtonContent = null; 
		stateButtonContentIndex = -1;
		
		isMySelect = false;
		filterWork = FilterWork.ALL;
		
		DBStorePacket packet = (DBStorePacket)store;
		
		DBStorePacketContent content = packet.getDBStorePacketContentPacket();
			
		store.addListenerStore(this);
		
		if(contentComp != null) {
			ListenerContent listenerContent = new ListenerContent(this);
			content.addListenerStore(listenerContent);
		}
				
		refreshData(store.getData(), StateRefresh.ALL);
		
	}

	@Override
	protected void initButton() {
		super.initButton();

		final AbstractButton btrt25Button = getButtonKarkases()[5].getButton();

		if (btrt25Button == null)
			return;

		btrt25Button.setVisible(((DBStorePacket) store).isSuperWorkspace());
	}

	private void selectAll(ActionEvent e) {
		
		filterWork = FilterWork.ALL;
		
		((TotalRowTable) focusComp).setMustFullRefresh();
		store.setViewType(filterWork.getSelectTypes(isMySelect).ordinal());

		DBStorePacket dBStorePacket = (DBStorePacket) store;

		focusComp.requestFocus();
	}
	
	private void selectNew(ActionEvent e) {

		filterWork = FilterWork.NEW;
		
		((TotalRowTable) focusComp).setMustFullRefresh();
		store.setViewType(filterWork.getSelectTypes(isMySelect).ordinal());
		focusComp.requestFocus();
	}
	
	private void selectWork(ActionEvent e) {
		
		filterWork = FilterWork.WORK;
		
		((TotalRowTable) focusComp).setMustFullRefresh();
		store.setViewType(filterWork.getSelectTypes(isMySelect).ordinal());
		focusComp.requestFocus();
	}
	
	private void selectEndWork(ActionEvent e) {
		
		filterWork = FilterWork.END;
		
		((TotalRowTable) focusComp).setMustFullRefresh();
		store.setViewType(filterWork.getSelectTypes(isMySelect).ordinal());
		focusComp.requestFocus();
	}
	
	private void selectError(ActionEvent e) {

		filterWork = FilterWork.ERROR;

		((TotalRowTable) focusComp).setMustFullRefresh();
		store.setViewType(filterWork.getSelectTypes(isMySelect).ordinal());
		focusComp.requestFocus();
	}

	private void selectMy(ActionEvent e) {
		
		isMySelect = true;
		((TotalRowTable) focusComp).setMustFullRefresh();
		store.setViewType(filterWork.getSelectTypes(isMySelect).ordinal());
		focusComp.requestFocus();
	}
	
	private void selectAllUser(ActionEvent e) {
		
		isMySelect = false;
		((TotalRowTable) focusComp).setMustFullRefresh();
		store.setViewType(filterWork.getSelectTypes(isMySelect).ordinal());
		focusComp.requestFocus();
	}
	
	private void renamePacket(ActionEvent e) {
		String newName = JOptionPane.showInputDialog(MSG_GET_NEW_NAME);
		
		if(newName == null) return;
		
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		
		String error = dBStorePacket.renamePacket(newName);
		if(error != null) {
			TopToolBarReIssueCard.messageError(error);
		}

		focusComp.requestFocus();
	}
	
	/**
	 * перевыпуск всех карт
	 */
	private void reIssueCards(ActionEvent e) {
		
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		String result = dBStorePacket.reIssueCards();
		
		if(result != null) {
			TopToolBarReIssueCard.messageError(result);
			focusComp.requestFocus();
			return;
		}

		focusComp.requestFocus();

		List<String> emptyDesignCards = new ArrayList<>();

		String error = dBStorePacket.getEmptyDesignCard(emptyDesignCards);
		if (error != null) {
			TopToolBarReIssueCard.messageError(result);
			focusComp.requestFocus();
			return;
		}

		if (emptyDesignCards.size() > 0) {
			final String cards = String.join("\n", emptyDesignCards);

			JOptionPane.showMessageDialog(null, Cfg.msg().msgDesignCardIsEmpty(cards),
					Cfg.msg().subjDesignCardIsEmpty(), JOptionPane.INFORMATION_MESSAGE);
		}

		focusComp.requestFocus();
	}
	
	/**
	 * создание файла
	 */
	private void createFile(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		String result = dBStorePacket.createFile();
		if(result != null) {
			TopToolBarReIssueCard.messageError(result);
		}

		focusComp.requestFocus();
	}
	
	private void toGet(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		
		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Нет");
		int reply = JOptionPane.showConfirmDialog(null, 
				MSG_TO_GET, 
				TITLE_TO_GET, JOptionPane.YES_NO_OPTION);
		
		if (reply != JOptionPane.YES_OPTION) {
			focusComp.requestFocus();
			return;
		}
		
		focusComp.requestFocus();

		String error = dBStorePacket.toGetHomes();
		if(error != null) {
			TopToolBarReIssueCard.messageError(error);
		}

		focusComp.requestFocus();
	}
	
	
	private void showMenuOutClient(ActionEvent e) {
		if(outClientPopup == null) {
			outClientPopup = initPopupOutClient();
		}
		
		Component src = (Component)e.getSource();
		
		outClientPopup.show(src, 1,	src.getHeight() + 1);
	}
	
	private JMenuItem getMenuOutClientOnly() {
		
		JMenuItem out = new JMenuItem();
		out.setText("Выдать карту клиенту");
		out.addActionListener(this::outClientOnly);
		
		return out;
	}
	
	private JPopupMenu initPopupFilterProblemType(ButtonKarkas[] karkases) {
		JPopupMenu popupMenu = new JPopupMenu();

		for (ButtonKarkas karkas : karkases) {

			if (karkas.getName() == null) {
				popupMenu.addSeparator();
				continue;
			}
			JMenuItem item = new JMenuItem(karkas.getName(), karkas.getImageIco());
			item.addActionListener(karkas.getListener());
			popupMenu.add(item);
		}
		return popupMenu;
	}

	private JPopupMenu initPopupOutClient() {
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem allOut = new JMenuItem();
		allOut.setText("Выдать ВСЕ карты в пакете");
		allOut.addActionListener(this::outClient);
		popupMenu.add(allOut);

		popupMenu.add(getMenuOutClientOnly() );

		return popupMenu;
	}
	
	private void outClient(ActionEvent e) {
		
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		
		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Нет");
		int reply = JOptionPane.showConfirmDialog(null, 
				MSG_OUT_CLIENT, 
				TITLE_OUT_CLIENT, JOptionPane.YES_NO_OPTION);
		
		if (reply != JOptionPane.YES_OPTION) return;
		
		String error = dBStorePacket.outClient();
		if(error != null) {
			TopToolBarReIssueCard.messageError(error);
			return;
		}

		focusComp.requestFocus();
	}
	
	private void outClientOnly(ActionEvent e) {
		
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		
		DBStorePacketContent contentDb = dBStorePacket.getDBStorePacketContentPacket();
		
		PacketContentRowField contentRow = contentDb.getRow();
		
		if(contentRow == null || contentRow.getId() == null) {
			TopToolBarReIssueCard.messageError(MSG_ERROR_NO_CONTENT);
			return;
		}
		
		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Нет");
		int reply = JOptionPane.showConfirmDialog(null, 
				String.format(MSG_OUT_CLIENT_ONLY, contentRow.getName()), 
				TITLE_OUT_CLIENT_ONLY, JOptionPane.YES_NO_OPTION);
		
		if (reply != JOptionPane.YES_OPTION) return;
		
		String error = contentDb.outClientOnly();
		if(error != null) {
			TopToolBarReIssueCard.messageError(error);
			return;
		}

		if (!dBStorePacket.isTestBaseConnect()) {
			IvrInfo ivrInfo = contentDb.getIvrInfo();

			if (ivrInfo != null) {
				IvrXml.startIvrProccessByDb(ivrInfo);
			}
		}

		focusComp.requestFocus();
	}

	private void toDopik(ActionEvent e) {
		if (e == null || !(e.getSource() instanceof AbstractButton)) {
			return;
		}

		final AbstractButton menuDopik = (AbstractButton) e.getSource();

		String dopic = menuDopik.getText();

		if (dopic.indexOf(SEND_IN_DOPIC) != 0) {
			TopToolBarReIssueCard.messageError("Не выбрано подразделение!");
			return;
		}

		dopic = dopic.substring(SEND_IN_DOPIC.length()).trim();

		DBStorePacket dBStorePacket = (DBStorePacket) store;

		String error = dBStorePacket.toDopik(dopic);
		if (error != null) {
			TopToolBarReIssueCard.messageError(error);
		}

		dBStorePacket.updateAllData();
		focusComp.requestFocus();
	}

	static private final String SEND_IN_DOPIC = "Отправить в ";

	private JPopupMenu initPopupToDopiki() {
		DBStorePacket dBStorePacket = (DBStorePacket) store;

		Set<String> dopikiInPacket = dBStorePacket.getDopikiInPacket();

		if (dopikiInPacket == null) {
			return null;
		}

		JPopupMenu popupMenu = new JPopupMenu();

		for (String dopik : dopikiInPacket) {
			JMenuItem menuDopik = new JMenuItem();
			menuDopik.setText(SEND_IN_DOPIC + dopik);
			menuDopik.addActionListener(this::toDopik);
			popupMenu.add(menuDopik);
		}

		return popupMenu;
	}
		
	
	private void toDopiki(ActionEvent e) {
		
		JPopupMenu dopikMenu = initPopupToDopiki();

		Component src = (Component) e.getSource();

		if(dopikMenu != null) {
			dopikMenu.show(src, 1, src.getHeight() + 1);
		}
	}
	
	private void toSmsState(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		String result = dBStorePacket.toSmsState();
		if(result != null) {
			TopToolBarReIssueCard.messageError(result);
		}
		dBStorePacket.updateAllData();

		focusComp.requestFocus();
	}
	
	private void toCreateFileState(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		String result = dBStorePacket.toCreateFileState();
		if(result != null) {
			TopToolBarReIssueCard.messageError(result);
		}
		dBStorePacket.updateAllData();

		focusComp.requestFocus();
	}

	
	private void showMenuDelete(ActionEvent e) {
		if(deleteRecordPopup == null) {
			deleteRecordPopup = initDelete();
		}
		
		Component src = (Component)e.getSource();
		
		deleteRecordPopup.show(src, 1, src.getHeight() + 1);
	}
	
	private JPopupMenu initDelete() {
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem alldel = new JMenuItem();
		alldel.setText("Удалить весь пакет");
		alldel.addActionListener(this::deleteAll);
		popupMenu.add(alldel);
		
		JMenuItem dell = new JMenuItem();
		dell.setText("Удалить запись из пакета");
		dell.addActionListener(this::deleteOne);
		popupMenu.add(dell);

		return popupMenu;
	}
	
	private JTabbedPane getMainBook() {

		Container findBook = this.getParent();

		while (findBook != null && (!(findBook instanceof JTabbedPane))) {
			findBook = findBook.getParent();
		}

		return (JTabbedPane) findBook;
	}

	private void sendBtr25(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket) store;

		String error = dBStorePacket.sendBrt25();
		if (error != null) {
			TopToolBarReIssueCard.messageError(error);
		}
	}

	private void changeProduct(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket) store;

		List<String> fromProducts = new ArrayList<String>();

		String error = dBStorePacket.getChangeFromProductItems(fromProducts);
		if (error != null) {
			TopToolBarReIssueCard.messageError(error);
			return;
		}

		if (fromProducts.size() != 0) {
			dialogChangeProduct(fromProducts);
		}
	}

	private String getDialogProductFrom(List<String> fromProducts) {

		final JComboBox<String> combo = new JComboBox<String>();
		fromProducts.forEach(combo::addItem);

		String[] buttons = { "OK", "Отмена" };

		String title = "Выберите изменяемый (старый) продукт";
		final int selection = JOptionPane.showOptionDialog(null, combo, title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				buttons, buttons[0]);

		if (selection != 0) {
			return null;
		}

		return (String) combo.getSelectedItem();
	}

	private String getDialogProductTo(List<String> toProducts) {

		final JComboBox<String> combo = new JComboBox<String>();
		toProducts.forEach(combo::addItem);

		String[] buttons = { "OK", "Отмена" };

		String title = "Выберите Новый продукт";
		final int selection = JOptionPane.showOptionDialog(null, combo, title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				buttons, buttons[0]);

		if (selection != 0) {
			return null;
		}

		return (String) combo.getSelectedItem();
	}

	private void dialogChangeProduct(List<String> fromProducts) {

		String fromProduct = fromProducts.size() == 1 ? fromProducts.get(0) : null;

		if (fromProduct == null) {
			fromProduct = getDialogProductFrom(fromProducts);
		}

		if (fromProduct == null) {
			return;
		}

		DBStorePacket dBStorePacket = (DBStorePacket) store;

		List<String> toProducts = new ArrayList<String>();
		String error = dBStorePacket.getChangeToProductItems(fromProduct, toProducts);
		if (error != null) {
			TopToolBarReIssueCard.messageError(error);
			return;
		}

		if (toProducts.size() == 0) {
			TopToolBarReIssueCard.messageError("Невозможно сменить этот продукт");
			return;
		}

		String toProduct = toProducts.size() == 1 ? toProducts.get(0) : null;

		if (toProduct == null) {
			toProduct = getDialogProductTo(toProducts);
		}

		if (toProduct == null) {
			return;
		}

		error = dBStorePacket.changeProduct(fromProduct, toProduct);
		if (error != null) {
			TopToolBarReIssueCard.messageError(error);
			return;
		}

		DBStorePacketContent contentDb = dBStorePacket
				.getDBStorePacketContentPacket();

		contentDb.setCursor(dBStorePacket.getRow());
	}

	private void gotoApplication(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket) store;

		DBStorePacketContent contentDb = dBStorePacket
				.getDBStorePacketContentPacket();

		PacketContentRowField contentRow = contentDb.getRow();

		if (contentRow == null || contentRow.getId() == null) {
			TopToolBarReIssueCard.messageError(MSG_ERROR_NO_CONTENT);
			return;
		}

		DBStoreApplicationCard applicationDB = dBStorePacket
				.getDBStoreApplicationCard();

		applicationDB.setViewType(contentRow.getApplicationId());

		JTabbedPane mainBook = getMainBook();
		if (mainBook == null) {
			return;
		}

		mainBook.setSelectedIndex(mainBook.getTabCount() - 1);
	}
	
	private void deleteOne(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		
		DBStorePacketContent contentDb = dBStorePacket.getDBStorePacketContentPacket();
		
		PacketContentRowField contentRow = contentDb.getRow();
		
		if(contentRow == null || contentRow.getId() == null) {
			TopToolBarReIssueCard.messageError(MSG_ERROR_NO_CONTENT);
			return;
		}
				
		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Нет");
		int reply = JOptionPane.showConfirmDialog(null, 
				String.format(REMOVE_RECORD_PACKET, contentRow.getName()), 
				REMOVE_TITLE_RECORD_PACKET, JOptionPane.YES_NO_OPTION);
		
		if (reply != JOptionPane.YES_OPTION) return;
		
		contentDb.removeRow();

		focusComp.requestFocus();
	}
	
	private void deleteAll(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket)store;
				
		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Нет");
		int reply = JOptionPane.showConfirmDialog(null, 
				String.format(REMOVE_ALL_PACKET, store.getRow().getName()), 
				REMOVE_TITLE_PACKET, JOptionPane.YES_NO_OPTION);
		
		if (reply != JOptionPane.YES_OPTION) return;
		
		String error = dBStorePacket.removePacket();
		if(error != null) {
			TopToolBarReIssueCard.messageError(error);
		}

		focusComp.requestFocus();
	}
	
	protected void wait(ActionEvent e) {
	
		JOptionPane.showMessageDialog(null, 
				MSG_WAIT,  null, JOptionPane.INFORMATION_MESSAGE );

		focusComp.requestFocus();
	}
	
	
	private void goHome(ActionEvent e) {
		
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		
		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Нет");
		int reply = JOptionPane.showConfirmDialog(null,
				MSG_GO_HOME, TITLE_GO_HOME, JOptionPane.YES_NO_OPTION);
		
		if (reply != JOptionPane.YES_OPTION) return;
		
		String error = dBStorePacket.goHomeState();
		if(error != null) {
			TopToolBarReIssueCard.messageError(error);
		}

		focusComp.requestFocus();
	}
	
	
	private void refresh(ActionEvent e) {
		DBStorePacket dBStorePacket = (DBStorePacket)store;
		dBStorePacket.updateAllData();
		dBStorePacket.sendListenersRefreshAllData(StateRefresh.ALL);

		focusComp.requestFocus();
	}

	@Override
	protected ButtonKarkas[] getButtonKarkases() {

		return buttonKarkases;
	}

	/////////////ListenerStore<PacketRowField>//////////////
	@Override
	public void setCursor(E row) {
		
		refreshButtons(row);
	}
	
	private boolean isRemoveStateButton(PacketRowField row) {
		
		if(stateButton != null && (row == null ||  row.getState() != stateButtonIndex) ) {
			
			//logger.info("remove" + stateButton.getText());
			
			stateButton.setEnabled(false);
			this.remove(stateButton);
			stateButtonIndex = -1;
			stateButton = null;
			
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isRemoveStateButtonContent(PacketContentRowField row) {
		if(contentComp == null) return false;
			
		if( stateButtonContent != null && 
		   (row == null ||  row.getState() != stateButtonContentIndex) ) {
			
			JPopupMenu popup = contentComp.getComponentPopupMenu();
			
			popup.remove(stateButtonContent);
			stateButtonContentIndex = -1;
			stateButtonContent = null;
			
			return true;
		} else {
			return false;
		}
	}
	
	private ButtonKarkas getStateButtonContent(int index) {
		return index < 0 ? null : stateButtonsContent[index];
	}
	
	private JMenuItem createMenuItem(ButtonKarkas button) {
		
		JMenuItem menu = new JMenuItem(button.getName() );
		
		menu.setText(button.getName() );
		
		menu.addActionListener(button.getListener());
		
		return menu;
	}
	
	/**
	 * кнопка - зависит от состояния
	 */
	void refreshStateContentButton(PacketContentRowField row) {
		
		//logger.info("row=" + row);

		isRemoveStateButtonContent(row);

		if(row == null || row.getState() == -1 ||
				row.getState() > getStateButtonsCount() || row.getState() == stateButtonContentIndex) return;
		
		stateButtonContentIndex = row.getState();

		ButtonKarkas button = getStateButtonContent(stateButtonContentIndex);
		
		if(button != null) {
			stateButtonContent = createMenuItem(button);
		}
				
		if(stateButtonContent != null) {
			if(contentComp == null) return;
			
			JPopupMenu popup = contentComp.getComponentPopupMenu();
			popup.add(stateButtonContent);
			popup.repaint();
		}
	}
	
	protected ButtonKarkas getStateButton(int stateButtonIndex) {
		return stateButtons[stateButtonIndex];
	}
	
	protected int getStateButtonsCount() {
		return stateButtons.length;
	}
	
	/**
	 * кнопка - зависит от состояния
	 */
    private void refreshStateButton(PacketRowField row) {
		
		//logger.info("row=" + row);
		
		boolean isRemove = isRemoveStateButton(row);
		
		if(row == null || row.getState() == -1 || 
				row.getState() > getStateButtonsCount() || row.getState() == stateButtonIndex) return;
		
		stateButtonIndex = row.getState();
		
		AbstractButton oldButton = stateButton;
		
		ButtonKarkas button = getStateButton(stateButtonIndex);
		
		if(button != null) {
			stateButton = createButton(button);
		}
				
		if(stateButton != null) {
			//logger.info("add " + stateButton.getText());
			this.add(stateButton);
			this.repaint();
		} else if(oldButton != null){
			//logger.info("disable " + oldButton);
			oldButton.setEnabled(false);
			this.repaint();
		} else if(isRemove) {
			this.repaint();
		}
	}
	
	private void refreshButtons(PacketRowField row) {
		refreshStateButton(row);
		refreshDeleteButtons(row);
	}
	
	private void refreshDeleteButtons(PacketRowField row) {
		
		AbstractButton deleteAllButton = getButtonKarkases()[1].getButton();
		
		if(deleteAllButton == null) return;
		
		if (row == null || (row.getState() != 0 && row.getState() != 1)) {
			deleteAllButton.setEnabled(false);
		} else {
			deleteAllButton.setEnabled(true);
		}
	}

	@Override
	public void refreshData(List<E> allData, StateRefresh stateRefresh) {
		
		E row = allData.size() == 0 ? null : allData.get(0);
		
		refreshButtons(row );
	}
}

enum FilterWork {
	ALL, 
	NEW,
	WORK,
	END,
	ERROR;
	
	public SelectTypes getSelectTypes(boolean isMy) {
		
		return SelectTypes.values()[this.ordinal() * 2 + (isMy ? 1 : 0)];
	}
}
