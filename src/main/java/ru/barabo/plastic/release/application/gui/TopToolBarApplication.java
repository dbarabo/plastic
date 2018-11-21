package ru.barabo.plastic.release.application.gui;


import org.apache.log4j.Logger;
import ru.barabo.plastic.release.application.data.AppCardRowField;
import ru.barabo.plastic.release.application.data.DBStoreApplicationCard;
import ru.barabo.plastic.release.reissue.gui.TopToolBarReIssueCard;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.ListenerStore;
import ru.barabo.total.gui.any.AbstractTopToolBar;
import ru.barabo.total.gui.any.ButtonKarkas;
import ru.barabo.total.gui.any.ShowMenuListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TopToolBarApplication<E extends AppCardRowField> extends AbstractTopToolBar<E>
		implements ListenerStore<E> {
	
	final static transient private Logger logger = Logger.getLogger(TopToolBarApplication.class
			.getName());

	private final ButtonKarkas[] printApplication = {

			new ButtonKarkas("print", "Печать: Заявление на кредит", this::printApplication, 0)
	};

	private final ButtonKarkas[] buttonKarkases = {
			new ButtonKarkas("newFile", "Новый", this::newApplication, null),
			new ButtonKarkas("saveDB", "Сохранить", this::saveApplication, null),
			new ButtonKarkas("toSent", "На отправку", this::toSentApplication, null),
			new ShowMenuListener(printApplication).createButtonKarkas(0)
	};
	
	private final static String[] STATES_TO_SENT = {
			"Создан",
			"Документы зарегистрированы",
			"Документы проверены"
	};

	TopToolBarApplication(DBStore<E> store, JComponent focusComp) {
		super(store, focusComp);

		initButton();

		store.addListenerStore(this);
	}

	private void newApplication(ActionEvent e) {

		store.setViewType(-1);

		store.getRow();
	}

	private void toSentApplication(ActionEvent e) {

		String error = ((DBStoreApplicationCard) store).toSentApplication();

		if (error != null) {
			TopToolBarReIssueCard.messageError(error);
		}
	}

	private void printApplication(ActionEvent e) {
		String error = ((DBStoreApplicationCard) store).printApplication();

		if (error != null) {
			TopToolBarReIssueCard.messageError(error);
		}
	}

	private void saveApplication(ActionEvent e) {

		String error = ((DBStoreApplicationCard) store).saveApplication();

		if (error != null) {
			TopToolBarReIssueCard.messageError(error);
		}
	}

	@Override
	protected ButtonKarkas[] getButtonKarkases() {
		return buttonKarkases;
	}

	@Override
	public void setCursor(E row) {
		refreshEnableToStateButton(store.getRow());

	}

	private boolean isStateSent(String stateLabel) {

		logger.info("isStateSent=" + stateLabel);

		for (String stateName : STATES_TO_SENT) {
			if (stateName.equals(stateLabel)) {
				return true;
			}
		}

		return false;
	}

	private void refreshEnableToStateButton(AppCardRowField row) {

		AbstractButton toSentButton = getButtonKarkases()[2].getButton();

		logger.info("toSentButton=" + toSentButton + " row=" + row);

		if (toSentButton == null)
			return;

		if (row == null
				|| !(isStateSent(row.getFieldByLabel(AppCardRowField.FIELD_STATE_NAME)
						.getValueField()))) {
			toSentButton.setEnabled(false);
		} else {
			toSentButton.setEnabled(true);
		}
	}

	@Override
	public void refreshData(List<E> allData) {
		refreshEnableToStateButton(store.getRow());

	}

}
