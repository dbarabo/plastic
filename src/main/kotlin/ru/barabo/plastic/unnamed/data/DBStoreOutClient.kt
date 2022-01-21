package ru.barabo.plastic.unnamed.data

import ru.barabo.afina.AfinaQuery
import ru.barabo.db.SessionException
import ru.barabo.plastic.gui.PlasticGui
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.general.FilteredStoreOutClient
import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.impl.AbstractFilterStore

class DBStoreOutClient(private val dbStorePlastic: DBStorePlastic) : AbstractFilterStore<RowFieldOutClient>(),
    FilteredStoreOutClient<RowFieldOutClient> {

    override fun selectApplicationStore() {

        val applicationId =  row.getApplicationId()?.toInt() ?: throw SessionException(MSG_ERROR_NO_CONTENT)

        PlasticGui.openApplicationForm(applicationId)
    }

    override fun changePinCode() {
        sendIvr(row.id)

        updateAllData()
    }

    override fun getTypeSelect(): Int = 0

    override fun setViewType(type: Int) {}

    override fun getFields(): MutableList<FieldItem> = row?.fieldItems() ?: RowFieldOutClient().fieldItems()

    override fun createEmptyRow(): RowFieldOutClient? = null

    override fun cloneRow(row: RowFieldOutClient?): RowFieldOutClient? = null

    override fun initData(): MutableList<RowFieldOutClient> =
        AfinaQuery.selectCursor(SELECT_OUT_CLIENT).map { createRowField<RowFieldOutClient>(it) }.toMutableList()

    override fun remove(row: RowFieldOutClient?) {}

    override fun insertRow(row: RowFieldOutClient?) {}

    override fun updateRow(oldData: RowFieldOutClient?, newData: RowFieldOutClient?) {}

    companion object {
        private const val SELECT_OUT_CLIENT = "{ ? = call od.PTKB_PLASTIC_AUTO.getUnnamedCardsOut }"
    }
}