package ru.barabo.plastic.gui

import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.release.application.data.AppCardRowField
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.total.db.DetailFieldItem
import ru.barabo.total.gui.detail.FactoryComponent
import ru.barabo.total.gui.detail.TableSheetForm
import java.awt.Container
import javax.swing.JComboBox
import javax.swing.JTabbedPane

object PlasticGui {

    var formReleaseApplication: TableSheetForm<AppCardRowField>? = null

    private fun getAccountFieldItem(): DetailFieldItem? {
        return formReleaseApplication?.getFieldItemByLabel("Счет")
    }

    @JvmStatic
    fun updateAccountByField(cursorField: AppCardRowField) {
        val state =  cursorField.getFieldByLabel("#Состояние").valueField ?: ""

        if(state.isNotEmpty() && state != "Создан") return

        val applicationId = cursorField.getFieldByLabel("#id").`val` as? Number

        applicationId?.apply { reloadComboAccountList(this) }
                     ?: reloadComboByCustomer(cursorField)
    }

    private fun reloadComboByCustomer(cursorField: AppCardRowField) {

        val customerId = cursorField.getFieldByLabel(AppCardRowField.CUSTOMER_ID).`val` as? Number ?: return

        val cardType = cursorField.getFieldByLabel(AppCardRowField.PRODUCTCARD_FIELD).`val` as? Number ?: return

        val accountItem = getAccountFieldItem() ?: return

        val list = AfinaQuery.selectCursor(SEL_ACCOUNTS_CUSTOMER, arrayOf(customerId, cardType))
            .map { it[0] as? String ?: "" }

        accountItem.listField = list.toTypedArray()
        FactoryComponent.setListItemsT(accountItem.component as JComboBox<String>, list, "")
    }

    @JvmStatic
    fun openApplicationForm(idApplication: Number?) {

        reloadComboAccountList(idApplication)

        val applicationStore = DBStorePlastic.getInstance().applicationCard
        applicationStore.setViewType(idApplication?.toInt() ?: -1)

        val mainBook = getMainBook() ?: return
        mainBook.selectedIndex = mainBook.tabCount - 1
    }

    private fun reloadComboAccountList(idApplication: Number?) {

        val listAccounts = getListAccount(idApplication)

        val selAccount = if(listAccounts == emptyListString)""
                                else AfinaQuery.select(SEL_ACCOUNT_APP, arrayOf(idApplication))[0][0] as? String

        val accountItem = getAccountFieldItem() ?: return

        accountItem.listField = listAccounts.toTypedArray()
        FactoryComponent.setListItemsT(accountItem.component as JComboBox<String>, listAccounts, selAccount)
    }

    private fun getListAccount(idApplication: Number?): MutableList<String> {

        if(idApplication == null || idApplication.toInt() == -1) return emptyListString

        val list = AfinaQuery.selectCursor(SEL_ACCOUNTS_APPLICATION, arrayOf(idApplication))

        if(list.isEmpty()) return emptyListString

        return list.map { it[0] as? String ?: "" }.toMutableList()
    }

    private val emptyListString = mutableListOf("")

    private const val SEL_ACCOUNT_APP = "select od.PTKB_PLASTIC_AUTO.getAccountCodeByApplication( ? ) from dual"

    private const val SEL_ACCOUNTS_APPLICATION = "{ ? = call od.PTKB_PLASTIC_AUTO.getAccountsCardByApplication( ? ) }"

    private const val SEL_ACCOUNTS_CUSTOMER = "{ ? = call od.PTKB_PLASTIC_AUTO.getAccountsCardByCustomer( ?, ? ) }"

    private fun getMainBook(): JTabbedPane? {

        var findBook: Container? = formReleaseApplication?.parent

        while (findBook != null && findBook !is JTabbedPane) {
            findBook = findBook.parent
        }

        return findBook as JTabbedPane?
    }
}