package ru.barabo.plastic.unnamed.data

import ru.barabo.afina.AfinaQuery
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.general.ClientCriteria
import ru.barabo.plastic.unnamed.general.ClientCriteriaDBStore
import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.impl.AbstractDBStore

class DBStoreClientSelect(private val dbStorePlastic: DBStorePlastic) : AbstractDBStore<RowFieldClient>(),
    ClientCriteriaDBStore<RowFieldClient> {

    private var clientCriteria: ClientCriteria = ClientCriteria.emptyClientCriteria

    override fun setClientCriteria(clientCriteria: ClientCriteria) {
        if(clientCriteria.isEmptyData()) return

        this.clientCriteria = clientCriteria

        updateAllData()
    }

    override fun getTypeSelect(): Int = 0

    override fun setViewType(type: Int) {}

    override fun getFields(): MutableList<FieldItem> = row?.fieldItems() ?: RowFieldClient().fieldItems()

    override fun createEmptyRow(): RowFieldClient? = null

    override fun cloneRow(row: RowFieldClient?): RowFieldClient? = null

    override fun initData(): MutableList<RowFieldClient> {

        if(clientCriteria.isEmptyData()) return emptyList<RowFieldClient>().toMutableList()

        return AfinaQuery.selectCursor(SELECT_CLIENT, clientCriteria.toSqlArray() )
            .map { createRowField<RowFieldClient>(it) }.toMutableList()
    }

    override fun remove(row: RowFieldClient?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertRow(row: RowFieldClient?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateRow(oldData: RowFieldClient?, newData: RowFieldClient?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val SELECT_CLIENT = "{ ? = call od.PTKB_PLASTIC_AUTO.findClientByCriteriaUnnamed( ?, ?, ?, ?, ?, ?, ? ) }"
    }
}