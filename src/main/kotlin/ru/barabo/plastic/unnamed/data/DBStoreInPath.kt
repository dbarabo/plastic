package ru.barabo.plastic.unnamed.data

import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.total.db.FieldItem
import ru.barabo.total.db.impl.AbstractFilterStore
import ru.barabo.total.db.impl.AbstractRowFields

open class DBStoreInPath(private val dbStorePlastic: DBStorePlastic) : AbstractFilterStore<RowFieldInPath>() {

    override fun getTypeSelect(): Int = 0

    override fun setViewType(type: Int) {}

    override fun getFields(): MutableList<FieldItem> = row?.fieldItems() ?: RowFieldInPath().fieldItems()

    override fun createEmptyRow(): RowFieldInPath? = null

    override fun cloneRow(row: RowFieldInPath?): RowFieldInPath? = null

    override fun initData(): MutableList<RowFieldInPath> =
        AfinaQuery.selectCursor(SELECT_INPATH).map { createRowField<RowFieldInPath>(it) }.toMutableList()

    override fun remove(row: RowFieldInPath?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertRow(row: RowFieldInPath?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateRow(oldData: RowFieldInPath?, newData: RowFieldInPath?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val SELECT_INPATH = "{ ? = call od.PTKB_PLASTIC_AUTO.getUnnamedCards }"
    }
}

inline fun <reified T: AbstractRowFields> createRowField(row: Array<Any?>): T = AbstractRowFields.create(row, T::class.java)
