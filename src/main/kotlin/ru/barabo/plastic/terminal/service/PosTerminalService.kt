package ru.barabo.plastic.terminal.service

import ru.barabo.afina.AfinaOrm
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.entity.selector.SelectClient
import ru.barabo.plastic.terminal.entity.PercentRateTerminal
import ru.barabo.plastic.terminal.entity.PosTerminal
import java.time.ZoneId
import java.util.*

object PosTerminalService : StoreFilterService<PosTerminal>(AfinaOrm, PosTerminal::class.java) {

    fun setFilterByAny(filterValue: String) {
        if(filterValue.trim().isEmpty()) {
            resetFilter()
            return
        }

        setFilter { createAllFieldCriteria(it, filterValue.trim().uppercase(Locale.getDefault())) }
    }

    fun changePercentRate(rate: PercentRateTerminal?) {

        val entity = selectedEntity() ?: throw Exception("Не выбран текущий терминал")

        if(entity.id == null) throw Exception("Не выбран текущий терминал")

        if(rate?.id == null) throw Exception("Нельзя менять %% ставку на пустую")

        entity.rateName = rate.name

        entity.percentId = rate.id

        entity.percentCommission = null

        save(entity)
    }

    fun editAccountTerminal(terminalId: String, selectAccount: SelectAccount?) {

        val accountId = selectAccount?.id ?: throw Exception("Не выбран расчетный счет")

        orm.executeQuery(UPDATE_ACCOUNT_TERMINAL, arrayOf(terminalId.uppercase(Locale.getDefault()), accountId) )

        initData()
    }

    fun createTerminal(terminalId: String?, selectAccount: SelectAccount?, rate: PercentRateTerminal?,
                       pactStart: java.util.Date?, address: String?, serialNumber: String?) {

        val accountId = selectAccount?.id ?: throw Exception("Не выбран расчетный счет")

        val rateId = rate?.id ?: throw Exception("Не выбрана %% ставка")

        val terminal =  terminalId?.trim()?.uppercase(Locale.getDefault()) ?: throw Exception("Не указан ID терминала")

        val startPact = pactStart?.let { java.sql.Date(it.time) } ?: java.sql.Date::class.javaObjectType

        val addressTerminal = address ?: ""

        val serial = serialNumber ?: ""

        orm.executeQuery(CREATE_TERMINAL, arrayOf(terminal, rateId, accountId, startPact, addressTerminal, serial) )

        initData()
    }

    fun createExternalTerminal(terminalId: String?, rate: PercentRateTerminal?, address: String?,
                               selectBank: SelectClient?, selectClient: SelectClient?,
                               extCodeAccount: String?, serialNumber: String?) {

        val extBankId = selectBank?.id ?: throw Exception("Не выбран банк")

        val extClientId = selectClient?.id ?: throw Exception("Не выбран клиент")

        val extCode = extCodeAccount?.trim() ?: throw Exception("Не выбран расчетный счет")

        val rateId = rate?.id ?: throw Exception("Не выбрана %% ставка")

        val terminal =  terminalId?.trim()?.uppercase(Locale.getDefault()) ?: throw Exception("Не указан ID терминала")

        val addressTerminal = address ?: ""

        val serial = serialNumber ?: ""

        orm.executeQuery(CREATE_EXTERNAL_TERMINAL,
            arrayOf(terminal, extClientId, extBankId, extCode, rateId, addressTerminal, serial) )

        initData()
    }

    fun setOpenPact(pactStart: java.util.Date?) {

        val entity = selectedEntity() ?: throw Exception("Не выбран текущий терминал")

        entity.pactStart = pactStart?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

        save(entity)
    }

    fun setClosePact(pactClose: java.util.Date?) {

        val entity = selectedEntity() ?: throw Exception("Не выбран текущий терминал")

        entity.pactEnd = pactClose?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

        save(entity)
    }

    private const val CREATE_EXTERNAL_TERMINAL = "{ call od.PTKB_TRANSACT_FUNC.createExternalTerminal(?, ?, ?, ?, ?, ?, ?) }"

    private const val CREATE_TERMINAL = "{ call od.PTKB_TRANSACT_FUNC.createPosTerminal(?, ?, ?, ?, ?, ?) }"

    private const val UPDATE_ACCOUNT_TERMINAL = "{ call od.PTKB_TRANSACT_FUNC.updateAccountTerminal(?, ?) }"

    private fun createAllFieldCriteria(entity: PosTerminal, filterUpper: String): Boolean {
        if(entity.terminal.uppercase(Locale.getDefault()).indexOf(filterUpper) >= 0) return true

        if(entity.clientName.uppercase(Locale.getDefault()).indexOf(filterUpper) >= 0) return true

        if(filterUpper.length >= 4 && entity.pactStartFormat.uppercase(Locale.getDefault()).indexOf(filterUpper) >= 0) return true

        if(filterUpper.length >= 4 && entity.pactEndFormat.uppercase(Locale.getDefault()).indexOf(filterUpper) >= 0) return true

        if(filterUpper.length >= 4 && entity.lastOperFormat.uppercase(Locale.getDefault()).indexOf(filterUpper) >= 0) return true

        if(entity.rateName.uppercase(Locale.getDefault()).indexOf(filterUpper) >= 0) return true

        if(filterUpper.length >= 4 && entity.addressTerminal.uppercase(Locale.getDefault()).indexOf(filterUpper) >= 0) return true

        if(filterUpper.length >= 4 && entity.accountCode.uppercase(Locale.getDefault()).indexOf(filterUpper) >= 0) return true

        if((entity.percentCommission?.toString()?.indexOf(filterUpper) ?: -1) >= 0) return true

        return false
    }
}
