package ru.barabo.plastic.unnamed.data

import org.apache.log4j.Logger
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.general.FilteredStoreInHome
import ru.barabo.plastic.unnamed.general.ResultOutClient

class DBStoreInHome(dbStorePlastic: DBStorePlastic) : DBStoreInPath(dbStorePlastic),
    FilteredStoreInHome<RowFieldInPath> {

    private val logger = Logger.getLogger(DBStoreInHome::class.java.name)

    override fun outCardToClient(resultOutClient: ResultOutClient) {

        logger.info("resultOutClient.clientId=${resultOutClient.clientId}")
        logger.info("resultOutClient.contentId=${resultOutClient.contentId}")
        logger.info("resultOutClient.phone=${resultOutClient.phone}")
        logger.info("resultOutClient.codeWord=${resultOutClient.codeWord}")
    }

    override fun initData(): MutableList<RowFieldInPath> =
        AfinaQuery.selectCursor(SELECT_IN_HOME).map { createRowField<RowFieldInPath>(it) }.toMutableList()

    companion object {
        private const val SELECT_IN_HOME = "{ ? = call od.PTKB_PLASTIC_AUTO.getUnnamedCardsInHome }"
    }
}