package ru.barabo.plastic.unnamed.data

import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.release.main.data.DBStorePlastic

class DBStoreInHome(dbStorePlastic: DBStorePlastic) : DBStoreInPath(dbStorePlastic) {

    override fun initData(): MutableList<RowFieldInPath> =
        AfinaQuery.selectCursor(SELECT_IN_HOME).map { createRowField<RowFieldInPath>(it) }.toMutableList()

    companion object {
        private const val SELECT_IN_HOME = "{ ? = call od.PTKB_PLASTIC_AUTO.getUnnamedCardsInHome }"
    }
}