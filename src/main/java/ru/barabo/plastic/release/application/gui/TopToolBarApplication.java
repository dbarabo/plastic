package ru.barabo.plastic.release.application.gui;


import org.apache.log4j.Logger;
import ru.barabo.plastic.gui.PlasticGui;
import ru.barabo.plastic.release.application.data.AppCardRowField;
import ru.barabo.plastic.release.application.data.DBStoreApplicationCard;
import ru.barabo.plastic.release.reissue.gui.TopToolBarReIssueCard;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.ListenerStore;
import ru.barabo.total.db.StateRefresh;
import ru.barabo.total.gui.any.AbstractTopToolBar;
import ru.barabo.total.gui.any.ButtonKarkas;
import ru.barabo.total.gui.any.ShowMenuListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TopToolBarApplication<E extends AppCardRowField> extends AbstractTopToolBar
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
			new ButtonKarkas("sendPaket", "Отправить в ПЦ", this::sendToPC, null),
			new ShowMenuListener(printApplication).createButtonKarkas(0)
	};
	
	private final static String[] STATES_TO_SENT = {
			"Создан",
			"Документы зарегистрированы",
			"Документы проверены"
	};

	private DBStore<E> store;

	TopToolBarApplication(DBStore<E> store, JComponent focusComp) {
		super(focusComp);

		this.store = store;

		initButton();

        refreshEnableToStateButton(null);

		store.addListenerStore(this);
	}

	private void newApplication(ActionEvent e) {

		PlasticGui.openApplicationForm(-1);
		store.getRow();
	}

    private void sendToPC(ActionEvent e) {
        String error = ((DBStoreApplicationCard) store).sendToPcApplication();

        if (error != null) {
            TopToolBarReIssueCard.messageError(error);
        }
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

    @Override
    public void refreshData(List<E> allData, StateRefresh stateRefresh) {
        refreshEnableToStateButton(store.getRow());

    }

	private void refreshEnableToStateButton(AppCardRowField row) {

        refreshToSentApplicationButton(row);

        refreshSendToPC(row);
	}

	private void refreshSendToPC(AppCardRowField row) {
        AbstractButton sendToPcButton = getButtonKarkases()[3].getButton();

        if (sendToPcButton == null) return;

        boolean isEnabled = row != null &&
                TO_SENT_APPLICATION_STATE_LABEL.equalsIgnoreCase(
                        row.getFieldByLabel(AppCardRowField.FIELD_STATE_NAME).getValueField());

        sendToPcButton.setEnabled(isEnabled);
    }

    final static private String TO_SENT_APPLICATION_STATE_LABEL = "На отправку";

    private void refreshToSentApplicationButton(AppCardRowField row) {
        AbstractButton toSentButton = getButtonKarkases()[2].getButton();

        if (toSentButton == null) return;

        boolean isEnabled = row != null &&
                isStateSent(row.getFieldByLabel(AppCardRowField.FIELD_STATE_NAME)
                        .getValueField());

        toSentButton.setEnabled(isEnabled);
    }

    private boolean isStateSent(String stateLabel) {

        for (String stateName : STATES_TO_SENT) {
            if (stateName.equals(stateLabel)) {
                return true;
            }
        }
        return false;
    }


}
