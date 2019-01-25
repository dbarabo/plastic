package ru.barabo.plastic.schema.service.account

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreFilterService
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.schema.entity.account.*

object TerminalService : StoreFilterService<Terminal>(AfinaOrm,  Terminal::class.java), StoreListener<List<Account>> {

    init {
        AccountService.addListener( this)
    }

    override fun refreshAll(elemRoot: List<Account>, refreshType: EditType) {
        if(refreshType == EditType.CHANGE_CURSOR || refreshType == EditType.EDIT) {
            val account = AccountService.selectedEntity() ?: return

            if (!account.isCheckTerminal && !account.isCheckBankomat) return

            if (account.isCheckTerminal && account.isCheckBankomat) {
                TerminalService.resetFilter()
            } else {
                TerminalService.setFilter { it.isPos == account.isCheckTerminal}
            }
        }

    }
}

object CurrencyService: StoreFilterService<Currency>(AfinaOrm, Currency::class.java)

object OfficeService : StoreFilterService<Office>(AfinaOrm,  Office::class.java)

object ClientTypeService : StoreFilterService<ClientType>(AfinaOrm,  ClientType::class.java)