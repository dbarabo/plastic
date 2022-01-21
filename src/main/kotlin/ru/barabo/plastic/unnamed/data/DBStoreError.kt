package ru.barabo.plastic.unnamed.data

import ru.barabo.afina.AfinaQuery
import ru.barabo.plastic.release.main.data.DBStorePlastic

class DBStoreError(dbStorePlastic: DBStorePlastic) : DBStoreInPath(dbStorePlastic) {
    override fun initData(): MutableList<RowFieldInPath> =
        AfinaQuery.selectCursor(SELECT_ERROR).map { createRowField<RowFieldInPath>(it) }.toMutableList()

    companion object {
        private const val SELECT_ERROR = "{ ? = call od.PTKB_PLASTIC_AUTO.getUnnamedCardsError }"
    }
}